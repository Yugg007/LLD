package com.book.show.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.book.show.modal.City;
import com.book.show.modal.Movie;
import com.book.show.modal.Screen;
import com.book.show.modal.Theater;

/** Reference data: cities (with their theaters/screens) and movies. Rarely written, read very often. */
public class InMemoryCatalogRepository {
	private final ConcurrentHashMap<Long, City> cities = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<String, City> citiesByName = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<Long, Long> screenToCity = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<Long, Movie> movies = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<Long, String> movieLowerNames = new ConcurrentHashMap<>();

	/** Register a city AFTER all its theaters and screens have been added. Screen ids must be globally unique. */
	public void addCity(City city) {
		for (Theater theater : city.getTheater()) {
			for (Screen screen : theater.getScreen()) {
				Long previous = screenToCity.putIfAbsent(screen.getId(), city.getId());
				if (previous != null && !previous.equals(city.getId())) {
					throw new IllegalArgumentException("Screen id " + screen.getId() + " is already used in another city");
				}
			}
		}
		cities.put(city.getId(), city);
		citiesByName.put(city.getName().trim().toLowerCase(Locale.ROOT), city);
	}

	public void addMovie(Movie movie) {
		Movie previous = movies.putIfAbsent(movie.getId(), movie);
		if (previous != null && previous != movie) {
			throw new IllegalArgumentException("Movie id " + movie.getId() + " is already used by '"
					+ previous.getMovieName() + "'");
		}
		movieLowerNames.put(movie.getId(), movie.getMovieName().toLowerCase(Locale.ROOT));
	}

	public Optional<City> findCityByName(String name) {
		return Optional.ofNullable(citiesByName.get(name.trim().toLowerCase(Locale.ROOT)));
	}

	public Optional<Long> findCityIdByScreen(Long screenId) {
		return Optional.ofNullable(screenToCity.get(screenId));
	}

	public Optional<Movie> findMovieById(Long id) {
		return Optional.ofNullable(movies.get(id));
	}

	/** Case-insensitive "contains" match on the movie name. */
	public List<Movie> findMoviesByName(String query) {
		String q = query.trim().toLowerCase(Locale.ROOT);
		List<Movie> result = new ArrayList<>();
		for (Map.Entry<Long, String> e : movieLowerNames.entrySet()) {
			if (e.getValue().contains(q)) {
				Movie m = movies.get(e.getKey());
				if (m != null) {
					result.add(m);
				}
			}
		}
		return result;
	}
}