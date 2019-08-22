package com.demo.services;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.domain.Answer;
import com.demo.domain.Question;
import com.demo.domain.Room;
import com.demo.domain.Score;
import com.demo.domain.Team;
import com.demo.domain.User;
import com.demo.dto.AlternativeDTO;
import com.demo.dto.RankingRoomDTO;
import com.demo.dto.ReportRoomDTO;
import com.demo.dto.ResumeRoomDTO;
import com.demo.dto.UserRankingDTO;
import com.demo.dto.UserReportDTO;
import com.demo.repositories.RoomRepository;
import com.demo.security.UserSS;
import com.demo.services.exception.AuthorizationException;
import com.demo.services.exception.ObjectNotFoundException;



@Service
public class RoomService {

	@Autowired
	private RoomRepository repository;
	
	@Autowired
	private QuestionService questionService;
	
	@Autowired
	private TeamService teamService;
	
	@Autowired 
	private ScoreService scoreService;
	
	
	@Autowired
	private AnswerService answerService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private RoomService roomService;
	
	
	public List<Room> findAll() {
		return repository.findAll();
	}
	
	public Room open(Integer id) {
		Room room = repository.findById(id).get();
		room.setOpen(true);
		repository.save(room);		
		return room;
	}
	
	public Room close(Integer id) {
		Room room = repository.findById(id).get();
		room.setOpen(false);
		repository.save(room);		
		return room;
	}
	

