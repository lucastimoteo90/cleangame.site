package com.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.demo.domain.Answer;
import com.demo.domain.EasyQuestion;
import com.demo.domain.Room;;

@Repository
public interface EasyQuestionRepository extends JpaRepository<EasyQuestion, Integer>{
   	List<EasyQuestion> findByRoom(Room room);
}
