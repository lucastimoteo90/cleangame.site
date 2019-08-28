package com.demo.resources;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.demo.domain.EasyQuestion;
import com.demo.domain.EasyRoom;
import com.demo.domain.MediumRoom;
import com.demo.domain.Room;
import com.demo.domain.Team;
import com.demo.dto.AlternativeDTO;
import com.demo.dto.ResumeRoomDTO;
import com.demo.services.EasyRoomService;
import com.demo.services.MediumRoomService;
import com.demo.services.RoomService;
import com.demo.services.TeamService;



@RestController
@RequestMapping(value="team")
public class TeamResource {

	@Autowired
	private TeamService service;
	
	@Autowired
	private EasyRoomService easyRoomService;
	
	@Autowired
	private MediumRoomService mediumRoomService;
	
	@RequestMapping(method=RequestMethod.GET)
	public List<Team> list() {
		return service.findAll();
	}
	
		
	@RequestMapping(value="/getroom/{id}",method=RequestMethod.GET)
	public ResponseEntity<?> findById(@PathVariable Integer id){
		Room room = service.findRoomByTeam(id);
		return ResponseEntity.ok().body(room);
	}
	
	

	
	
	
	
}
