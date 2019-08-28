package com.demo.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.demo.domain.Answer;
import com.demo.domain.EasyRoom;
import com.demo.domain.GitClone;
import com.demo.domain.MediumRoom;
import com.demo.domain.Question;
import com.demo.domain.Room;
import com.demo.domain.Team;
import com.demo.domain.User;
import com.demo.repositories.AnswerRepository;
import com.demo.repositories.RoomRepository;
import com.demo.security.UserSS;
import com.demo.services.exception.AuthorizationException;
import com.demo.services.exception.ObjectNotFoundException;

@Service
public class MediumRoomService {

	@Autowired
	private RoomRepository repository;

	@Autowired
	private UserService userService;
	
	@Autowired
	private ApplicationContext context;
	
	@Autowired
	private AnswerRepository answerRepository;
	
	@Autowired
	private TeamService teamService;

	public MediumRoom insert(MediumRoom room) {
		room.setId(null);
		UserSS user = UserService.authenticated();
		if (user == null) {
			throw new AuthorizationException("Token Inválido");
		}

		// Insere usuário que fez a requisição como administrador
		List<User> administrators = new ArrayList<User>();
		administrators.add(userService.findById(user.getID()));

		room.setAdministrators(administrators);
		room.setCloneStatus("Aguardando execução");
		room.setPmdStatus("Aguardando execução");
		room.setMakeQuestionStatus("Aguardando execução");
		repository.save(room);
				
		JmsTemplate jmsTemplate = context.getBean(JmsTemplate.class);
		
		GitClone gitClone = new GitClone();
		gitClone.setRoom(room);
		gitClone.setUserid(user.getID());
		
		jmsTemplate.convertAndSend("gitClone",gitClone);
		
		return room;
	}
	
	public Question getQuestion(Integer id, Integer idTeam) {
		
		UserSS userSS = UserService.authenticated();
		if(userSS == null ) {
			throw new AuthorizationException("Token Inválido");
		}
	
		User user = userService.findById(userSS.getID());
		Team team = teamService.findById(idTeam);
		
		Optional<Room> room = repository.findById(id);
	    		
		
		List<Question> roomQuestions = room.get().getQuestions();
		
		System.out.println("SALA"+room.get().getName());
		System.out.println("SALA"+roomQuestions.size());
			
		for(Question question: roomQuestions){
			//Procurar se existe answer para question
		    if(answerRepository.findByTeamAndQuestion(team, question).size() == 0) {
				Answer newAnswer = new Answer();
				newAnswer.setQuestion(question);
				newAnswer.setUser(user);
				newAnswer.setTips(0);
				newAnswer.setStart(new Timestamp(System.currentTimeMillis()));
				newAnswer.setTeam(team);
				newAnswer = answerRepository.save(newAnswer);
				question.setAnswer(newAnswer.getId());
				question.makeAlternatives();
				question.setTip("");
				question.setTip2("");
				question.setTip3("");
				return question;
			}
			
		    //Se existe em aberto
		    if(answerRepository.findByTeamAndQuestionAndEndIsNull(team, question).size() > 0){
		    	question.setAnswer(answerRepository.findByTeamAndQuestionAndEndIsNull(team, question).get(0).getId());
		    	question.makeAlternatives();
		    	question.setTip("");
				question.setTip2("");
				question.setTip3("");
		    	return question;
		    }				
					
		}
		
		
		return new Question();
	}
	
	public List<Question> findAllQuestions(Integer id) {
	    Optional<Room> room = repository.findById(id);
	    return room.get().getQuestions();
	}
	
	public void setCompleteCloneStatus(GitClone gitClone){
		System.out.println("COMPLETED");		
		//CHAMA ANALISADOR PMD PARA GERAR REPORT
		JmsTemplate jmsTemplate = context.getBean(JmsTemplate.class);
		jmsTemplate.convertAndSend("pmdAnalyser",gitClone);
		MediumRoom room = this.findById(gitClone.getRoom().getId());
		room.setCloneStatus("COMPLETED");
		repository.save(room);		
	}
	
	public void setInExecutionCloneStatus(GitClone gitClone){
		MediumRoom room = this.findById(gitClone.getRoom().getId());
		room.setCloneStatus("EXECUTING");
		repository.save(room);		
	}
	
	
	public void setCompleteCloneStatusError(MediumRoom room){
	    room = this.findById(room.getId());
		room.setCloneStatus("ERROR");
		repository.save(room);		
	}
	
	
	public void setCompletePMDStatus(GitClone gitClone){
		System.out.println("COMPLETED");		
		//CHAMA ANALISADOR PMD PARA GERAR REPORT
		JmsTemplate jmsTemplate = context.getBean(JmsTemplate.class);
		jmsTemplate.convertAndSend("makeQuestions",gitClone);
		MediumRoom room = this.findById(gitClone.getRoom().getId());
		room.setPmdStatus("COMPLETED");
		repository.save(room);		
	}
	
	public void setInExecutionPMDStatus(GitClone gitClone){
		MediumRoom room = this.findById(gitClone.getRoom().getId());
		room.setPmdStatus("EXECUTING");
		repository.save(room);		
	}
	
	public void setCompletePMDStatusError(MediumRoom room){
	    room = this.findById(room.getId());
		room.setPmdStatus("ERROR");
		repository.save(room);		
	}
	
	public void setCompleteMakeQuestionStatus(GitClone gitClone){
		System.out.println("COMPLETED");
		//CHAMA ANALISADOR PMD PARA GERAR REPORT
		MediumRoom room = this.findById(gitClone.getRoom().getId());
		room.setMakeQuestionStatus("COMPLETED");
		repository.save(room);		
	}
	
	public void setInExecutionMakeQuestionStatus(GitClone gitClone){
		MediumRoom room = this.findById(gitClone.getRoom().getId());
		room.setMakeQuestionStatus("EXECUTING");
		repository.save(room);					
	}
	
	public void setCompleteMakeQuestionStatusError(MediumRoom room){
	    room = this.findById(room.getId());
		room.setMakeQuestionStatus("ERROR");
		repository.save(room);		
	}
	
	public MediumRoom findById(Integer id) throws ObjectNotFoundException {
		Optional<Room> obj = repository.findById(id);
		return (MediumRoom) obj.orElseThrow(() -> new ObjectNotFoundException("User id not find"));
	}

}
