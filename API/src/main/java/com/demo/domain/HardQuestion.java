package com.demo.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
public class HardQuestion extends Question implements Serializable{
	private static final long serialVersionUID = 1L;
		
	@OneToMany(mappedBy="hardQuestion")
	@JsonIgnore
	private List<CodeBadSmell> codeBadSmell = new ArrayList<>();

		
	public List<CodeBadSmell> getCodeBadSmell() {
		return codeBadSmell;
	}

	public void setCodeBadSmell(List<CodeBadSmell> codeBadSmell) {
		this.codeBadSmell = codeBadSmell;
	}

	
	
	
		
	
	
}
 