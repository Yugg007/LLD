package com.book.show.modal;

import java.util.ArrayList;
import java.util.List;

public class Theater {
	private Long id;
	private String name;
	private List<Screen> screen;

	public Theater(long id, String name) {
		// TODO Auto-generated constructor stub
		this.id = id;
		this.name = name;
		this.screen = new ArrayList<>();
	}
	
	public void addScreen(Screen screen) {
		this.screen.add(screen);
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

	public List<Screen> getScreen() {
		return screen;
	}

	public void setScreen(List<Screen> screen) {
		this.screen = screen;
	}

}
