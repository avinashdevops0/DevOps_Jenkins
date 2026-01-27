package com.example.movietickets;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpServer;

public class MovieTicketServer {
    public static void main(String[] args) throws IOException {
        // Create HTTP server on port 8080
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        
        // Set up context handlers
        server.createContext("/", new FileHandler());
        
        // API endpoints
        server.createContext("/api/movies", new ApiHandler.MoviesHandler());
        server.createContext("/api/bookings", new ApiHandler.BookingsHandler());
        server.createContext("/api/seats", new ApiHandler.SeatsHandler());
        server.createContext("/api/payment", new ApiHandler.PaymentHandler());
        
        // Set executor (thread pool)
        server.setExecutor(Executors.newFixedThreadPool(10));
        
        // Start the server
        server.start();
        
        System.out.println("=========================================");
        System.out.println("Movie Tickets Booking Server Started");
        System.out.println("Open: http://localhost:8080");
        System.out.println("API Endpoints:");
        System.out.println("  GET  /api/movies      - List all movies");
        System.out.println("  GET  /api/bookings    - List all bookings");
        System.out.println("  POST /api/bookings    - Create booking");
        System.out.println("  GET  /api/seats?movieId={id} - Get available seats");
        System.out.println("  POST /api/payment     - Process payment");
        System.out.println("=========================================");
    }
}