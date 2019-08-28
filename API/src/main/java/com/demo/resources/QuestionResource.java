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
import com.demo.domain.Question;
import com.demo.domain.Room;
import com.demo.dto.AlternativeDTO;
import com.demo.dto.ResumeRoomDTO;
import com.demo.dto.SkipDTO;
import com.demo.dto.TipDTO;
import com.demo.services.EasyRoomService;
import com.demo.services.MediumRoomService;
import com.demo.services.QuestionService;
import com.demo.services.RoomService;



@RestController
@RequestMapping(value="question")
public class QuestionResource {

	@Autowired
	private QuestionService service;
	

	@RequestMapping(method=RequestMethod.GET)
	public List<Question> list() {
		return service.findAll();
	}
	
	@RequestMapping(value="/{id}",method=RequestMethod.GET)
	public ResponseEntity<?> findById(@PathVariable Integer id){
		Question room = service.findById(id);
		return ResponseEntity.ok().body(room);
	}
		
	@RequestMapping(value="/{id}/{idteam}/tip",method=RequestMethod.GET)
	public ResponseEntity<TipDTO> getTip(@PathVariable Integer id, @PathVariable Integer idteam){
		TipDTO tip = service.getTip(id, idteam);
		return ResponseEntity.ok().body(tip);
	}
	
	@RequestMapping(value="/{id}/{idteam}/tip1",method=RequestMethod.GET)
	public ResponseEntity<TipDTO> getTip1(@PathVariable Integer id, @PathVariable Integer idteam){
		TipDTO tip = service.getTip1(id, idteam);
		return ResponseEntity.ok().body(tip);
	}
	
	@RequestMapping(value="/{id}/{idteam}/tip2",method=RequestMethod.GET)
	public ResponseEntity<TipDTO> getTip2(@PathVariable Integer id, @PathVariable Integer idteam){
		TipDTO tip = service.getTip2(id, idteam);
		return ResponseEntity.ok().body(tip);
	}
	
	@RequestMapping(value="/{id}/{idteam}/tip3",method=RequestMethod.GET)
	public ResponseEntity<TipDTO> getTip3(@PathVariable Integer id, @PathVariable Integer idteam){
		TipDTO tip = service.getTip3(id, idteam);
		return ResponseEntity.ok().body(tip);
	}
	
	
	@RequestMapping(value="/{id}/{idteam}/skip",method=RequestMethod.GET)
	public ResponseEntity<SkipDTO> skip(@PathVariable Integer id, @PathVariable Integer idteam){
		SkipDTO skip = service.skip(id, idteam);
		return ResponseEntity.ok().body(skip);
	}
	
	
	
}
