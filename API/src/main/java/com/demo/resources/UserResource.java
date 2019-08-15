package com.demo.resources;

import java.util.List;
import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.demo.domain.Room;
import com.demo.domain.User;
import com.demo.services.UserService;



@RestController
@RequestMapping(value="users")
public class UserResource {

	@Autowired
	private UserService service;
	
	
	@RequestMapping(method=RequestMethod.GET)
	public List<User> list() {
		return service.findAll();
	}
	
	@RequestMapping(value="/mydata",method=RequestMethod.GET)
	public ResponseEntity<?> getDataLogedUser(){
		User user = service.getDataLogedUser();
		return ResponseEntity.ok().body(user);
	}
	
	@RequestMapping(value="/{id}",method=RequestMethod.GET)
	public ResponseEntity<?> findById(@PathVariable Integer id){
		User user = service.findById(id);
		return ResponseEntity.ok().body(user);
	}
	
	@RequestMapping(method=RequestMethod.POST)
	public ResponseEntity<Void> insert(@RequestBody User user){
		user = service.insert(user);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(user.getId()).toUri();
	
		return ResponseEntity.created(uri).build();
	}
	
	@RequestMapping(value="/rooms/admin",method=RequestMethod.GET)
	public ResponseEntity<List<?>> findRoomsAdmin(){
		List<?> rooms =  service.findRoomsAdmin();				
		
		return ResponseEntity.ok().body(rooms);
	}
	
	@RequestMapping(value="/rooms/member",method=RequestMethod.GET)
	public ResponseEntity<List<Room>> findRoomsMember(){
		List<Room> rooms =  service.findRoomsMember();
	 	return ResponseEntity.ok().body(rooms);
	}
	
	@RequestMapping(value="/room/subscribe/{id}",method=RequestMethod.POST)
	public ResponseEntity<User> subscribeInRoom(@PathVariable Integer id){
		User user = service.subscribeInRoom(id); 
		return ResponseEntity.ok().body(user);
	}
	
	
	
	
	
}
