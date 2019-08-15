package com.demo.services;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.domain.Answer;
import com.demo.domain.EasyQuestion;
import com.demo.domain.EasyRoom;
import com.demo.domain.Question;
import com.demo.domain.Room;
import com.demo.domain.Team;
import com.demo.domain.User;
import com.demo.repositories.AnswerRepository;
import com.demo.repositories.EasyQuestionRepository;
import com.demo.repositories.QuestionRepository;
import com.demo.repositories.RoomRepository;
import com.demo.security.UserSS;
import com.demo.services.exception.AuthorizationException;
import com.demo.services.exception.ObjectNotFoundException;

@Service
public class EasyRoomService {
	@Autowired
	private RoomRepository repository;
	
	@Autowired
	private QuestionRepository questionRepository;
	
	@Autowired
	private EasyQuestionRepository easyQuestionRepository;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private TeamService teamService;
	
	@Autowired
	private AnswerRepository answerRepository;
	
	public List<Room> findAll() {
		return repository.findAll();
	}

	public EasyRoom findById(Integer id) throws ObjectNotFoundException {
	    Optional<Room> obj = repository.findById(id);
		return (EasyRoom) obj.orElseThrow(() -> new ObjectNotFoundException("User id not find"));
	}
	
	public List<Room> findByNameOrDescriptionLike(String keyword) {
		return repository.findByNameOrDescriptionContaining(keyword,keyword);
		//return repository.findByNameOrDescriptionLike(keyword, keyword);
	}
		
	public List<Question> findAllQuestions(Integer id) {
	    Optional<Room> room = repository.findById(id);
	    return room.get().getQuestions();
	}
	
	public Question getQuestion(Integer idRoom, Integer idTeam) {
		UserSS userSS = UserService.authenticated();
		if(userSS == null ) {
			throw new AuthorizationException("Token Inválido");
		}
	
		User user = userService.findById(userSS.getID());			
		Team team = teamService.findById(idTeam);
		
		Optional<Room> room = repository.findById(idRoom);
	    		
		
		List<Question> roomQuestions = room.get().getAllQuestions();
		
		System.out.println("SALA"+room.get().getName());
		System.out.println("SALA"+roomQuestions.size());
			
		for(Question question: roomQuestions){
			//Procurar se existe answer para question
		    if(answerRepository.findByTeamAndQuestion(team, question).size() == 0) {
				Answer newAnswer = new Answer();
				newAnswer.setQuestion(question);
				newAnswer.setUser(user);
				newAnswer.setTeam(team);
				newAnswer.setTips(0);
				newAnswer.setStart(new Timestamp(System.currentTimeMillis()));
				newAnswer = answerRepository.save(newAnswer);
				question.setAnswer(newAnswer.getId());
				question.makeAlternatives();
				return question;
			}else if(answerRepository.findByTeamAndQuestionAndEndIsNull(team, question).size() > 0){
		    	question.setAnswer(answerRepository.findByTeamAndQuestionAndEndIsNull(team, question).get(0).getId());
		    	question.makeAlternatives();
		    	return question;
		    }				
					
		}
		
		
		return new Question();
	}
	
	
	public void delete(Integer id){
		Optional<Room> room = repository.findById(id);
		
		UserSS user = UserService.authenticated();
		if(user == null || !room.get().getAdministrators().contains( userService.findById(user.getID()))) {
			throw new AuthorizationException("Token Inválido");
		}
		
		//Não apagar usuários em cascata...
		room.get().setAdministrators(null);
		room.get().setMembers(null);
		repository.delete(room.get());
		
	}
		
	public EasyRoom insert(EasyRoom room) {
		/*
		 * Nescessario adicionar verificação;
		 * */
		UserSS user = UserService.authenticated();
		if(user == null) {
			throw new AuthorizationException("Token Inválido");
		}	
		
		//Insere usuário que fez a requisição como administrador
		List<User> administrators = new ArrayList<User>();
		administrators.add(userService.findById(user.getID()));
		
		room.setAdministrators(administrators);
		
		return repository.save(room);
	}
	
	
	public EasyQuestion insertQuestion(Room room,EasyQuestion question) {
		room.getAllQuestions().add(question);
		question.setRoom(room);
		question.setCode("");
		question.setValid(true);
		repository.save(room);
		questionRepository.save(question);
		return question;
	}
	

}
