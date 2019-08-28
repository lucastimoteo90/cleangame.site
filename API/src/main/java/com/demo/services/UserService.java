package com.demo.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.demo.domain.Room;
import com.demo.domain.User;
import com.demo.repositories.RoomRepository;
import com.demo.repositories.UserRepository;
import com.demo.security.UserSS;
import com.demo.services.exception.AuthorizationException;
import com.demo.services.exception.ObjectNotFoundException;

@Service
public class UserService {

	@Autowired
	private BCryptPasswordEncoder bcrypt;

	@Autowired
	private UserRepository repository;
	
	@Autowired
	private RoomRepository repositoryRoom;
	
	@Autowired RoomService roomService;

	public static UserSS authenticated() {
		try {
			return (UserSS) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		} catch (Exception e) {
			return null;
		}
	}

	public List<User> findAll() {
		return repository.findAll();
	}

	
	public User findById(Integer id) throws ObjectNotFoundException {
		UserSS user = UserService.authenticated();
		if(user == null || !id.equals(user.getID())) {
			//throw new AuthorizationException("DENY ACCESS");
		}		
		
		Optional<User> obj = repository.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException("User not find"));
	}
	
	public User getDataLogedUser() throws ObjectNotFoundException {
		UserSS user = UserService.authenticated();
		if(user == null) {
			throw new AuthorizationException("Token Inválido");
		}		
		
		Optional<User> obj = repository.findById(user.getID());
		//Omitir passwd na resposta
		
		obj.get().setPasswd(null);
		
		//Corrigir falha loop json
		obj.get().setRoomsAdministrator(null);
		obj.get().setRoomsMember(null);
		return obj.orElseThrow(() -> new ObjectNotFoundException("Token Inválido, User not find"));
	}
	
	public List<?>findRoomsAdmin(){
		UserSS user_ss = UserService.authenticated();
		if(user_ss == null) {
			throw new AuthorizationException("Token Inválido");
		}
		
		Optional<User> user = repository.findById(user_ss.getID());
		return user.get().getRoomsAdministrator();
	}
	
    public List<Room>findRoomsMember(){
    	UserSS user_ss = UserService.authenticated();
		if(user_ss == null) {
			throw new AuthorizationException("Token Inválido");
		}
		
		Optional<User> user = repository.findById(user_ss.getID());
		return user.get().getRoomsMember();
	}
	    
	public User insert(User user) {
		user.setId(null);
		user.setPasswd(bcrypt.encode(user.getPasswd()).toString());
		
		//Adiciona usuário como membro de todas as salas existentes;
		List<Room> rooms = roomService.findAll();
			
		user = repository.save(user);
		
		for(Room room: rooms){
			user.getRoomsMember().add(room);
			room.getMembers().add(user);
			repositoryRoom.save(room);			
		}
		
		user = repository.save(user);
		
		return user;
	}
	
	public User subscribeInRoom(Integer roomId) {
		UserSS user_ss = UserService.authenticated();
		if(user_ss == null) {
			throw new AuthorizationException("Token Inválido");
		}
		Optional<Room> room = repositoryRoom.findById(roomId);
		Optional<User> user = repository.findById(user_ss.getID());
		
		//Adiciona room a lista de salar do usuário
		user.get().getRoomsMember().add(room.get());
		room.get().getMembers().add(user.get());
		
		repositoryRoom.save(room.get());		
		repository.save(user.get());
			
		return user.get();
	}


}
