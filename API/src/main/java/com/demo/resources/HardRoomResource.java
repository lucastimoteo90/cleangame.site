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

import com.demo.domain.Battle;
import com.demo.domain.EasyQuestion;
import com.demo.domain.HardQuestion;
import com.demo.domain.HardRoom;
import com.demo.domain.MediumRoom;
import com.demo.domain.Question;
import com.demo.domain.Room;
import com.demo.services.HardRoomService;
import com.demo.services.MediumRoomService;



@RestController
@RequestMapping(value="hardroom")
public class HardRoomResource {

	@Autowired
	private HardRoomService service;
		
	@RequestMapping(value="{id}",method=RequestMethod.GET)
	public ResponseEntity<?> findById(@PathVariable Integer id){
		HardRoom room = (HardRoom)service.findById(id);
		return ResponseEntity.ok().body(room);
	}
	
		
	@RequestMapping(method=RequestMethod.POST)
	public ResponseEntity<HardRoom> insert(@RequestBody HardRoom room){
		room = service.insert(room);		
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(room.getId()).toUri();
		return ResponseEntity.created(uri).body(room);
	}
		
	@RequestMapping(value="/{id}/question/",method=RequestMethod.POST)
	public ResponseEntity<HardQuestion> insertQuestion(@PathVariable Integer id,@RequestBody HardQuestion question){
		Room room = service.findById(id);
		System.out.println("CODIGO:"+question.getCode());
		return ResponseEntity.ok().body(service.insertQuestion(room, question));		
	}
	
	
	
	@RequestMapping(value="/{id}/questions",method=RequestMethod.GET)
	public ResponseEntity<List<Question>> findAllQuestions(@PathVariable Integer id){
     	return ResponseEntity.ok().body(service.findAllQuestions(id));
	}
	
	/*{id} room id -> obtem questão do usuário;*/
	@RequestMapping(value="/{battle_id}/{id}/question/{idteam}/",method=RequestMethod.GET)
	public ResponseEntity <Question> getQuestion(@PathVariable Integer battle_id, @PathVariable Integer id, @PathVariable Integer idteam){
		return ResponseEntity.ok().body(service.getQuestion(battle_id,id,idteam));
	}	
	
	//RETORNA BATALHA
	@RequestMapping(value="/battle/{battle_id}/",method=RequestMethod.GET)
	public ResponseEntity <Battle> getBattleAction(@PathVariable Integer battle_id){
		return ResponseEntity.ok().body(service.getBattleAction(battle_id));
	}	
	
	
	
	@RequestMapping(value="/new_battle/{team_id}",method=RequestMethod.GET)
	public ResponseEntity<Battle> newBattle(@PathVariable Integer team_id){		
		return ResponseEntity.ok().body(service.newBattle(team_id));		
	}
	
	@RequestMapping(value="/join_battle/{battle_id}/{team_id}",method=RequestMethod.GET)
	public ResponseEntity<Battle> joinBattle(@PathVariable Integer battle_id, @PathVariable Integer team_id ){		
		return ResponseEntity.ok().body(service.joinBattle(battle_id, team_id));		
	}
		
	
	
}
