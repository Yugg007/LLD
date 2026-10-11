package com.book.show;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.book.show.exception.SeatUnavailableException;
import com.book.show.modal.Booking;
import com.book.show.modal.City;
import com.book.show.modal.Movie;
import com.book.show.modal.MovieType;
import com.book.show.modal.Payment;
import com.book.show.modal.Screen;
import com.book.show.modal.Seat;
import com.book.show.modal.SeatCategory;
import com.book.show.modal.Show;
import com.book.show.modal.StandardSeatCategory;
import com.book.show.modal.Theater;
import com.book.show.modal.ThreeDHindiMovie;
import com.book.show.modal.TwoDHindiMovie;
import com.book.show.repository.InMemoryBookingRepository;
import com.book.show.repository.InMemoryCatalogRepository;
import com.book.show.repository.InMemoryShowRepository;
import com.book.show.service.BookingService;
import com.book.show.service.ShowService;
import com.book.show.service.SimulatedPaymentGateway;

public class BookMyShowApplication {

	public static void main(String args[]) throws Exception {

		// ---- wiring ----
		InMemoryCatalogRepository catalog = new InMemoryCatalogRepository();
		InMemoryShowRepository showRepository = new InMemoryShowRepository();
		InMemoryBookingRepository bookingRepository = new InMemoryBookingRepository();

		ShowService showService = new ShowService(catalog, showRepository);
		BookingService bookingService = new BookingService(showRepository, bookingRepository,
				new SimulatedPaymentGateway());

		// ---- master data ----
		MovieType twoDHindi = new TwoDHindiMovie();
		MovieType threeDHindi = new ThreeDHindiMovie();

		SeatCategory standard = new StandardSeatCategory("Standard", 250L);

		Movie movie1 = new Movie(1L, "War", 7200L, twoDHindi);
		Movie movie2 = new Movie(2L, "War2", 7200L, twoDHindi);
		Movie movie3 = new Movie(3L, "Avenger:Endgame", 10800L, threeDHindi);
		catalog.addMovie(movie1);
		catalog.addMovie(movie2);
		catalog.addMovie(movie3);

		// screen ids must be unique across the whole system
		Screen screen1 = new Screen(1L, "Screen1");
		Screen screen2 = new Screen(2L, "Screen2");
		Screen screen3 = new Screen(3L, "Screen3");
		Screen screen4 = new Screen(4L, "Screen1");
		Screen screen5 = new Screen(5L, "Screen2");

		addSeats(screen1, standard, 5, 10);
		addSeats(screen2, standard, 5, 10);
		addSeats(screen3, standard, 6, 12);
		addSeats(screen4, standard, 4, 8);
		addSeats(screen5, standard, 4, 8);

		Theater theater1 = new Theater(1L, "Inorbit Mall");
		theater1.addScreen(screen1);
		theater1.addScreen(screen2);
		theater1.addScreen(screen3);

		Theater theater2 = new Theater(2L, "Infinity Mall");
		theater2.addScreen(screen4);
		theater2.addScreen(screen5);

		City city1 = new City(1L, "Mumbai");
		city1.addTheater(theater1);
		city1.addTheater(theater2);
		catalog.addCity(city1); // register only after theaters/screens are added

		// ---- create shows ----
		Show show1 = showService.createShow(movie1, screen1, "09/10/2026", "15:00", "18:00");
		Show show2 = showService.createShow(movie1, screen1, "11/10/2026", "15:00", "18:00");
		Show show3 = showService.createShow(movie2, screen1, "10/10/2026", "15:00", "18:00");
		Show show4 = showService.createShow(movie3, screen1, "10/10/2026", "18:00", "21:00");
		Show show5 = showService.createShow(movie3, screen4, "10/10/2026", "20:00", "23:30");

		// ---- search: by location, by name, by time ----
		System.out.println("== Shows in Mumbai ==");
		print(showService, showService.searchShows("Mumbai", null, null, null, null));

		System.out.println("\n== Shows with 'war' in the name ==");
		print(showService, showService.searchShows(null, "war", null, null, null));

		System.out.println("\n== Mumbai, 10/10/2026, starting between 17:00 and 22:00 ==");
		print(showService, showService.searchShows("Mumbai", null, "10/10/2026", "17:00", "22:00"));

		System.out.println("\n== 'endgame' in Pune (no such city) ==");
		print(showService, showService.searchShows("Pune", "endgame", null, null, null));

		System.out.println("\n== Distinct movies in Mumbai on 10/10/2026 ==");
		for (Movie m : showService.searchMovies("Mumbai", null, "10/10/2026", null, null)) {
			System.out.println(m.getMovieName());
		}

		// ---- available seats ----
		List<Seat> free = bookingService.getAvailableSeats(show1.getShowId());
		System.out.println("\nShow " + show1.getShowId() + " available seats: " + free.size());

		// ---- book (hold) -> pay ----
		System.out.println("\n== Alice holds seats 1,2,3 ==");
		Booking alice = bookingService.bookSeats(show1.getShowId(), Arrays.asList(1L, 2L, 3L), "alice");
		System.out.println("Booking " + alice.getBookingId() + " " + alice.getStatus() + " seats " + alice.getSeatIds()
				+ " amount " + alice.getAmount());

		System.out.println("\n== Bob tries seats 3,4 (3 is held by Alice) ==");
		try {
			bookingService.bookSeats(show1.getShowId(), Arrays.asList(3L, 4L), "bob");
		} catch (SeatUnavailableException e) {
			System.out.println("Rejected: " + e.getMessage() + "  (seat 4 was rolled back)");
		}

		System.out.println("\n== Bob tries seats 4,5 ==");
		Booking bob = bookingService.bookSeats(show1.getShowId(), Arrays.asList(4L, 5L), "bob");
		System.out.println("Booking " + bob.getBookingId() + " " + bob.getStatus());

		System.out.println("\n== Payments ==");
		Payment p1 = bookingService.pay(alice.getBookingId(), "CARD");
		System.out.println("Alice payment " + p1.getPaymentId() + " " + p1.getStatus() + " booking is now "
				+ alice.getStatus());
		Payment p2 = bookingService.pay(bob.getBookingId(), "FAIL");
		System.out.println("Bob payment " + p2.getStatus() + " booking is now " + bob.getStatus());
		System.out.println("Alice pays again (idempotent): same payment id = "
				+ (bookingService.pay(alice.getBookingId(), "CARD").getPaymentId() == p1.getPaymentId()));

		System.out.println("\nAvailable seats now: " + bookingService.getAvailableSeats(show1.getShowId()).size()
				+ " (3 sold, Bob's 2 released)");

		// ---- race: 200 threads fight for ONE seat ----
		int threads = 2000;
		ExecutorService pool = Executors.newFixedThreadPool(threads);
		CountDownLatch ready = new CountDownLatch(threads);
		CountDownLatch go = new CountDownLatch(1);
		AtomicInteger winners = new AtomicInteger();
		for (int i = 0; i < threads; i++) {
			final String user = "user" + i;
			pool.submit(() -> {
				ready.countDown();
				try {
					go.await();
					bookingService.bookSeats(show2.getShowId(), Arrays.asList(20L), user);
					winners.incrementAndGet();
				} catch (SeatUnavailableException e) {
					// lost the race
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			});
		}
		ready.await();
		go.countDown();
		pool.shutdown();
		pool.awaitTermination(10, TimeUnit.SECONDS);
		System.out.println("\n== Race: " + threads + " users, 1 seat -> winners = " + winners.get() + " ==");

		bookingService.shutdown();
	}

	private static void print(ShowService showService, List<Show> shows) {
		if (shows.isEmpty()) {
			System.out.println("No shows found");
		}
		for (Show s : shows) {
			System.out.println(showService.describe(s));
		}
	}

	// Helper: fills a screen with rows x columns seats of the given category
	private static void addSeats(Screen screen, SeatCategory category, int rows, int columns) {
		long seatId = 1L;
		for (int r = 1; r <= rows; r++) {
			for (int c = 1; c <= columns; c++) {
				screen.addSeat(new Seat(seatId++, r, c, category));
			}
		}
	}
}