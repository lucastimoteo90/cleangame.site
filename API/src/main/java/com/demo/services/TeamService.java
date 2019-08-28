package com.demo.services;


import java.util.ArrayList;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.domain.Room;
import com.demo.domain.Team;
import com.demo.domain.User;
import com.demo.repositories.AnswerRepository;
import com.demo.repositories.TeamRepository;
import com.demo.security.UserSS;
import com.demo.services.exception.AuthorizationException;




@Service
public class TeamService {

	@Autowired
	private TeamRepository repository;

	@Autowired 
	private RoomService roomService;
	
	@Autowired
	private UserService userService;
	
	public List<Team> findAll() {
		return repository.findAll();
	}
	
	public Team save(Team team) {
		return repository.save(team);
	}
	
	public Team findById(Integer id) {
		return repository.findById(id).get();
	}
	
	public List<Team> findByUser(List<User> users) {
	 return repository.findByteamUsers(users);
	}
	
	public List<Team> findByUserAndRoom(List<User> users, List<Room>rooms) {
		 return repository.findByteamUsersAndTeamRooms(users, rooms);
		}
	
	/*
	public Team insert(Team team){
		UserSS userSS = UserService.authenticated();
		if(userSS == null ) {
			throw new AuthorizationException("Token Inválido");
		}
		
		User user = userService.findById(userSS.getID());
		Room room = repository.findById(idRoom).get();
	    
		//Adiciona 1 sala (Mapeamento permite mais de uma, para possivel uso futuro);
    	List<Room> teamRooms = new ArrayList<Room>();
		
	 	List<User> teamUsers = new ArrayList<User>();
		
		
		teamRooms.add(room);
		teamUsers.add(user);
		
		Team team = new Team();
		team.setName(nameTeam);
		team.setTeamRooms(teamRooms);
		
		
		
		
		return repository.save(team);
	}*/
	
	
	public Room findRoomByTeam(Integer teamId) {
		Team team = repository.findById(teamId).get();
		return team.getTeamRooms().get(0);
	}
	


}
