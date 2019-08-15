package com.demo.services;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.domain.Answer;
import com.demo.domain.BadSmell;
import com.demo.domain.Battle;
import com.demo.domain.CodeBadSmell;
import com.demo.domain.Question;
import com.demo.domain.Room;
import com.demo.domain.Score;
import com.demo.domain.Team;
import com.demo.domain.User;
import com.demo.dto.SkipDTO;
import com.demo.dto.TipDTO;
import com.demo.repositories.AnswerRepository;
import com.demo.repositories.BadsmellRepository;
import com.demo.repositories.BattleRepository;
import com.demo.repositories.CodeBadSmellRepository;
import com.demo.repositories.QuestionRepository;
import com.demo.security.UserSS;
import com.demo.services.exception.AuthorizationException;




@Service
public class BattleService {

	@Autowired
	private BattleRepository repository;
	
	@Autowired
	private UserService userService;
	
		
	public List<Battle> findAll() {
		return repository.findAll();
	}
    
	public Battle findById(Integer id) {
		return repository.findById(id).get();
	}
	
	public Battle save(Battle battle) {
		 return repository.save(battle);
	}
	

}
