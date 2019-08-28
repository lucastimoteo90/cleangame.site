package com.demo.security;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.demo.domain.enums.Profile;

public class UserSS implements UserDetails{

	private static final long serialVersionUID = 1L;
	private Integer id;
	private String mail;
	private String passwd;
	private Collection<? extends GrantedAuthority> authorities;
	
	public UserSS() {
		
	}
	
	
	
	
	
	public UserSS(Integer id, String mail, String passwd, Set<Profile> profiles ) {
		super();
		this.id = id;
		this.mail = mail;
		this.passwd = passwd;
		this.authorities = profiles.stream().map(p -> new SimpleGrantedAuthority(p.getDescription())).collect(Collectors.toList());
	}

	
	public boolean hasRole(Profile profile) {
	  	return getAuthorities().contains(new SimpleGrantedAuthority(profile.getDescription()));
	}



	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return passwd;
	}

	@Override
	public String getUsername() {
		return mail;
	}

	public Integer getID() {
		return id;
	}
	
	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

}
