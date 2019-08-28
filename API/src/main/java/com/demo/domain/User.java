package com.demo.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.demo.domain.enums.Profile;
import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
@Table(name = "Users")
public class User implements Serializable{
	private static final long serialVersionUID = 1L;
	

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	private String mail;
	
	private String passwd;
	private String name;
	
	@ManyToMany(mappedBy="administrators")
	@JsonIgnore
	private List<Room> roomsAdministrator = new ArrayList<Room>();
	
	@ManyToMany(mappedBy="members")
	@JsonIgnore
	private List<Room> roomsMember = new ArrayList<Room>();
	
	@ManyToMany(mappedBy="teamUsers")
	private List<Team> teamUsers = new ArrayList<Team>();
	
	@ElementCollection(fetch=FetchType.EAGER)
	@CollectionTable(name="PROFILES")
	private Set<Integer> profiles =  new HashSet<>();
	
	@OneToMany(mappedBy="user")
	@JsonIgnore
	private List<Answer> answers = new ArrayList<>();
	
	@OneToMany(mappedBy="user")
	@JsonIgnore
	private List<Score> scores = new ArrayList<>();
	
	
	public User(Integer id, String mail, String passwd,String name) {
		super();
		this.id = id;
		this.mail = mail;
		this.passwd = passwd;
		this.name = name;
		addProfile(Profile.BASIC_USER);
	}

	
	
	public List<Score> getScores() {
		return scores;
	}



	public void setScores(List<Score> scores) {
		this.scores = scores;
	}



	public List<Room> getRoomsAdministrator() {
		return roomsAdministrator;
	}

	public void setRoomsAdministrator(List<Room> roomsAdministrator) {
		this.roomsAdministrator = roomsAdministrator;
	}

	public List<Room> getRoomsMember() {
		return roomsMember;
	}

	public void setRoomsMember(List<Room> roomsMember) {
		this.roomsMember = roomsMember;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
		
	public List<Answer> getAnswers() {
		return answers;
	}

	public void setAnswers(List<Answer> answers) {
		this.answers = answers;
	}

	public User() {
		addProfile(Profile.BASIC_USER);
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
		User other = (User) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}
    
	public Set<Profile> getProfiles() {
		return profiles.stream().map(x -> Profile.toEnum(x)).collect(Collectors.toSet());
	}

	public void addProfile(Profile profile) {
		profiles.add(profile.getId());
	}

     	
}
 