	public Room findById(Integer id) throws ObjectNotFoundException {
	    Optional<Room> obj = repository.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException("User id not find"));
	}
	
	public Room restart(Integer id) throws ObjectNotFoundException {
	    UserSS userSS = UserService.authenticated();
		if(userSS == null) {
			throw new AuthorizationException("Token Inválido");
		}	    
		User user = userService.findById(userSS.getID());
		Team team = teamService.findById(id);
		Room room = teamService.findRoomByTeam(team.getId());
	    
		List<Answer> answers = answerService.findAnswersTeamQuestions(team, room.getQuestions());
	
				
		answerService.delete(answers);	
		
		return room;
	}
	
	public Room reset(Integer id) throws ObjectNotFoundException {
	    UserSS userSS = UserService.authenticated();
		if(userSS == null) {
			throw new AuthorizationException("Token Inválido");
		}	    
		User user = userService.findById(userSS.getID());
		
		Room room = roomService.findById(id);
	    
		
		List<Answer> answers = answerService.findByQuestionIn(room.getQuestions());
		
		System.out.println("AQQUIII...");
		
		for(Answer answer: answers) {
			
			List<Score> scores = scoreService.findByTeam(answer.getTeam());
		    scoreService.deleteAll(scores);
		}
		answerService.delete(answers);	
		
		return room;
	}	
	
	
	public Team createTeam(Integer idRoom, String nameTeam) throws ObjectNotFoundException {
	    UserSS userSS = UserService.authenticated();
		if(userSS == null) {
			throw new AuthorizationException("Token Inválido");
		}	    
		User user = userService.findById(userSS.getID());
		Room room = repository.findById(idRoom).get();
		
			    
		//Adiciona 1 sala (Mapeamento permite mais de uma, para possivel uso futuro);
    	List<Room> teamRooms = new ArrayList<Room>();		
	 	List<User> teamUsers = new ArrayList<User>();
		
		
		teamRooms.add(room);
		teamUsers.add(user);
		
		//Gambiarra para não registrar mais de um team POR SALA para 
		//o mesmo usuário, especifico para primeiro esperimento.
		if(teamService.findByUserAndRoom(teamUsers, teamRooms).size() > 0) {
			 Team team = teamService.findByUserAndRoom(teamUsers,teamRooms).get(0);
			 //Clear json
			 team.setTeamRooms(null);
			 team.setTeamUsers(null);
			 team.setAnswers(null); 
			 return team;
		}else {		
			Team team = new Team();
			team.setName(nameTeam);
			team.setTeamRooms(teamRooms);
			team.setTeamUsers(teamUsers);
			team = teamService.save(team);
			
			//Cria score			
		     Score score = new Score();
			 score.setRoom(room);
			 score.setUser(user);
			 score.setScore(0.0);
			 score.setConsecutiveHits(0);
		     score = scoreService.save(score);			
			 scoreService.save(score);		
			
			return team;
		}
		
		//List<Answer> answers = answerService.findAnswersUserQuestions(user, room.getQuestions());
	
		//answerService.delete(answers);	
		
		
	}
	
	
	
	public AlternativeDTO markAlternative(AlternativeDTO alternative) {
		UserSS userSS = UserService.authenticated();
		if(userSS == null) {
			throw new AuthorizationException("Token Inválido");
		}	    
		User user = userService.findById(userSS.getID());
		
		//Obtem question
		Question question = questionService.findById(alternative.getQuestion());
		Answer answer = answerService.findById(alternative.getAnswer());
		
		Team team = answer.getTeam();
		//System.out.println("QUESTION"+question.getMd5correct());
	    //System.out.println("ANSWER"+answer.getQuestion().getAsk());
		/*
	    if(!question.getRoom().getOpen()) {
	    	return null;
	    }*/
		
		if(question.getMd5correct().equals(alternative.getMd5answer())){
			alternative.setCorrect(true);
			answer.setCorrect(true);
		}else {
			alternative.setCorrect(false);
		    answer.setCorrect(false);
		}	  
				
		answer.setEnd(new Timestamp(System.currentTimeMillis()));
		answerService.save(answer);
		alternative.setMd5correct(question.getMd5correct());
		
		if(scoreService.findByTeamAndRoom(team, question.getRoom()).size() > 0 ){
			Score score = scoreService.findByTeamAndRoom(team, question.getRoom()).get(0);
		    score.computeScore(answer);
		    scoreService.save(score);
		    alternative.setScore(score);
		}else{
			/*Score score = new Score();
			score.setRoom(question.getRoom());
			score.setUser(user);
			score.setTeam(team);
			score.setScore(0.0);
			score.setConsecutiveHits(0);
			score = scoreService.save(score);
			score.computeScore(answer);
			scoreService.save(score);
			alternative.setScore(score);
			*/
		}	 
			
		
		
		return alternative;
	}
	
	public List<Room> findByNameOrDescriptionLike(String keyword) {
		return repository.findByNameOrDescriptionContaining(keyword,keyword);
		//return repository.findByNameOrDescriptionLike(keyword, keyword);
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
	
	
	public Room insert(Room room) {
		room.setId(null);
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

	public ResumeRoomDTO makeResume(Integer id) {
		//Mudar pra team :)
		ResumeRoomDTO resume = new ResumeRoomDTO();
		UserSS userSS = UserService.authenticated();
		if(userSS == null) {
			throw new AuthorizationException("Token Inválido");
		}	
		
		User user = userService.findById(userSS.getID());
		Team team = teamService.findById(id);
		Room room = teamService.findRoomByTeam(id);
		
		resume.setRoom_id(room.getId());
		resume.setTotalQuestions(room.getQuestions().size());
	    resume.setHits(answerService.findAnswerCorrectsTeam(team, room));
		resume.setErrors(answerService.findAnswerIncorrectsTeam(team, room));
		resume.setSkips(answerService.findAnswerSkipsTeam(team, room));
	
		//Corrigir isso aqui.
		Score score = scoreService.findByTeamAndRoom(team, room).get(0);
		
		resume.setPenalites(score.getPanalites());
		resume.setScore(score);
		
			
		
		return resume;
	}
	
	public List<UserReportDTO> makeReport(Integer id) {
		//Mudar pra team :)
		List<UserReportDTO> report = new ArrayList<UserReportDTO>();
		UserSS userSS = UserService.authenticated();
		if(userSS == null) {
			throw new AuthorizationException("Token Inválido");
		}	
		
			
		User user = userService.findById(userSS.getID());
		
		Room room = repository.findById(id).get();
		
		
		List<Question> questions = questionService.findByRoom(room);
		
			
		
		for(Score score: scoreService.findByRoomOrderByScoreDesc(room)) {
		   UserReportDTO userReport = new UserReportDTO();
		   userReport.setUser(score.getUser()); 
		   userReport.setRoom(room);
		   userReport.setScore(score);
		   
		   answerService.findAnswersUserQuestions(score.getUser(), questions);
		   System.out.println("AQUI!!!!!!!!!!");
		   
		   for(Answer answer: answerService.findAnswersUserQuestions(score.getUser(), questions)) {
			   System.out.println("AQUI TAMBEM!!!!!!!!!!");   
			answer.setUser(null);
			//answer.setQuestion(null);
			answer.setScore(null);
			answer.setTeam(null);
		
			
		    try {
		     answer.setTime((answer.getEnd().getTime() - answer.getStart().getTime())/1000);
		    }catch(NullPointerException ex) {
		    	answer.setTime(0L);
		    }
		    
			userReport.getAnswers().add(answer);
			 
		   }
		   System.out.println("SAIU TAMBEM!!!!!!!!!!");   
		   
		   //userReport.setAnswers(answerService.findAnswersUserQuestions(score.getUser(), questions));
           report.add(userReport);		     
		   
		  
		}
			
		
		return report;
	}
	
	

	public RankingRoomDTO makeRanking(Integer id) {
		//Mudar pra team :)
		RankingRoomDTO ranking = new RankingRoomDTO();
		UserSS userSS = UserService.authenticated();
		if(userSS == null) {
			throw new AuthorizationException("Token Inválido");
		}	
		
		User user = userService.findById(userSS.getID());
		Team team = teamService.findById(id);
		Room room = teamService.findRoomByTeam(id);		
		
		Integer position = 1;
		for(Score score: scoreService.findByRoomOrderByScoreDesc(room)){
		  User userScore = score.getUser();	
		  UserRankingDTO userDTO = new UserRankingDTO();
		  
		  userDTO.setEmail(userScore.getMail());
		  userDTO.setName(userScore.getName());
		  userDTO.setScore(score.getScore());
		  userDTO.setPosition(position++);
		  
		   ranking.getUsersRankingDTO().add(userDTO);
		}
	
		
		return ranking;
	}
	
	public ResumeRoomDTO makeResumeBKP(Integer id) {
		//Mudar pra team :)
		ResumeRoomDTO resume = new ResumeRoomDTO();
		UserSS userSS = UserService.authenticated();
		if(userSS == null) {
			throw new AuthorizationException("Token Inválido");
		}	
		
		User user = userService.findById(userSS.getID());
		Room room = repository.findById(id).get();
		
		resume.setRoom_id(room.getId());
		resume.setTotalQuestions(room.getQuestions().size());
		
		resume.setHits(answerService.findAnswerCorrectsRoom(user, room));
		resume.setErrors(answerService.findAnswerIncorrectsRoom(user, room));
		resume.setSkips(answerService.findAnswerSkipsRoom(user, room));
		
		return resume;
	}

}
