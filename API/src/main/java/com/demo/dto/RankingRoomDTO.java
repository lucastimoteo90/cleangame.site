package com.demo.dto;

import java.io.Serializable;
import java.util.ArrayList;

import com.demo.domain.User;

import java.util.List;

public class RankingRoomDTO implements Serializable {
  	private static final long serialVersionUID = 1L;

  	private List<UserRankingDTO> users = new ArrayList<UserRankingDTO>();

  	public List<UserRankingDTO> getUsersRankingDTO() {
		return users;
	}

	public void setUsersRankingDTO(List<UserRankingDTO> users) {
		this.users = users;
	}
  	
  	
	
	
    
}
