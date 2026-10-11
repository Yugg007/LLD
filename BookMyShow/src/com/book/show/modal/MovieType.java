package com.book.show.modal;

public abstract class MovieType {
	private String type;
	
	
	MovieType(String type){
		this.type = type;
	}
	

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
