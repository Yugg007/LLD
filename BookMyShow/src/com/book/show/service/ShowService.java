package com.book.show.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.book.show.modal.City;
import com.book.show.modal.Movie;
import com.book.show.modal.Screen;
import com.book.show.modal.Seat;
import com.book.show.modal.Show;
import com.book.show.modal.ShowSeat;
import com.book.show.repository.InMemoryCatalogRepository;
import com.book.show.repository.InMemoryShowRepository;
import com.book.show.utils.TimeUtil;

public class ShowService {
	private static final int SEARCH_LIMIT = 100;
	private static final DateTimeFormatter DISPLAY = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

	private final InMemoryCatalogRepository catalog;
	private final InMemoryShowRepository showRepository;

	public ShowService(InMemoryCatalogRepository catalog, InMemoryShowRepository showRepository) {
		this.catalog = catalog;
		this.showRepository = showRepository;
	}

	public Show createShow(Movie movie, Screen screen, String date, String startTime, String endTime) {
		LocalDateTime start = TimeUtil.toDateTime(date, startTime);
		LocalDateTime end = TimeUtil.toDateTime(date, endTime);

		if (!end.isAfter(start)) {
			throw new IllegalArgumentException("End time must be after start time");
		}
		if (Duration.between(start, end).getSeconds() < movie.getDuration()) {
			throw new IllegalArgumentException("Show window is shorter than the movie duration");
		}
		catalog.findMovieById(movie.getId())
				.orElseThrow(() -> new IllegalArgumentException("Movie is not registered in the catalog"));
		Long cityId = catalog.findCityIdByScreen(screen.getId())
				.orElseThrow(() -> new IllegalArgumentException("Screen is not part of any registered city"));

		Show show = new Show(movie, screen, start, end, createShowSeats(screen.getSeats()));
		show.setCityId(cityId);

		// overlap check + save are atomic per screen (see InMemoryShowRepository.saveIfScreenFree)
		if (!showRepository.saveIfScreenFree(show)) {
			throw new IllegalStateException(screen.getScreenName() + " is already booked between "
					+ start.format(DISPLAY) + " and " + end.format(DISPLAY));
		}
		return show;
	}

	/**
	 * Find shows. Every argument is optional (null/blank = no filter).
	 *
	 * @param city      city name, e.g. "Mumbai"
	 * @param movieName part of the movie name, case-insensitive
	 * @param date      dd/MM/yyyy; when given, only shows STARTING that day (within fromTime..toTime) are returned
	 * @param fromTime  HH:mm, defaults to 00:00 (only used together with date)
	 * @param toTime    HH:mm, defaults to 23:59 (only used together with date)
	 */
	public List<Show> searchShows(String city, String movieName, String date, String fromTime, String toTime) {
		Long cityId = null;
		if (!isBlank(city)) {
			City found = catalog.findCityByName(city).orElse(null);
			if (found == null) {
				return Collections.emptyList();
			}
			cityId = found.getId();
		}

		List<Long> movieIds = null;
		if (!isBlank(movieName)) {
			movieIds = catalog.findMoviesByName(movieName).stream().map(Movie::getId).collect(Collectors.toList());
			if (movieIds.isEmpty()) {
				return Collections.emptyList();
			}
		}

		LocalDateTime from = null;
		LocalDateTime to = null;
		if (!isBlank(date)) {
			from = TimeUtil.toDateTime(date, isBlank(fromTime) ? "00:00" : fromTime);
			to = TimeUtil.toDateTime(date, isBlank(toTime) ? "23:59" : toTime);
		}
		return showRepository.search(cityId, movieIds, from, to, SEARCH_LIMIT);
	}

	/** Same filters as searchShows, but returns each matching movie once. */
	public List<Movie> searchMovies(String city, String movieName, String date, String fromTime, String toTime) {
		Map<Long, Movie> distinct = new LinkedHashMap<>();
		for (Show show : searchShows(city, movieName, date, fromTime, toTime)) {
			distinct.putIfAbsent(show.getMovie().getId(), show.getMovie());
		}
		return new ArrayList<>(distinct.values());
	}

	public String describe(Show show) {
		return "Show " + show.getShowId() + " | " + show.getMovie().getMovieName() + " ("
				+ show.getMovie().getMovieType().getType() + ") | " + show.getScreen().getScreenName() + " | "
				+ show.getStartTime().format(DISPLAY) + " - " + show.getEndTime().format(DISPLAY);
	}

	private List<ShowSeat> createShowSeats(List<Seat> seats) {
		List<ShowSeat> showSeats = new ArrayList<>(seats.size());
		for (Seat seat : seats) {
			showSeats.add(new ShowSeat(seat));
		}
		return showSeats;
	}

	private static boolean isBlank(String s) {
		return s == null || s.trim().isEmpty();
	}
}