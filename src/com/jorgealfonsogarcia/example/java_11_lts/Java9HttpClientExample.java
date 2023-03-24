/*
 *   Copyright (c) 2023 Jorge Garcia
 *   All rights reserved.

 *   Permission is hereby granted, free of charge, to any person obtaining a copy
 *   of this software and associated documentation files (the "Software"), to deal
 *   in the Software without restriction, including without limitation the rights
 *   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   copies of the Software, and to permit persons to whom the Software is
 *   furnished to do so, subject to the following conditions:
 
 *   The above copyright notice and this permission notice shall be included in all
 *   copies or substantial portions of the Software.
 
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *   SOFTWARE.
 */

package com.jorgealfonsogarcia.example.java_11_lts;

import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * A simple example class demonstrating how to use {@link HttpClient} in Java to
 * make HTTP requests.
 * 
 * @author Jorge Garcia
 * @since 17
 */
public class Java9HttpClientExample {

    private static final Logger LOGGER = Logger.getLogger(Java9HttpClientExample.class.getName());

    private static final String MY_REQUEST_TRACE_ID_HEADER = "my-request-trace-id";

    private static final String CONTENT_TYPE_HEADER = "Content-Type";

    private static final String APPLICATION_JSON = "application/json";

    private static final int TIMEOUT_SECONDS = 2;

    /**
     * This is the entry point of the application.
     * This method is called by the JVM to start the application.
     *
     * @param args The command line arguments. Additional arguments can be passed to
     *             the program.
     */
    public static void main(String[] args) {
        try {
            executeGetExample();

            executePostExample();

            executeAsyncPutExample();

            executeBasicAuthExample();
        } catch (URISyntaxException | IOException | ExecutionException
                | TimeoutException e) {
            LOGGER.log(Level.WARNING, "Exception", e);
        } catch (InterruptedException e) {
            LOGGER.log(Level.WARNING, "Interrupted!", e);
            Thread.currentThread().interrupt();
        }
    }

    private static void executeGetExample()
            throws URISyntaxException, IOException, InterruptedException {
        final var httpRequest = HttpRequest.newBuilder()
                .uri(new URI("https://postman-echo.com/status/400"))
                .GET()
                .header(MY_REQUEST_TRACE_ID_HEADER, UUID.randomUUID().toString())
                .timeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                .build();

        final var httpClient = HttpClient.newBuilder()
                .build();

        final var httpResponse = httpClient.send(httpRequest, BodyHandlers.ofString());

        printHeaders(httpResponse.headers());
        printBody(httpResponse.body());
    }

    private static void executePostExample()
            throws URISyntaxException, IOException, InterruptedException {
        final var httpRequest = HttpRequest.newBuilder()
                .uri(new URI("https://postman-echo.com/post"))
                .POST(BodyPublishers.ofString("""
                        {
                            "employee": {
                                "name": "John Doe",
                                "salary": 56000,
                                "married": true
                            }
                        }"""))
                .header(MY_REQUEST_TRACE_ID_HEADER, UUID.randomUUID().toString())
                .header(CONTENT_TYPE_HEADER, APPLICATION_JSON)
                .timeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                .build();

        final var httpClient = HttpClient.newBuilder()
                .build();

        final var httpResponse = httpClient.send(httpRequest, BodyHandlers.ofString());

        printHeaders(httpResponse.headers());
        printBody(httpResponse.body());
    }

    private static void executeBasicAuthExample()
            throws URISyntaxException, IOException, InterruptedException {
        final var httpRequest = HttpRequest.newBuilder()
                .uri(new URI("https://postman-echo.com/basic-auth"))
                .GET()
                .header(MY_REQUEST_TRACE_ID_HEADER, UUID.randomUUID().toString())
                .timeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                .build();

        final var authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("postman",
                        "password".toCharArray());
            }
        };

        final var httpClient = HttpClient.newBuilder()
                .authenticator(authenticator)
                .build();

        final var httpResponse = httpClient.send(httpRequest, BodyHandlers.ofString());

        printHeaders(httpResponse.headers());
        printBody(httpResponse.body());
    }

    private static void executeAsyncPutExample()
            throws URISyntaxException, InterruptedException, ExecutionException,
            TimeoutException {
        final var httpRequest = HttpRequest.newBuilder()
                .uri(new URI("https://postman-echo.com/put"))
                .PUT(BodyPublishers.ofString("""
                        {
                            "employee": {
                                "name": "John Doe",
                                "salary": 26000,
                                "married": false
                            }
                        }"""))
                .header(MY_REQUEST_TRACE_ID_HEADER, UUID.randomUUID().toString())
                .header(CONTENT_TYPE_HEADER, APPLICATION_JSON)
                .timeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                .build();

        final var httpClient = HttpClient.newBuilder()
                .build();

        final var future = httpClient.sendAsync(httpRequest, BodyHandlers.ofString());

        future.thenAccept(httpResponse -> {
            printHeaders(httpResponse.headers());
            printBody(httpResponse.body());
        });

        future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    private static void printHeaders(final HttpHeaders httpHeaders) {
        httpHeaders.map().entrySet().stream()
                .map(entry -> String.format("HEADER\t%s:\t%s",
                        entry.getKey(), entry.getValue().stream()
                                .collect(Collectors.joining(","))))
                .forEach(System.out::println);
    }

    private static void printBody(final String body) {
        System.out.println("RESPONSE");
        System.out.println(body);
    }
}
