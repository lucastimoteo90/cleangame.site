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
import javax.persistence.OneToOne;
import javax.persistence.Table;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
public class Battle {
    private static final long serialVersionUID = 1L;	
	
    @Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
    
    @OneToOne()
    @JsonIgnore
    private Team team1;
    
    @OneToOne()
    @JsonIgnore
    private Team team2;
    
    @ManyToOne
    private Room room;
    
    @OneToOne()
    @JsonIgnore
    private Team attack;

	public Team getAttack() {
		return attack;
	}

	public void setAttack(Team attack) {
		this.attack = attack;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Team getTeam1() {
		return team1;
	}

	public void setTeam1(Team team1) {
		this.team1 = team1;
	}

	public Team getTeam2() {
		return team2;
	}

	public void setTeam2(Team team2) {
		this.team2 = team2;
	}

	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}
	
	
    
    
    
		
	
    
    
    
	
	
}
