package com.book.show.modal;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A movie playing on a screen at a time. Everything except the ids is immutable after creation, so a Show can be
 * read by any number of threads without locking (the only mutable state is inside each ShowSeat, via CAS).
 */
public class Show {
	private Long showId;
	private Long cityId;
	private final Movie movie;
	private final Screen screen;
	private final LocalDateTime startTime;
	private final LocalDateTime endTime;
	private final List<ShowSeat> showSeats; // one per Seat in the screen
	private final Map<Long, ShowSeat> showSeatById; // O(1) lookup by seat id

	public Show(Movie movie, Screen screen, LocalDateTime startTime, LocalDateTime endTime,
			List<ShowSeat> showSeats) {
		this.movie = movie;
		this.screen = screen;
		this.startTime = startTime;
		this.endTime = endTime;
		this.showSeats = Collections.unmodifiableList(showSeats);
		Map<Long, ShowSeat> map = new LinkedHashMap<>();
		for (ShowSeat ss : showSeats) {
			map.put(ss.getSeat().getId(), ss);
		}
		this.showSeatById = Collections.unmodifiableMap(map);
	}

	public ShowSeat getShowSeat(Long seatId) {
		return showSeatById.get(seatId);
	}

	// set once by the repository / service BEFORE the show is published to other threads
	public Long getShowId() {
		return showId;
	}

	public void setShowId(Long showId) {
		this.showId = showId;
	}

	public Long getCityId() {
		return cityId;
	}

	public void setCityId(Long cityId) {
		this.cityId = cityId;
	}

	public Movie getMovie() {
		return movie;
	}

	public Screen getScreen() {
		return screen;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public LocalDateTime getEndTime() {
		return endTime;
	}

	public List<ShowSeat> getShowSeats() {
		return showSeats;
	}
}