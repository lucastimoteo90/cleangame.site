package com.demo.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
public class CodeBadSmell {
    private static final long serialVersionUID = 1L;	
	
    @Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
    
    @ManyToOne
    @JoinColumn(name="badsmell_id")
	private BadSmell badsmell;
    
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name="hard_question_id")
	private HardQuestion hardQuestion;
        
    
    private Integer startline;
    private Integer endline;
	
    
    public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public BadSmell getBadsmell() {
		return badsmell;
	}
	public void setBadsmell(BadSmell badsmell) {
		this.badsmell = badsmell;
	}
	public HardQuestion getHardQuestion() {
		return hardQuestion;
	}
	public void setHardQuestion(HardQuestion hardQuestion) {
		this.hardQuestion = hardQuestion;
	}
	public Integer getStartline() {
		return startline;
	}
	public void setStartline(Integer startline) {
		this.startline = startline;
	}
	public Integer getEndline() {
		return endline;
	}
	public void setEndline(Integer endline) {
		this.endline = endline;
	}
    
    
    
		
	
    
    
    
	
	
}
