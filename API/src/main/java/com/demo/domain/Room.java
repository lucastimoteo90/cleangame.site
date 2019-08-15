package com.demo.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.demo.domain.enums.RoomType;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Inheritance(strategy=InheritanceType.JOINED)
public abstract class Room implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	
	private Integer type;
	
	private String name;
	
	private String description;
	
	private String gitUrl;
	
	private Boolean open;
	
	private Integer totalParticipantes;
	
	@OneToMany(mappedBy="room")
	@JsonIgnore
	private List<Question> questions = new ArrayList<Question>();
	
	
	@JsonIgnore
	@OneToMany(mappedBy="room")
	private List<Battle> battles = new ArrayList<Battle>();
	
	private boolean isPublic;
	
	
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(
	    name = "ROOM_ADMIN",
	    joinColumns = @JoinColumn(name = "room_id"),
	    inverseJoinColumns = @JoinColumn(name = "admin_id")
	)
	@JsonIgnore
	private List<User> administrators = new ArrayList<User>();
	
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(
	    name = "ROOM_MEMBER",
	    joinColumns = @JoinColumn(name = "room_id"),
	    inverseJoinColumns = @JoinColumn(name = "member_id")
	)
	@JsonIgnore
	private List<User> members = new ArrayList<User>();

	@ManyToMany(mappedBy="teamRooms")
	private List<Team> teamRooms = new ArrayList<Team>();
	
	@OneToMany(mappedBy="room")
	private List<Score> scores = new ArrayList<>();
	
	public Room() {
		
	}
	
	public RoomType getType() {
		return RoomType.toEnum(type);
	}

	public void setType(RoomType t) {
		type = t.getId();
	}
	
	public Room(String description, String gitUrl, boolean isPublic) {
		super();
		this.description = description;
		this.gitUrl = gitUrl;
		this.isPublic = isPublic;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}	

	public Integer getTotalParticipantes() {
		return this.members.size();
	}

	public void setTotalParticipantes(Integer totalParticipantes) {
		this.totalParticipantes = totalParticipantes;
	}

	public void setName(String name) {
		this.name = name;
	}
    
	
	public Boolean getOpen() {
		return open;
	}
	
	public void setOpen(Boolean open) {
		this.open = open;
	}

	
	public boolean isPublic() {
		return isPublic;
	}


	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}


	public String getGitUrl() {
		return gitUrl;
	}

	public void setGitUrl(String gitUrl) {
		this.gitUrl = gitUrl;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<User> getAdministrators() {
		return administrators;
	}

	public void setAdministrators(List<User> administrators) {
		this.administrators = administrators;
	}

	public List<User> getMembers() {
		return members;
	}

	public void setMembers(List<User> members) {
		this.members = members;
	}

	public List<Question> getQuestions() {
		//Remove quetões não validadas
		List<Question> questions_temp = new ArrayList<Question>();
		for(Question question: questions){
			if(question.getValid()){
				questions_temp.add(question);
			}
		}
		Collections.sort(questions_temp, Comparator.comparing(Question::getFilename));
		return questions_temp;
	}
	
	public List<Question> getAllQuestions() {
		//Remove quetões não validadas
		//Collections.sort(questions, Comparator.comparing(Question::getFilename));
	    return questions;
	}

	public void setQuestions(List<Question> questions) {
		this.questions = questions;
	}
	
	

	public List<Battle> getBattles() {
		return battles;
	}

	public void setBattles(List<Battle> battles) {
		this.battles = battles;
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
		Room other = (Room) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	
	
	
	
	

}
