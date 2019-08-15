package com.demo.domain;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;



@Entity
public class Answer implements Serializable{
	private static final long serialVersionUID = 1L;
		
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
    
	@ManyToOne
	@JsonIgnore
	@JoinColumn(name="question_id")
	private Question question;
	
	@ManyToOne
	@JoinColumn(name="user_id")
	private User user;
	
	@ManyToOne
	@JoinColumn(name="team_id")
	private Team team;
	
	private Integer alternative;
	
	private Timestamp start;
	
	@Column(name="endtime")
	private Timestamp end;
	
	private Boolean correct;
		
	private Integer tips;
	
	private Boolean tip1;
	private Boolean tip2;
	private Boolean tip3;
	
	private Boolean skip;
	
	private Integer score;	
	
	@Transient
	private Long time;
	
	public Answer() {
	 this.setSkip(false);
	 this.setTip1(false);
	 this.setTip2(false);
	 this.setTip3(false);	 
	}
			
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
			
	public Integer getTips() {
		return tips;
	}

	public void setTips(Integer tips) {
		this.tips = tips;
	}

	public Question getQuestion() {
		return question;
	}

	public void setQuestion(Question question) {
		this.question = question;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Integer getAlternative() {
		return alternative;
	}

	public void setAlternative(Integer alternative) {
		this.alternative = alternative;
	}
	

	public Boolean getCorrect() {
		return correct;
	}

	public void setCorrect(Boolean correct) {
		this.correct = correct;
	}
	
	public Timestamp getStart() {
		return start;
	}

	public void setStart(Timestamp start) {
		this.start = start;
	}
	
	

	public Timestamp getEnd() {
		return end;
	}

	public void setEnd(Timestamp end) {
		this.end = end;
	}

	
	
	public Boolean getSkip() {
		return skip;
	}

	public void setSkip(Boolean skip) {
		this.skip = skip;
	}
	
	
	public Boolean getTip1() {
		return tip1;
	}

	public void setTip1(Boolean tip1) {
		this.tip1 = tip1;
	}

	public Boolean getTip2() {
		return tip2;
	}

	public void setTip2(Boolean tip2) {
		this.tip2 = tip2;
	}

	public Boolean getTip3() {
		return tip3;
	}

	public void setTip3(Boolean tip3) {
		this.tip3 = tip3;
	}

	public Team getTeam() {
		return team;
	}

	public void setTeam(Team team) {
		this.team = team;
	}
	
	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Answer other = (Answer) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}


	public Long getTime() {
		return time;
	}


	public void setTime(Long time) {
		this.time = time;
	}
	
	
	
	
	
	
	
	
	

     	
}
 