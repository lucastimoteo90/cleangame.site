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
import com.demo.dto.RankingRoomDTO;
import com.demo.dto.ReportRoomDTO;
import com.demo.dto.ResumeRoomDTO;
import com.demo.dto.UserReportDTO;
import com.demo.services.EasyRoomService;
import com.demo.services.MediumRoomService;
import com.demo.services.QuestionService;
import com.demo.services.RoomService;



@RestController
@RequestMapping(value="rooms")
public class RoomResource {

	@Autowired
	private RoomService service;
	
	@Autowired
	private EasyRoomService easyRoomService;
	
	@Autowired
	private MediumRoomService mediumRoomService;
	

	
	@RequestMapping(method=RequestMethod.GET)
	public List<Room> list() {
		return service.findAll();
	}
	
	@RequestMapping(value="/{id}",method=RequestMethod.GET)
	public ResponseEntity<?> findById(@PathVariable Integer id){
		Room room = service.findById(id);
		return ResponseEntity.ok().body(room);
	}
	
	@RequestMapping(value="/{id}/resume",method=RequestMethod.GET)
	public ResponseEntity<ResumeRoomDTO> makeResume(@PathVariable Integer id){
		ResumeRoomDTO resume = service.makeResume(id);
		return ResponseEntity.ok().body(resume);
	}
	
	@RequestMapping(value="/{id}/report",method=RequestMethod.GET)
	public ResponseEntity<List<UserReportDTO>> makeReport(@PathVariable Integer id){
		List<UserReportDTO> report = service.makeReport(id);
		return ResponseEntity.ok().body(report);
	}
	
	@RequestMapping(value="/{id}/ranking",method=RequestMethod.GET)
	public ResponseEntity<RankingRoomDTO> makeRanking(@PathVariable Integer id){
	RankingRoomDTO ranking = service.makeRanking(id);
		return ResponseEntity.ok().body(ranking);
	}
	
	
	@RequestMapping(value="/{id}/restart",method=RequestMethod.GET)
	public ResponseEntity<Room> restart(@PathVariable Integer id){
		Room room = service.restart(id);
		return ResponseEntity.ok().body(room);
	}
	
	@RequestMapping(value="/search",method=RequestMethod.GET)
	public ResponseEntity<?> findByNameAndDescription(@RequestParam(value="keyword",defaultValue="") String keyword ){
		List<Room> rooms = service.findByNameOrDescriptionLike(keyword);
		return ResponseEntity.ok().body(rooms);
	}
	
	@RequestMapping(method=RequestMethod.POST)
	public ResponseEntity<Void> insert(@RequestBody Room room){
		room = service.insert(room);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(room.getId()).toUri();
		
		return ResponseEntity.created(uri).build();
	}
	
	@RequestMapping(value="/markalternative",method=RequestMethod.POST)
	public ResponseEntity<AlternativeDTO> markAlternative(@RequestBody AlternativeDTO alternative){
		alternative = service.markAlternative(alternative);
		return ResponseEntity.ok().body(alternative);
	}
	
	@RequestMapping(value="/open/{id}",method=RequestMethod.POST)
	public ResponseEntity<Room> open(@PathVariable Integer id){
		return ResponseEntity.ok().body( service.open(id));
	}
	
	@RequestMapping(value="/close/{id}",method=RequestMethod.POST)
	public ResponseEntity<Room> close(@PathVariable Integer id){
		return ResponseEntity.ok().body( service.close(id));
	}
	
	@RequestMapping(value="/createteam/{id}",method=RequestMethod.POST)
	public ResponseEntity<Team> createTeam(@PathVariable Integer id, @RequestBody Team team){
		return ResponseEntity.ok().body(service.createTeam(id, team.getName()));
	}
	
	
	
	
	@RequestMapping(value="/easy",method=RequestMethod.POST)
	public ResponseEntity<EasyRoom> insert(@RequestBody EasyRoom room){
		room = easyRoomService.insert(room);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(room.getId()).toUri();
		return ResponseEntity.created(uri).body(room);
	}
	
	@RequestMapping(value="/medium",method=RequestMethod.POST)
	public ResponseEntity<Void> insert(@RequestBody MediumRoom room){
		room = mediumRoomService.insert(room);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(room.getId()).toUri();
		return ResponseEntity.created(uri).build();
	}
	
	
	//Limitação do AngulaJS1 utilizando post para delete
	@RequestMapping(value="/delete/{id}",method=RequestMethod.POST)
	public ResponseEntity<?>delete(@PathVariable Integer id){
		service.delete(id);
		return ResponseEntity.ok().body(null);
	}
	
	@RequestMapping(value="/{id}/question",method=RequestMethod.POST)
	public ResponseEntity<?>insert(@PathVariable Integer id, @RequestBody EasyQuestion question){
		//service.delete(id);
		return ResponseEntity.ok().body(null);
	}
	
	@RequestMapping(value="/{id}/loadquestion",method=RequestMethod.GET)
	public ResponseEntity<List<?>> loadQuestion(@PathVariable Integer id){
		Room room = service.findById(id);
					
		return ResponseEntity.ok().body(room.getQuestions());
	}
	
	

	
	
	
	
}
