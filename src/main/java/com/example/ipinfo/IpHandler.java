package com.example.ipinfo;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Handles GET /api/ip and GET /api/ip/{address} requests and answers in JSON.
 */
final class IpHandler implements HttpHandler {

    private final IpService service;

    IpHandler(IpService service) {
        this.service = service;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                send(exchange, 405, Map.of("status", "error", "message", "Method not allowed"));
                return;
            }

            String path = exchange.getRequestURI().getPath(); // e.g. /api/ip or /api/ip/8.8.8.8
            String prefix = "/api/ip";
            String remainder = path.startsWith(prefix) ? path.substring(prefix.length()) : "";

            if (remainder.startsWith("/")) {
                remainder = remainder.substring(1);
            }
            if (remainder.contains("/")) {
                send(exchange, 400, Map.of("status", "error", "message", "Invalid path"));
                return;
            }

            String ip = remainder.isEmpty() ? null : URLDecoder.decode(remainder, StandardCharsets.UTF_8);
            if (ip != null && !ip.matches("[0-9a-fA-F:.%]+")) {
                send(exchange, 400, Map.of("status", "error", "message", "Invalid IP format"));
                return;
            }

            IpInfo info = service.lookup(ip);
            int status = "success".equals(info.status()) ? 200 : 404;
            send(exchange, status, info);
        } catch (Exception e) {
            send(exchange, 502, Map.of("status", "error", "message", "Upstream lookup failed: " + e.getMessage()));
        }
    }

    private void send(HttpExchange exchange, int code, Object body) throws IOException {
        byte[] bytes = Json.stringify(body).getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
