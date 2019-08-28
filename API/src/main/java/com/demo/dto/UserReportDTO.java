package com.demo.dto;

import java.util.ArrayList;
import java.util.List;

import com.demo.domain.Answer;
import com.demo.domain.Room;
import com.demo.domain.Score;
import com.demo.domain.User;

public class UserReportDTO {

	private User user;
	private Room room;
	private Score score;
	
	private List<Answer> answers = new ArrayList<Answer>();

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

	public Score getScore() {
		return score;
	}

	public void setScore(Score score) {
		this.score = score;
	}

	public List<Answer> getAnswers() {
		return answers;
	}

	public void setAnswers(List<Answer> answers) {
		this.answers = answers;
	}
	
	
	
	
	
	
	
}
