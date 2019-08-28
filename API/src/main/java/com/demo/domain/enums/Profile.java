package com.demo.domain.enums;

public enum Profile {

	ADMIN(1,"ROLE_ADMIN"),
	BASIC_USER(2,"ROLE_BASIC_USER");
	
	private int id;
	private String description;
	
	private Profile(int id, String description) {
		this.id = id;
		this.description = description;		
	}

	public int getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public static Profile toEnum(Integer id) {
		if(id == null) {
           return null;
		}
		
		for (Profile p : Profile.values()) {
			if(id.equals(p.getId())) {
				return p;
			}
		}
	
		throw new IllegalArgumentException("Invalid ID" + id);		
	}	
}
