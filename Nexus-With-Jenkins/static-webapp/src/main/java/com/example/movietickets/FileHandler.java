package com.example.movietickets;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class FileHandler implements HttpHandler {
    
    private static final String WEB_ROOT = "src/main/resources/web/";
    private static final String DEFAULT_FILE = "index.html";
    
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestPath = exchange.getRequestURI().getPath();
        
        // Skip API requests - they're handled separately
        if (requestPath.startsWith("/api/")) {
            exchange.sendResponseHeaders(404, 0);
            exchange.close();
            return;
        }
        
        // Handle root path
        if (requestPath.equals("/") || requestPath.isEmpty()) {
            requestPath = DEFAULT_FILE;
        } else {
            // Remove leading slash for file path
            if (requestPath.startsWith("/")) {
                requestPath = requestPath.substring(1);
            }
        }
        
        // Special case: if no file extension, try adding .html
        if (!requestPath.contains(".") && !requestPath.endsWith("/")) {
            requestPath = requestPath + ".html";
        }
        
        // Construct file path
        Path filePath = Paths.get(WEB_ROOT, requestPath).normalize().toAbsolutePath();
        Path webRootPath = Paths.get(WEB_ROOT).normalize().toAbsolutePath();
        
        // Security check: ensure file is within WEB_ROOT
        if (!filePath.startsWith(webRootPath)) {
            send404(exchange, "Access denied");
            return;
        }
        
        // Check if file exists
        if (Files.exists(filePath) && !Files.isDirectory(filePath)) {
            try {
                // Determine content type
                String contentType = getContentType(requestPath);
                
                // Read file content
                byte[] fileContent = Files.readAllBytes(filePath);
                
                // Set headers
                exchange.getResponseHeaders().set("Content-Type", contentType);
                exchange.getResponseHeaders().set("Cache-Control", "no-cache");
                exchange.sendResponseHeaders(200, fileContent.length);
                
                // Send response
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(fileContent);
                }
            } catch (IOException e) {
                sendError(exchange, 500, "Error reading file: " + e.getMessage());
            }
        } else {
            // File not found
            send404(exchange, "File not found: " + requestPath);
        }
    }
    
    private String getContentType(String filename) {
        if (filename.endsWith(".html") || filename.endsWith(".htm")) {
            return "text/html; charset=UTF-8";
        } else if (filename.endsWith(".css")) {
            return "text/css";
        } else if (filename.endsWith(".js")) {
            return "application/javascript";
        } else if (filename.endsWith(".png")) {
            return "image/png";
        } else if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (filename.endsWith(".gif")) {
            return "image/gif";
        } else if (filename.endsWith(".ico")) {
            return "image/x-icon";
        } else if (filename.endsWith(".json")) {
            return "application/json";
        } else if (filename.endsWith(".txt")) {
            return "text/plain";
        } else {
            return "application/octet-stream";
        }
    }
    
    private void send404(HttpExchange exchange, String message) throws IOException {
        String response = "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "    <title>404 - Movie Tickets</title>\n" +
            "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
            "    <style>\n" +
            "        body { \n" +
            "            font-family: Arial, sans-serif; \n" +
            "            margin: 0; \n" +
            "            padding: 20px; \n" +
            "            background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);\n" +
            "            min-height: 100vh;\n" +
            "            display: flex;\n" +
            "            align-items: center;\n" +
            "            justify-content: center;\n" +
            "        }\n" +
            "        .container { \n" +
            "            max-width: 600px; \n" +
            "            text-align: center; \n" +
            "            background: white;\n" +
            "            padding: 40px;\n" +
            "            border-radius: 10px;\n" +
            "            box-shadow: 0 4px 15px rgba(0,0,0,0.1);\n" +
            "        }\n" +
            "        h1 { \n" +
            "            color: #e63946; \n" +
            "            font-size: 48px; \n" +
            "            margin-bottom: 20px; \n" +
            "        }\n" +
            "        p { \n" +
            "            color: #666; \n" +
            "            font-size: 18px; \n" +
            "            margin-bottom: 30px; \n" +
            "        }\n" +
            "        .btn { \n" +
            "            display: inline-block; \n" +
            "            padding: 12px 24px; \n" +
            "            background: #e63946; \n" +
            "            color: white; \n" +
            "            text-decoration: none; \n" +
            "            border-radius: 5px; \n" +
            "            font-weight: bold; \n" +
            "            border: none;\n" +
            "            cursor: pointer;\n" +
            "            font-size: 16px;\n" +
            "        }\n" +
            "        .btn:hover { \n" +
            "            background: #c1121f; \n" +
            "        }\n" +
            "        .error-details {\n" +
            "            background: #f8f9fa;\n" +
            "            padding: 15px;\n" +
            "            border-radius: 5px;\n" +
            "            margin: 20px 0;\n" +
            "            font-family: monospace;\n" +
            "            font-size: 14px;\n" +
            "            color: #666;\n" +
            "            text-align: left;\n" +
            "        }\n" +
            "        .icon {\n" +
            "            font-size: 64px;\n" +
            "            color: #e63946;\n" +
            "            margin-bottom: 20px;\n" +
            "        }\n" +
            "    </style>\n" +
            "    <link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css\">\n" +
            "</head>\n" +
            "<body>\n" +
            "    <div class=\"container\">\n" +
            "        <div class=\"icon\">\n" +
            "            <i class=\"fas fa-film\"></i>\n" +
            "        </div>\n" +
            "        <h1>404</h1>\n" +
            "        <p>Oops! The page you're looking for doesn't exist.</p>\n" +
            "        <div class=\"error-details\">\n" +
            "            <i class=\"fas fa-info-circle\"></i> " + message + "\n" +
            "        </div>\n" +
            "        <button onclick=\"window.location.href='/'\"" +
            "                class=\"btn\">\n" +
            "            <i class=\"fas fa-home\"></i> Go to Homepage\n" +
            "        </button>\n" +
            "    </div>\n" +
            "</body>\n" +
            "</html>";
        
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(404, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
    
    private void sendError(HttpExchange exchange, int code, String message) throws IOException {
        String response = "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "    <title>" + code + " - Error</title>\n" +
            "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
            "    <style>\n" +
            "        body { \n" +
            "            font-family: Arial, sans-serif; \n" +
            "            margin: 0; \n" +
            "            padding: 20px; \n" +
            "            background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);\n" +
            "            min-height: 100vh;\n" +
            "            display: flex;\n" +
            "            align-items: center;\n" +
            "            justify-content: center;\n" +
            "        }\n" +
            "        .container { \n" +
            "            max-width: 600px; \n" +
            "            text-align: center; \n" +
            "            background: white;\n" +
            "            padding: 40px;\n" +
            "            border-radius: 10px;\n" +
            "            box-shadow: 0 4px 15px rgba(0,0,0,0.1);\n" +
            "        }\n" +
            "        h1 { \n" +
            "            color: #e63946; \n" +
            "            font-size: 48px; \n" +
            "            margin-bottom: 20px; \n" +
            "        }\n" +
            "        p { \n" +
            "            color: #666; \n" +
            "            font-size: 18px; \n" +
            "            margin-bottom: 30px; \n" +
            "        }\n" +
            "        .error { \n" +
            "            background: #ffe6e6; \n" +
            "            padding: 15px; \n" +
            "            border-radius: 5px; \n" +
            "            margin: 20px 0; \n" +
            "            text-align: left; \n" +
            "            font-family: monospace; \n" +
            "            color: #c33;\n" +
            "        }\n" +
            "        .btn { \n" +
            "            display: inline-block; \n" +
            "            padding: 12px 24px; \n" +
            "            background: #e63946; \n" +
            "            color: white; \n" +
            "            text-decoration: none; \n" +
            "            border-radius: 5px; \n" +
            "            font-weight: bold; \n" +
            "            border: none;\n" +
            "            cursor: pointer;\n" +
            "            font-size: 16px;\n" +
            "        }\n" +
            "        .btn:hover { \n" +
            "            background: #c1121f; \n" +
            "        }\n" +
            "        .icon {\n" +
            "            font-size: 64px;\n" +
            "            color: #e63946;\n" +
            "            margin-bottom: 20px;\n" +
            "        }\n" +
            "    </style>\n" +
            "    <link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css\">\n" +
            "</head>\n" +
            "<body>\n" +
            "    <div class=\"container\">\n" +
            "        <div class=\"icon\">\n" +
            "            <i class=\"fas fa-exclamation-triangle\"></i>\n" +
            "        </div>\n" +
            "        <h1>" + code + "</h1>\n" +
            "        <p>An error occurred while processing your request</p>\n" +
            "        <div class=\"error\">\n" +
            "            <i class=\"fas fa-bug\"></i> " + message + "\n" +
            "        </div>\n" +
            "        <button onclick=\"window.location.href='/'\"" +
            "                class=\"btn\">\n" +
            "            <i class=\"fas fa-home\"></i> Go to Homepage\n" +
            "        </button>\n" +
            "    </div>\n" +
            "</body>\n" +
            "</html>";
        
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(code, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}