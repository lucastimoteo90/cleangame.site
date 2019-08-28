package com.demo.domain.enums;

public enum RoomType {
	EASY(1,"easy"),
	MEDIUM(2,"medium"),
	HARD(3,"hard");
	
	private int id;
	private String description;
	
	private RoomType(int id, String description) {
		this.id = id;
		this.description = description;		
	}

	public int getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public static RoomType toEnum(Integer id) {
		if(id == null) {
           return null;
		}
		
		for (RoomType type : RoomType.values()) {
			if(id.equals(type.getId())) {
				return type;
			}
		}
	
		throw new IllegalArgumentException("Invalid ID" + id);		
	}	
}
