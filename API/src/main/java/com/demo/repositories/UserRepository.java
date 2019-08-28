package com.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.demo.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>{
	
	@Transactional(readOnly=true)
    User findByMail(String mail);
}
