package com.example.movietickets;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class ApiHandler {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    
    // Movies Handler
    public static class MoviesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                List<MovieService.Movie> movies = MovieService.getAllMovies();
                String response = gson.toJson(movies);
                
                sendResponse(exchange, 200, response);
            } else if ("OPTIONS".equals(exchange.getRequestMethod())) {
                handleOptions(exchange);
            } else {
                sendError(exchange, 405, "Method not allowed");
            }
        }
    }
    
    // Bookings Handler
    public static class BookingsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                handleOptions(exchange);
                return;
            }
            
            switch (exchange.getRequestMethod()) {
                case "GET":
                    handleGetBookings(exchange);
                    break;
                case "POST":
                    handleCreateBooking(exchange);
                    break;
                default:
                    sendError(exchange, 405, "Method not allowed");
            }
        }
        
        private void handleGetBookings(HttpExchange exchange) throws IOException {
            List<MovieService.Booking> bookings = MovieService.getAllBookings();
            String response = gson.toJson(bookings);
            sendResponse(exchange, 200, response);
        }
        
        private void handleCreateBooking(HttpExchange exchange) throws IOException {
            try {
                String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                MovieService.BookingRequest bookingRequest = gson.fromJson(requestBody, MovieService.BookingRequest.class);
                
                // Validate request
                if (bookingRequest.getMovieId() == 0 || 
                    bookingRequest.getSeatNumbers() == null || 
                    bookingRequest.getSeatNumbers().isEmpty() || 
                    bookingRequest.getCustomerName() == null || 
                    bookingRequest.getCustomerName().trim().isEmpty()) {
                    sendError(exchange, 400, "Invalid booking request");
                    return;
                }
                
                // Book seats
                boolean seatsBooked = MovieService.bookSeats(
                    bookingRequest.getMovieId(), 
                    bookingRequest.getSeatNumbers()
                );
                
                if (!seatsBooked) {
                    sendError(exchange, 409, "Seats not available");
                    return;
                }
                
                // Create booking
                MovieService.Booking booking = MovieService.createBooking(bookingRequest);
                String response = gson.toJson(booking);
                sendResponse(exchange, 201, response);
                
            } catch (Exception e) {
                sendError(exchange, 500, "Internal server error: " + e.getMessage());
            }
        }
    }
    
    // Seats Handler
    public static class SeatsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                handleOptions(exchange);
                return;
            }
            
            if ("GET".equals(exchange.getRequestMethod())) {
                String query = exchange.getRequestURI().getQuery();
                if (query == null || !query.contains("movieId=")) {
                    sendError(exchange, 400, "movieId parameter required");
                    return;
                }
                
                try {
                    String[] params = query.split("=");
                    int movieId = Integer.parseInt(params[1]);
                    
                    List<MovieService.Seat> seats = MovieService.getAvailableSeats(movieId);
                    String response = gson.toJson(seats);
                    sendResponse(exchange, 200, response);
                } catch (NumberFormatException e) {
                    sendError(exchange, 400, "Invalid movie ID");
                }
            } else {
                sendError(exchange, 405, "Method not allowed");
            }
        }
    }
    
    // Payment Handler
    public static class PaymentHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                handleOptions(exchange);
                return;
            }
            
            if ("POST".equals(exchange.getRequestMethod())) {
                try {
                    String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    
                    // Simulate payment processing
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("transactionId", "TXN" + System.currentTimeMillis());
                    response.put("message", "Payment processed successfully");
                    response.put("timestamp", new Date().toString());
                    
                    String jsonResponse = gson.toJson(response);
                    sendResponse(exchange, 200, jsonResponse);
                } catch (Exception e) {
                    sendError(exchange, 500, "Payment processing failed");
                }
            } else {
                sendError(exchange, 405, "Method not allowed");
            }
        }
    }
    
    // Helper methods
    private static void sendResponse(HttpExchange exchange, int code, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
        exchange.sendResponseHeaders(code, response.getBytes().length);
        
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
    
    private static void sendError(HttpExchange exchange, int code, String message) throws IOException {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        String response = gson.toJson(error);
        sendResponse(exchange, code, response);
    }
    
    private static void handleOptions(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
        exchange.sendResponseHeaders(200, -1);
    }
}