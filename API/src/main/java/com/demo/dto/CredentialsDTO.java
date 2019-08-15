package com.demo.dto;

import java.io.Serializable;

public class CredentialsDTO implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String mail;
	private String passwd;
	
	public CredentialsDTO() {
		
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
	
	

}
