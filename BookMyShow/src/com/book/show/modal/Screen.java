package com.book.show.modal;

import java.util.ArrayList;
import java.util.List;

public class Screen {
	private Long id;
	private String screenName;
	private List<Seat> seats;

	public Screen(long id, String name) {
		this.id = id;
		this.screenName = name;
		this.seats = new ArrayList<>();
	}

	public void addSeat(Seat seat) {
		this.seats.add(seat);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getScreenName() {
		return screenName;
	}

	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}

	public List<Seat> getSeats() {
		return seats;
	}

	public void setSeats(List<Seat> seats) {
		this.seats = seats;
	}
}