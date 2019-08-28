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
import com.demo.domain.Question;
import com.demo.domain.Room;
import com.demo.services.EasyRoomService;

@RestController
@RequestMapping(value="easyroom")
public class EasyRoomResource {

	@Autowired
	private EasyRoomService service;
	
	@RequestMapping(method=RequestMethod.GET)
	public List<Room> list() {
		return service.findAll();
	}
	
	@RequestMapping(value="/{id}",method=RequestMethod.GET)
	public ResponseEntity<?> findById(@PathVariable Integer id){
		EasyRoom room = (EasyRoom)service.findById(id);
		return ResponseEntity.ok().body(room);
	}
	
	@RequestMapping(value="/{id}/questions",method=RequestMethod.GET)
	public ResponseEntity<List<Question>> findAllQuestions(@PathVariable Integer id){
     	return ResponseEntity.ok().body(service.findAllQuestions(id));
	}
	
	/*{id} room id -> obtem questão do usuário;*/
	@RequestMapping(value="/{id}/question/{idteam}/",method=RequestMethod.GET)
	public ResponseEntity <Question> getQuestion(@PathVariable Integer id, @PathVariable Integer idteam){
		return ResponseEntity.ok().body(service.getQuestion(id, idteam));
	}	
	
		
	@RequestMapping(method=RequestMethod.POST)
	public ResponseEntity<EasyRoom> insert(@RequestBody EasyRoom room){
		room = service.insert(room);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(room.getId()).toUri();
			return ResponseEntity.created(uri).body(room);
	}
	
	@RequestMapping(value="/{id}/question/",method=RequestMethod.POST)
	public ResponseEntity<EasyQuestion> insertQuestion(@PathVariable Integer id,@RequestBody EasyQuestion question){
		Room room = service.findById(id);
		return ResponseEntity.ok().body(service.insertQuestion(room, question));		
	}
	
	//Limitação do AngulaJS1 utilizando post para delete
	@RequestMapping(value="/delete/{id}",method=RequestMethod.POST)
	public ResponseEntity<?>delete(@PathVariable Integer id){
		service.delete(id);
		return ResponseEntity.ok().body(null);
	}
		
	@RequestMapping(value="/{id}/loadquestion",method=RequestMethod.GET)
	public ResponseEntity<List<?>> loadQuestion(@PathVariable Integer id){
		Room room = service.findById(id);
					
		return ResponseEntity.ok().body(room.getQuestions());
	}
	
	

	
	
	
	
}
