package com.demo.dto;

import java.io.Serializable;

import com.demo.domain.Score;

public class ResumeRoomDTO implements Serializable {
  	private static final long serialVersionUID = 1L;

	Integer room_id;
    
    Integer hits;
    Integer errors;
    Integer skips;
      
    
    Integer totalQuestions;
    Integer position;
    
    Score score;
    Double penalites;
    
	public Integer getRoom_id() {
		return room_id;
	}

	public void setRoom_id(Integer room_id) {
		this.room_id = room_id;
	}

	public Integer getHits() {
		return hits;
	}
	

	public Double getPenalites() {
		return penalites;
	}

	public void setPenalites(Double penalites) {
		this.penalites = penalites;
	}

	public void setHits(Integer hits) {
		this.hits = hits;
	}

	public Integer getErrors() {
		return errors;
	}

	public void setErrors(Integer errors) {
		this.errors = errors;
	}

	public Integer getTotalQuestions() {
		return totalQuestions;
	}

	public void setTotalQuestions(Integer totalQuestions) {
		this.totalQuestions = totalQuestions;
	}

	public Integer getSkips() {
		return skips;
	}

	public void setSkips(Integer skips) {
		this.skips = skips;
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	public Score getScore() {
		return score;
	}

	public void setScore(Score score) {
		this.score = score;
	}
	
    
    
    
    
    
    
}
