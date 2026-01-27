package com.example.movietickets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MovieService {
    
    // In-memory data storage
    private static Map<Integer, Movie> movies = new ConcurrentHashMap<>();
    private static Map<String, Booking> bookings = new ConcurrentHashMap<>();
    private static Map<Integer, List<Seat>> movieSeats = new ConcurrentHashMap<>();
    private static int nextBookingId = 1000;
    
    static {
        initializeData();
    }
    
    private static void initializeData() {
        // Initialize movies
        movies.put(1, new Movie(1, "Avengers: Endgame", "Action", 
            "The epic conclusion to the Infinity Saga.", 181, 
            "English", 4.8, "PG-13", "Anthony Russo", 
            Arrays.asList("Robert Downey Jr.", "Chris Evans", "Scarlett Johansson"),
            "2019-04-26", 12.99));
        
        movies.put(2, new Movie(2, "The Batman", "Action/Crime", 
            "Batman ventures into Gotham City's underworld.", 176, 
            "English", 4.5, "PG-13", "Matt Reeves", 
            Arrays.asList("Robert Pattinson", "Zoe Kravitz", "Paul Dano"),
            "2022-03-04", 11.99));
        
        movies.put(3, new Movie(3, "Dune: Part Two", "Sci-Fi/Adventure", 
            "Paul Atreides unites with Fremen for revenge.", 166, 
            "English", 4.7, "PG-13", "Denis Villeneuve", 
            Arrays.asList("Timothée Chalamet", "Zendaya", "Rebecca Ferguson"),
            "2024-03-01", 13.99));
        
        movies.put(4, new Movie(4, "Elemental", "Animation/Comedy", 
            "In a city where fire, water, land and air residents live together.", 102, 
            "English", 4.3, "PG", "Peter Sohn", 
            Arrays.asList("Leah Lewis", "Mamoudou Athie"),
            "2023-06-16", 9.99));
        
        movies.put(5, new Movie(5, "Mission: Impossible", "Action/Thriller", 
            "Ethan Hunt and his IMF team must track down a terrifying new weapon.", 163, 
            "English", 4.6, "PG-13", "Christopher McQuarrie", 
            Arrays.asList("Tom Cruise", "Hayley Atwell", "Ving Rhames"),
            "2023-07-12", 12.49));
        
        // Initialize seats for each movie
        for (Movie movie : movies.values()) {
            List<Seat> seats = new ArrayList<>();
            for (int i = 1; i <= 50; i++) {
                seats.add(new Seat(i, "A" + i, true));
            }
            movieSeats.put(movie.getId(), seats);
        }
    }
    
    // Movie related methods
    public static List<Movie> getAllMovies() {
        return new ArrayList<>(movies.values());
    }
    
    public static Movie getMovieById(int id) {
        return movies.get(id);
    }
    
    // Seat related methods
    public static List<Seat> getAvailableSeats(int movieId) {
        List<Seat> seats = movieSeats.get(movieId);
        if (seats == null) return new ArrayList<>();
        return seats.stream().filter(Seat::isAvailable).toList();
    }
    
    public static synchronized boolean bookSeats(int movieId, List<Integer> seatNumbers) {
        List<Seat> seats = movieSeats.get(movieId);
        if (seats == null) return false;
        
        // Check if all seats are available
        for (int seatNum : seatNumbers) {
            if (seatNum < 1 || seatNum > seats.size()) return false;
            Seat seat = seats.get(seatNum - 1);
            if (!seat.isAvailable()) return false;
        }
        
        // Book all seats
        for (int seatNum : seatNumbers) {
            Seat seat = seats.get(seatNum - 1);
            seat.setAvailable(false);
        }
        return true;
    }
    
    // Booking related methods
    public static synchronized Booking createBooking(BookingRequest request) {
        String bookingId = "BK" + (nextBookingId++);
        double totalAmount = calculateTotalPrice(request.getMovieId(), request.getSeatNumbers().size());
        
        Booking booking = new Booking(
            bookingId,
            request.getMovieId(),
            request.getSeatNumbers(),
            request.getCustomerName(),
            request.getCustomerEmail(),
            request.getCustomerPhone(),
            new Date(),
            totalAmount,
            "CONFIRMED"
        );
        bookings.put(bookingId, booking);
        return booking;
    }
    
    public static Booking getBooking(String bookingId) {
        return bookings.get(bookingId);
    }
    
    public static List<Booking> getAllBookings() {
        return new ArrayList<>(bookings.values());
    }
    
    private static double calculateTotalPrice(int movieId, int seatCount) {
        Movie movie = movies.get(movieId);
        if (movie == null) return 0;
        return movie.getPrice() * seatCount;
    }
    
    // Data Models
    public static class Movie {
        private int id;
        private String title;
        private String genre;
        private String description;
        private int duration;
        private String language;
        private double rating;
        private String certificate;
        private String director;
        private List<String> cast;
        private String releaseDate;
        private double price;
        
        public Movie(int id, String title, String genre, String description, int duration, 
                    String language, double rating, String certificate, String director, 
                    List<String> cast, String releaseDate, double price) {
            this.id = id;
            this.title = title;
            this.genre = genre;
            this.description = description;
            this.duration = duration;
            this.language = language;
            this.rating = rating;
            this.certificate = certificate;
            this.director = director;
            this.cast = cast;
            this.releaseDate = releaseDate;
            this.price = price;
        }
        
        // Getters
        public int getId() { return id; }
        public String getTitle() { return title; }
        public String getGenre() { return genre; }
        public String getDescription() { return description; }
        public int getDuration() { return duration; }
        public String getLanguage() { return language; }
        public double getRating() { return rating; }
        public String getCertificate() { return certificate; }
        public String getDirector() { return director; }
        public List<String> getCast() { return cast; }
        public String getReleaseDate() { return releaseDate; }
        public double getPrice() { return price; }
    }
    
    public static class Seat {
        private int id;
        private String seatNumber;
        private boolean available;
        
        public Seat(int id, String seatNumber, boolean available) {
            this.id = id;
            this.seatNumber = seatNumber;
            this.available = available;
        }
        
        public int getId() { return id; }
        public String getSeatNumber() { return seatNumber; }
        public boolean isAvailable() { return available; }
        public void setAvailable(boolean available) { this.available = available; }
    }
    
    public static class Booking {
        private String bookingId;
        private int movieId;
        private List<Integer> seatNumbers;
        private String customerName;
        private String customerEmail;
        private String customerPhone;
        private Date bookingDate;
        private double totalAmount;
        private String status;
        
        public Booking(String bookingId, int movieId, List<Integer> seatNumbers, 
                      String customerName, String customerEmail, String customerPhone,
                      Date bookingDate, double totalAmount, String status) {
            this.bookingId = bookingId;
            this.movieId = movieId;
            this.seatNumbers = seatNumbers;
            this.customerName = customerName;
            this.customerEmail = customerEmail;
            this.customerPhone = customerPhone;
            this.bookingDate = bookingDate;
            this.totalAmount = totalAmount;
            this.status = status;
        }
        
        // Getters
        public String getBookingId() { return bookingId; }
        public int getMovieId() { return movieId; }
        public List<Integer> getSeatNumbers() { return seatNumbers; }
        public String getCustomerName() { return customerName; }
        public String getCustomerEmail() { return customerEmail; }
        public String getCustomerPhone() { return customerPhone; }
        public Date getBookingDate() { return bookingDate; }
        public double getTotalAmount() { return totalAmount; }
        public String getStatus() { return status; }
    }
    
    public static class BookingRequest {
        private int movieId;
        private List<Integer> seatNumbers;
        private String customerName;
        private String customerEmail;
        private String customerPhone;
        
        // Getters and Setters
        public int getMovieId() { return movieId; }
        public void setMovieId(int movieId) { this.movieId = movieId; }
        
        public List<Integer> getSeatNumbers() { return seatNumbers; }
        public void setSeatNumbers(List<Integer> seatNumbers) { this.seatNumbers = seatNumbers; }
        
        public String getCustomerName() { return customerName; }
        public void setCustomerName(String customerName) { this.customerName = customerName; }
        
        public String getCustomerEmail() { return customerEmail; }
        public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
        
        public String getCustomerPhone() { return customerPhone; }
        public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
    }
}