package com.book.show.modal;

public class Movie {
	private Long id;
	private String movieName;
	private Long duration;
	private MovieType movieType;
	
	

	public Movie(Long id, String movieName, Long duration, MovieType movieType) {
		super();
		this.id = id;
		this.movieName = movieName;
		this.duration = duration;
		this.movieType = movieType;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMovieName() {
		return movieName;
	}

	public void setMovieName(String movieName) {
		this.movieName = movieName;
	}

	public Long getDuration() {
		return duration;
	}

	public void setDuration(Long duration) {
		this.duration = duration;
	}

	public MovieType getMovieType() {
		return movieType;
	}

	public void setMovieType(MovieType movieType) {
		this.movieType = movieType;
	}

}
