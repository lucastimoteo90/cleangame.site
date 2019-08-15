package com.demo.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class PMDErrors {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name="room_id")
	@JsonIgnore
	private MediumRoom room;
	
	private String Jpackage;
	private String file_dir;
	private Integer priority;
	private Integer line;
	private String description;
	private String rule_set;
	private String rule;
	
	public String getJpackage() {
		return Jpackage;
	}
	public void setJpackage(String jpackage) {
		Jpackage = jpackage;
	}
	public String getFile_dir() {
		return file_dir;
	}
	public void setFile_dir(String file_dir) {
		this.file_dir = file_dir;
	}
	public Integer getPriority() {
		return priority;
	}
	public void setPriority(Integer priority) {
		this.priority = priority;
	}
	public Integer getLine() {
		return line;
	}
	public void setLine(Integer line) {
		this.line = line;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getRule_set() {
		return rule_set;
	}
	public void setRule_set(String rule_set) {
		this.rule_set = rule_set;
	}
	public String getRule() {
		return rule;
	}
	public void setRule(String rule) {
		this.rule = rule;
	}
	public MediumRoom getRoom() {
		return room;
	}
	public void setRoom(MediumRoom room) {
		this.room = room;
	}
	
	
	
	
	
	
}
