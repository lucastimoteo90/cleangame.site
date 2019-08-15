package com.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.demo.domain.Answer;
import com.demo.domain.CodeBadSmell;
import com.demo.domain.Question;
import com.demo.domain.Room;
import com.demo.domain.Team;
import com.demo.domain.User;

@Repository
public interface CodeBadSmellRepository extends JpaRepository<CodeBadSmell, Integer>{
  	
}
