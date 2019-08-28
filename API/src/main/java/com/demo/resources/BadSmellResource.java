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

import com.demo.domain.BadSmell;
import com.demo.domain.EasyQuestion;
import com.demo.domain.EasyRoom;
import com.demo.domain.MediumRoom;
import com.demo.domain.Question;
import com.demo.domain.Room;
import com.demo.domain.User;
import com.demo.dto.AlternativeDTO;
import com.demo.dto.ResumeRoomDTO;
import com.demo.dto.SkipDTO;
import com.demo.dto.TipDTO;
import com.demo.services.BadSmellService;
import com.demo.services.EasyRoomService;
import com.demo.services.MediumRoomService;
import com.demo.services.QuestionService;
import com.demo.services.RoomService;



@RestController
@RequestMapping(value="badsmell")
public class BadSmellResource {

	@Autowired
	private BadSmellService service;
	

	@RequestMapping(method=RequestMethod.GET)
	public List<BadSmell> list() {
		return service.findAll();
	}
	
	
}
