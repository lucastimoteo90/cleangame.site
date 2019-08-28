package com.demo.dto;

import java.io.Serializable;
import java.util.ArrayList;

import com.demo.domain.Score;
import com.demo.domain.User;

import java.util.List;

public class ReportRoomDTO implements Serializable {
  	private static final long serialVersionUID = 1L;

  	private List<UserRankingDTO> users = new ArrayList<UserRankingDTO>();

  	private List<Score> scores = new ArrayList<Score>();
  	
  	
  	
  	public List<Score> getScores() {
		return scores;
	}

	public void setScores(List<Score> scores) {
		this.scores = scores;
	}

	public List<UserRankingDTO> getUsersRankingDTO() {
		return users;
	}

	public void setUsersRankingDTO(List<UserRankingDTO> users) {
		this.users = users;
	}
  	
  	
	
	
    
}
