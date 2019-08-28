package com.demo.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.demo.domain.Answer;
import com.demo.domain.Battle;
import com.demo.domain.EasyQuestion;
import com.demo.domain.EasyRoom;
import com.demo.domain.GitClone;
import com.demo.domain.HardQuestion;
import com.demo.domain.HardRoom;
import com.demo.domain.MediumRoom;
import com.demo.domain.Question;
import com.demo.domain.Room;
import com.demo.domain.Team;
import com.demo.domain.User;
import com.demo.repositories.AnswerRepository;
import com.demo.repositories.QuestionRepository;
import com.demo.repositories.RoomRepository;
import com.demo.security.UserSS;
import com.demo.services.exception.AuthorizationException;
import com.demo.services.exception.ObjectNotFoundException;

@Service
public class HardRoomService {

	@Autowired
	private RoomRepository repository;

	@Autowired
	private UserService userService;
	
	@Autowired
	private BattleService battleService;
	
	@Autowired
	private QuestionRepository questionRepository;
	
	@Autowired
	private ApplicationContext context;
	
	@Autowired
	private AnswerRepository answerRepository;
	
	@Autowired
	private TeamService teamService;

	public HardRoom insert(HardRoom room) {
		room.setId(null);
		UserSS user = UserService.authenticated();
		if (user == null) {
			throw new AuthorizationException("Token Inválido");
		}

		// Insere usuário que fez a requisição como administrador
		List<User> administrators = new ArrayList<User>();
		administrators.add(userService.findById(user.getID()));

		room.setAdministrators(administrators);
	
		repository.save(room);
		return room;
	}
	
	
	public HardQuestion insertQuestion(Room room,HardQuestion question) {
		room.getAllQuestions().add(question);
		question.setRoom(room);
		//question.setCode();
		question.setValid(true);
		repository.save(room);
		questionRepository.save(question);
		return question;
	}
	
	public Battle newBattle(Integer idTeam) {
		Team team = teamService.findById(idTeam);
		Battle battle = new Battle();
		battle.setTeam1(team);
		return battleService.save(battle);
	}
	
	public Battle joinBattle(Integer idBattle, Integer idTeam) {
		Team team = teamService.findById(idTeam);
		Battle battle = battleService.findById(idBattle);
		battle.setTeam2(team);
		battle.setAttack(battle.getTeam1());
		return battleService.save(battle);
	}
	
	public Battle getBattleAction(Integer battle_id){
		return battleService.findById(battle_id);
	}
	
	public Question getQuestion(Integer battle_id, Integer id, Integer idTeam) {
		//System.out.println("E AI? TA AQUI?");
		UserSS userSS = UserService.authenticated();
		if(userSS == null ) {
			throw new AuthorizationException("Token Inválido");
		}
		
		Battle battle = battleService.findById(battle_id);
		
		//Se requisição vier do time atacante
		if(battle.getAttack().getId() == idTeam) {
			
		}

		User user = userService.findById(userSS.getID());
		Team team = teamService.findById(idTeam);

		Optional<Room> room = repository.findById(id);


		List<Question> roomQuestions = room.get().getQuestions();
		Collections.shuffle(roomQuestions);
		System.out.println("SALA"+room.get().getName());
		System.out.println("SALA"+roomQuestions.size());
        
		for(Question question: roomQuestions){
			if(question.getValid()) {
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
		}
		
		return new Question();
	}
	
	public List<Question> findAllQuestions(Integer id) {
	    Optional<Room> room = repository.findById(id);
	    List<Question> questions = room.get().getAllQuestions();
	    //Collections.sort(questions, Comparator.comparing(Question::getFilename));
	    return room.get().getAllQuestions();
	}
	

	
	public HardRoom findById(Integer id) throws ObjectNotFoundException {
		Optional<Room> obj = repository.findById(id);
		return (HardRoom) obj.orElseThrow(() -> new ObjectNotFoundException("User id not find"));
	}

}
