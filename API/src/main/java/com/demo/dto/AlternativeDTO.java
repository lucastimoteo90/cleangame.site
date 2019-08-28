package com.demo.dto;

import com.demo.domain.Score;

public class AlternativeDTO {
	
	private Integer question;
	private String md5answer;
	private Boolean correct;
	private Integer answer;
	private Score score;
	
	
	public Integer getQuestion() {
		return question;
	}
	public void setQuestion(Integer question) {
		this.question = question;
	}
	public String getMd5answer() {
		return md5answer;
	}
	public void setMd5answer(String md5answer) {
		this.md5answer = md5answer;
	}
	public Boolean getCorrect() {
		return correct;
	}
	public void setCorrect(Boolean correct) {
		this.correct = correct;
	}
	public Integer getAnswer() {
		return answer;
	}
	public void setAnswer(Integer answer) {
		this.answer = answer;
	}
	public Score getScore() {
		return score;
	}
	public void setScore(Score score) {
		this.score = score;
	}
	
	
	
	
	
}
