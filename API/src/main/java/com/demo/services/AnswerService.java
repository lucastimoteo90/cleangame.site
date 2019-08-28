package com.demo.services;


import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.domain.Answer;
import com.demo.domain.Question;
import com.demo.domain.Room;
import com.demo.domain.Team;
import com.demo.domain.User;
import com.demo.repositories.AnswerRepository;




@Service
public class AnswerService {

	@Autowired
	private AnswerRepository repository;
	
	public List<Answer> findAll() {
		return repository.findAll();
	}
	
		
	public Integer findAnswerCorrectsRoom(User user, Room room){
		
		Integer hits = 0;
		for(Answer answer: repository.findAllByUserAndCorrectTrue(user)){
			if(answer.getQuestion().getRoom().getId() == room.getId()) {
			  hits++;
			}
		}
		
		return hits;
	}
	
	  /*Busca respostas corretas do team*/	
      public Integer findAnswerCorrectsTeam(Team team, Room room){		
		Integer hits = 0;
		for(Answer answer: repository.findAllByTeamAndCorrectTrue(team)){
			if(answer.getQuestion().getRoom().getId() == room.getId()) {
			  hits++;
			}
		}
		
		return hits;
	}
	
     public Integer findAnswerIncorrectsTeam(Team team, Room room){
  		Integer errors = 0;
  		for(Answer answer: repository.findAllByTeamAndCorrectFalse(team)){
  			if(answer.getQuestion().getRoom().getId() == room.getId()) {
  				errors++;
  			}
  		}
  		
  		return errors;
  	}
     
 	public Integer findAnswerSkipsTeam(Team team, Room room){
		Integer skips = 0;
		for(Answer answer: repository.findAllByTeamAndSkipTrue(team)){
			if(answer.getQuestion().getRoom().getId() == room.getId()) {
				skips++;
			}
		}
		
		return skips;
	} 
      
      
	public Integer findAnswerIncorrectsRoom(User user, Room room){
		Integer errors = 0;
		for(Answer answer: repository.findAllByUserAndCorrectFalse(user)){
			if(answer.getQuestion().getRoom().getId() == room.getId()) {
				errors++;
			}
		}
		
		return errors;
	}
	
	public Integer findAnswerSkipsRoom(User user, Room room){
		Integer skips = 0;
		for(Answer answer: repository.findAllByUserAndSkipTrue(user)){
			if(answer.getQuestion().getRoom().getId() == room.getId()) {
				skips++;
			}
		}
		
		return skips;
	}
	
	public List<Answer> findAnswersUserQuestions(User user, List<Question> questions ) {
		return repository.findByUserAndQuestionIn(user, questions);
	}
	
	public List<Answer> findAnswersTeamQuestions(Team team, List<Question> questions ) {
		return repository.findByTeamAndQuestionIn(team, questions);
	}
	
	public List<Answer> findByQuestionIn(List<Question> questions ) {
		return repository.findByQuestionIn(questions);
	}
	
	
	public Answer findById(Integer id) {
		return repository.findById(id).get();
	}
	
	public Answer save(Answer answer){
		return repository.save(answer);		
	}
	
	public void delete(List<Answer> answers) {
		repository.deleteAll(answers);
	}


}
