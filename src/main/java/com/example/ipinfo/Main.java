package com.example.ipinfo;

import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;

/**
 * Application entry point. Starts a minimal HTTP server.
 * No persistence, no framework dependencies.
 */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) throws Exception {
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/api/ip", new IpHandler(new IpService()));
        server.setExecutor(null); // default single-threaded executor (lean)
        server.start();
        System.out.println("IP Info API listening on http://localhost:" + port);
    }
}
