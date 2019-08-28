package com.demo.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Inheritance(strategy=InheritanceType.JOINED)

public class Question implements Serializable{
	private static final long serialVersionUID = 1L;
		
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
    
	private String ask;
	
	private Boolean valid;
	
	private String correct;
	private String fake1;
	private String fake2;
	private String fake3;
	
	private String tip;
	private String tip2;
	private String tip3;
	
	@Transient
	private List<String> alternatives = new ArrayList<String>();
	
	@Transient
	private Integer answer;
	
	private String md5correct;
		
	private Integer rightAnswer;
	
	@Column(columnDefinition = "TEXT")
	private String code;
	
	private String filename;
	
	
	@ManyToOne
	@JoinColumn(name="room_id")
	@JsonIgnore
	private Room room;
	
	@JsonIgnore
	@OneToMany(mappedBy="question")
	private List<Answer> answers = new ArrayList<>();
	

	
	
	public List<String> makeAlternatives(){
		this.getAlternatives().add(this.getCorrect());
		this.getAlternatives().add(this.getFake1());
		this.getAlternatives().add(this.getFake2());	
		this.getAlternatives().add(this.getFake3());	
		
		//Apaga infos
		this.setCorrect(null);
		this.setFake1(null);
		this.setFake2(null);
		this.setFake3(null);
		this.setMd5correct(null);
		
				
		
		Collections.shuffle(this.getAlternatives(),new Random(this.getId()));
		
		return null;
	}
		
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getAsk() {
		return ask;
	}

	public void setAsk(String ask) {
		this.ask = ask;
	}

	public String getCorrect() {
		return correct;
	}

	public void setCorrect(String correct) {
		this.correct = correct;
	}

	public String getFake1() {
		return fake1;
	}

	public void setFake1(String fake1) {
		this.fake1 = fake1;
	}

	public String getFake2() {
		return fake2;
	}

	public void setFake2(String fake2) {
		this.fake2 = fake2;
	}

	public String getFake3() {
		return fake3;
	}

	public void setFake3(String fake3) {
		this.fake3 = fake3;
	}

	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}
	
	

	public Integer getAnswer() {
		return answer;
	}


	public void setAnswer(Integer answer) {
		this.answer = answer;
	}


	public Integer getRightAnswer() {
		return rightAnswer;
	}

	public void setRightAnswer(Integer rightAnswer) {
		this.rightAnswer = rightAnswer;
	}

	
	
	public List<Answer> getAnswers() {
		return answers;
	}

	public void setAnswers(List<Answer> answers) {
		this.answers = answers;
	}
	
	
	
	

	public Boolean getValid() {
		return valid;
	}


	public void setValid(Boolean valid) {
		this.valid = valid;
	}


	public String getMd5correct() {
		return md5correct;
	}

	public void setMd5correct(String md5correct) {
		this.md5correct = md5correct;
	}

	public String getCode() {
		return code.replace("*", "//");
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	

	public List<String> getAlternatives() {
		return alternatives;
	}


	public void setAlternatives(List<String> alternatives) {
		this.alternatives = alternatives;
	}

	

	public String getTip() {
		return tip;
	}


	public void setTip(String tip) {
		this.tip = tip;
	}


	public String getTip2() {
		return tip2;
	}


	public void setTip2(String tip2) {
		this.tip2 = tip2;
	}

	

	public String getTip3() {
		return tip3;
	}


	public void setTip3(String tip3) {
		this.tip3 = tip3;
	}


	public String getFilename() {
		return filename;
	}


	public void setFilename(String filename) {
		this.filename = filename;
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
		Question other = (Question) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	
	
	
	
	
	
	
	

     	
}
 