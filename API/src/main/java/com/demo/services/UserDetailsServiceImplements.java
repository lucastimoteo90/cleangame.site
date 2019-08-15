package com.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.demo.domain.User;
import com.demo.repositories.UserRepository;
import com.demo.security.UserSS;

@Service
public class UserDetailsServiceImplements implements UserDetailsService{

	@Autowired
	private UserRepository repository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = repository.findByMail(username);
		if(user == null)
		  throw new UsernameNotFoundException(username);		
	
		return new UserSS(user.getId(), user.getMail(), user.getPasswd(), user.getProfiles());
	}

}
