package com.demo.resources;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.demo.domain.MediumRoom;
import com.demo.domain.Question;
import com.demo.services.MediumRoomService;



@RestController
@RequestMapping(value="mediumroom")
public class MediumRoomResource {

	@Autowired
	private MediumRoomService service;
		
	@RequestMapping(value="{id}",method=RequestMethod.GET)
	public ResponseEntity<?> findById(@PathVariable Integer id){
		MediumRoom room = (MediumRoom)service.findById(id);
		return ResponseEntity.ok().body(room);
	}
	
	
	
	@RequestMapping(method=RequestMethod.POST)
	public ResponseEntity<MediumRoom> insert(@RequestBody MediumRoom room){
		room = service.insert(room);		
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(room.getId()).toUri();
			return ResponseEntity.created(uri).body(room);
	}
		
	
	
	@RequestMapping(value="{id}/status/",method=RequestMethod.GET)
	public ResponseEntity<?> findStatusById(@PathVariable Integer id){
		MediumRoom room = (MediumRoom)service.findById(id);
		return ResponseEntity.ok().body(room);
	}
	
	
	@RequestMapping(value="/{id}/questions",method=RequestMethod.GET)
	public ResponseEntity<List<Question>> findAllQuestions(@PathVariable Integer id){
     	return ResponseEntity.ok().body(service.findAllQuestions(id));
	}
	
	/*{id} room id -> obtem questão do usuário;*/
	@RequestMapping(value="/{id}/question/{idteam}/",method=RequestMethod.GET)
	public ResponseEntity <Question> getQuestion(@PathVariable Integer id, @PathVariable Integer idteam){
		return ResponseEntity.ok().body(service.getQuestion(id,idteam));
	}	
		
	/*
	 * {id} room id -> obtem questão do usuário;
	 * 
	@RequestMapping(value="/{id}/question",method=RequestMethod.GET)
	public ResponseEntity <Question> getQuestion(@PathVariable Integer id){
		return ResponseEntity.ok().body(service.getQuestion(id));
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
	*/
	

	
	
	
	
}
