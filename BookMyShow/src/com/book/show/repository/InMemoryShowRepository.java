package com.book.show.repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;

import com.book.show.modal.Screen;
import com.book.show.modal.Show;

/**
 * Shows plus secondary indexes, so every query is a map lookup / range scan instead of a scan over all shows:
 * byTime (all shows), byCity, byMovie and byScreen. Indexes are sorted by start time, so "shows between 6pm and
 * 10pm" is a sub-range of a skip list.
 */
public class InMemoryShowRepository implements Repository<Show> {

	/** Sort key: start time, then show id (to keep keys unique). */
	private static final class ShowKey implements Comparable<ShowKey> {
		final LocalDateTime start;
		final long showId;

		ShowKey(LocalDateTime start, long showId) {
			this.start = start;
			this.showId = showId;
		}

		static ShowKey of(Show show) {
			return new ShowKey(show.getStartTime(), show.getShowId());
		}

		@Override
		public int compareTo(ShowKey o) {
			int c = start.compareTo(o.start);
			return c != 0 ? c : Long.compare(showId, o.showId);
		}
	}

	private static final Comparator<Show> BY_START_THEN_ID = Comparator.comparing(Show::getStartTime)
			.thenComparing(Show::getShowId);

	private final ConcurrentHashMap<Long, Show> store = new ConcurrentHashMap<>();
	private final AtomicLong id = new AtomicLong(1);

	private final ConcurrentSkipListMap<ShowKey, Show> byTime = new ConcurrentSkipListMap<>();
	private final ConcurrentHashMap<Long, ConcurrentSkipListMap<ShowKey, Show>> byCity = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<Long, ConcurrentSkipListMap<ShowKey, Show>> byMovie = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<Long, ConcurrentSkipListMap<LocalDateTime, Show>> byScreen = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<Long, Object> screenLocks = new ConcurrentHashMap<>();

	@Override
	public Show save(Show show) {
		if (show.getShowId() == null) {
			show.setShowId(id.getAndIncrement());
		}
		store.put(show.getShowId(), show);
		index(show);
		return show;
	}

	/**
	 * Saves the show only if its screen has no overlapping show. The check and the save happen under a lock that
	 * belongs to THAT screen only, so creating shows on different screens never waits on each other.
	 */
	public boolean saveIfScreenFree(Show show) {
		Object lock = screenLocks.computeIfAbsent(show.getScreen().getId(), k -> new Object());
		synchronized (lock) {
			if (!isScreenFree(show.getScreen().getId(), show.getStartTime(), show.getEndTime())) {
				return false;
			}
			save(show);
			return true;
		}
	}

	private boolean isScreenFree(Long screenId, LocalDateTime start, LocalDateTime end) {
		NavigableMap<LocalDateTime, Show> shows = byScreen.get(screenId);
		if (shows == null) {
			return true;
		}
		// shows on one screen never overlap each other, so only the two neighbours of the new start can clash
		Map.Entry<LocalDateTime, Show> before = shows.floorEntry(start);
		if (before != null && before.getValue().getEndTime().isAfter(start)) {
			return false;
		}
		Map.Entry<LocalDateTime, Show> after = shows.ceilingEntry(start);
		return after == null || !after.getKey().isBefore(end);
	}

	private void index(Show show) {
		ShowKey key = ShowKey.of(show);
		byTime.put(key, show);
		if (show.getCityId() != null) {
			byCity.computeIfAbsent(show.getCityId(), k -> new ConcurrentSkipListMap<>()).put(key, show);
		}
		byMovie.computeIfAbsent(show.getMovie().getId(), k -> new ConcurrentSkipListMap<>()).put(key, show);
		byScreen.computeIfAbsent(show.getScreen().getId(), k -> new ConcurrentSkipListMap<>())
				.put(show.getStartTime(), show);
	}

	private void unindex(Show show) {
		ShowKey key = ShowKey.of(show);
		byTime.remove(key);
		if (show.getCityId() != null) {
			ConcurrentSkipListMap<ShowKey, Show> m = byCity.get(show.getCityId());
			if (m != null) {
				m.remove(key);
			}
		}
		ConcurrentSkipListMap<ShowKey, Show> mm = byMovie.get(show.getMovie().getId());
		if (mm != null) {
			mm.remove(key);
		}
		ConcurrentSkipListMap<LocalDateTime, Show> ms = byScreen.get(show.getScreen().getId());
		if (ms != null) {
			ms.remove(show.getStartTime());
		}
	}

	@Override
	public List<Show> findAll() {
		return new ArrayList<>(store.values());
	}

	@Override
	public void delete(Long showId) {
		Show show = store.remove(showId);
		if (show != null) {
			unindex(show);
		}
	}

	@Override
	public Optional<Show> findById(Long showId) {
		return Optional.ofNullable(store.get(showId));
	}

	public List<Show> findByScreen(Screen screen) {
		ConcurrentSkipListMap<LocalDateTime, Show> shows = byScreen.get(screen.getId());
		return shows == null ? new ArrayList<>() : new ArrayList<>(shows.values());
	}

	/**
	 * Search shows. Every argument is optional (null = no filter). Shows are matched when their START time is
	 * within [from, to]. Results are sorted by start time and capped at 'limit'.
	 */
	public List<Show> search(Long cityId, Collection<Long> movieIds, LocalDateTime from, LocalDateTime to,
			int limit) {
		List<Show> result = new ArrayList<>();

		if (movieIds != null) {
			for (Long movieId : movieIds) {
				NavigableMap<ShowKey, Show> shows = byMovie.get(movieId);
				if (shows != null) {
					collect(range(shows, from, to), cityId, limit, result);
				}
			}
			result.sort(BY_START_THEN_ID);
			return result.size() > limit ? new ArrayList<>(result.subList(0, limit)) : result;
		}

		NavigableMap<ShowKey, Show> source = (cityId != null) ? byCity.get(cityId) : byTime;
		if (source != null) {
			collect(range(source, from, to), null, limit, result);
		}
		return result;
	}

	private NavigableMap<ShowKey, Show> range(NavigableMap<ShowKey, Show> source, LocalDateTime from,
			LocalDateTime to) {
		ShowKey lo = new ShowKey(from == null ? LocalDateTime.MIN : from, Long.MIN_VALUE);
		ShowKey hi = new ShowKey(to == null ? LocalDateTime.MAX : to, Long.MAX_VALUE);
		if (lo.compareTo(hi) > 0) {
			return Collections.emptyNavigableMap();
		}
		return source.subMap(lo, true, hi, true);
	}

	private void collect(NavigableMap<ShowKey, Show> shows, Long cityId, int max, List<Show> out) {
		int added = 0;
		for (Show show : shows.values()) {
			if (cityId != null && !cityId.equals(show.getCityId())) {
				continue;
			}
			out.add(show);
			if (++added >= max) {
				break;
			}
		}
	}
}