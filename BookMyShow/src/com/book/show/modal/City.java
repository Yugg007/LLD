package com.book.show.modal;

import java.util.ArrayList;
import java.util.List;

public class City {
	private Long id;
	private String name;
	private List<Theater> theater;

	public City(Long id, String name) {
		// TODO Auto-generated constructor stub
		this.id = id;
		this.name = name;
		this.theater = new ArrayList<>();
	}
	
	public void addTheater(Theater tr) {
		theater.add(tr);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Theater> getTheater() {
		return theater;
	}

	public void setTheater(List<Theater> theater) {
		this.theater = theater;
	}

}
