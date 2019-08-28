package com.demo.services;


import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.domain.Answer;
import com.demo.domain.PMDErrors;
import com.demo.domain.Question;
import com.demo.domain.Room;
import com.demo.domain.User;
import com.demo.repositories.AnswerRepository;
import com.demo.repositories.PMDErrorsRepository;




@Service
public class PMDErrorService {

	@Autowired
	private PMDErrorsRepository repository;
	
	public List<PMDErrors> findAll() {
		return repository.findAll();
	}
	
	public PMDErrors save(PMDErrors pmdError){
		return repository.save(pmdError);		
	}
		
	


}
