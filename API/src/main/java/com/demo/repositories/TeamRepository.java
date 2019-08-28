package com.demo.repositories;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import com.demo.domain.Room;
import com.demo.domain.Team;
import com.demo.domain.User;

@Repository
public interface TeamRepository extends JpaRepository<Team, Integer>{
	List<Team> findByteamUsers(List<User> user);
	
	List<Team> findByteamUsersAndTeamRooms(List<User> user, List<Room> rooms);
	
}
