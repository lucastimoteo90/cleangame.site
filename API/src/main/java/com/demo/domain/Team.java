package com.demo.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
public class Team implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	
	private String name;
	
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(
	    name = "USERS_TEAM",
	    joinColumns = @JoinColumn(name = "team_id"),
	    inverseJoinColumns = @JoinColumn(name = "user_id")
	)
	@JsonIgnore
	private List<User> teamUsers = new ArrayList<User>();
	
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(
	    name = "ROOMS_TEAM",
	    joinColumns = @JoinColumn(name = "team_id"),
	    inverseJoinColumns = @JoinColumn(name = "room_id")
	)	
	@JsonIgnore
	private List<Room> teamRooms = new ArrayList<Room>();
	
	@OneToMany(mappedBy="team")
	private List<Answer> answers = new ArrayList<>();
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<User> getTeamUsers() {
		return teamUsers;
	}

	public void setTeamUsers(List<User> teamUsers) {
		this.teamUsers = teamUsers;
	}

	public List<Room> getTeamRooms() {
		return teamRooms;
	}

	public void setTeamRooms(List<Room> teamRooms) {
		this.teamRooms = teamRooms;
	}

	public List<Answer> getAnswers() {
		return answers;
	}

	public void setAnswers(List<Answer> answers) {
		this.answers = answers;
	}
	
	
	
	
	
	

	
	
}
