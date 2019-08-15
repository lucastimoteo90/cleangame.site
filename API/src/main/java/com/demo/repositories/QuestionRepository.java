package com.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.demo.domain.Answer;
import com.demo.domain.Question;
import com.demo.domain.Room;;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Integer>{
   
	List<Question> findByRoom(Room room);
	
	//List<Question> findByAnswersNotInAndRoom(List<Answer> answers,Room room);
	
}
