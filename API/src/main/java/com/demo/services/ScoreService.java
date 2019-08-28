package com.demo.services;


import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.domain.Answer;
import com.demo.domain.Question;
import com.demo.domain.Room;
import com.demo.domain.Score;
import com.demo.domain.Team;
import com.demo.domain.User;
import com.demo.repositories.AnswerRepository;
import com.demo.repositories.ScoreRepository;




@Service
public class ScoreService {

	@Autowired
	private ScoreRepository repository;
	
	public List<Score> findAll() {
		return repository.findAll();
	}
	
	public List<Score> findByUserAndRoom(User user, Room room) {
		return repository.findByUserAndRoom(user, room);
	}
	
	public List<Score> findByTeamAndRoom(Team team, Room room) {
		return repository.findByTeamAndRoom(team, room);
	}
	
	public List<Score> findByTeam(Team team) {
		return repository.findByTeam(team);
	}
	
	public List<Score> findByRoomOrderByScoreDesc(Room room){
		return repository.findByRoomOrderByScoreDesc(room);
	}
	
	public List<Score> findByRoomOrderByScore(Room room){
		return repository.findByRoomOrderByScore(room);
	}
	
    public Score save(Score score) {
      return repository.save(score);
    }		
	
    public void deleteAll(List<Score> scores) {
        repository.deleteAll(scores);;
     }	
    

}
