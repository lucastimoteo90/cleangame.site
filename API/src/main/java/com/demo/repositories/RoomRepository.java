package com.demo.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.demo.domain.EasyRoom;
import com.demo.domain.Room;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer>{
	List<Room> findByNameOrDescriptionContaining(String name, String description);
}
