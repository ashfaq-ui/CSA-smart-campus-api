package com.smartcampus;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;

public class Main {

    public static void main(String[] args) throws IOException {
        ResourceConfig config = new ResourceConfig()
                .packages(
                        "com.smartcampus.resource",
                        "com.smartcampus.config",
                        "com.smartcampus.exception",
                        "com.smartcampus.filter"
                )
                .register(JacksonFeature.class);

        HttpServer server = GrizzlyHttpServerFactory
                .createHttpServer(URI.create("http://0.0.0.0:8080/"), config);

        System.out.println("Server started at http://localhost:8080/");
        System.in.read();
        server.shutdownNow();
    }
}