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

import java.text.MessageFormat;
import java.util.UUID;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.Flow.Processor;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An example class demonstrating the usage of Reactive Streams API in Java 9.
 * This class provides a simple implementation of a Publisher that emits a
 * sequence of values,and a Subscriber that consumes and prints the emitted
 * values.
 * The Reactive Streams API provides a standardized way for asynchronous stream
 * processing with backpressure support, allowing for efficient and scalable
 * processing of large data sets.
 * 
 * @author Jorge Garcia
 * @since 17
 */
public final class Java9ReactiveStreamsExample {

    private static final Logger LOGGER = Logger.getLogger(Java9ReactiveStreamsExample.class.getName());

    private static class PrintSubscriber implements Subscriber<String> {

        private static final int NUMBER_OF_ATTEMPS = 1;

        private final String id;

        private Subscription subscription;

        PrintSubscriber(String id) {
            this.id = id;
        }

        @Override
        public void onSubscribe(Subscription subscription) {
            this.subscription = subscription;
            this.subscription.request(NUMBER_OF_ATTEMPS);
            LOGGER.log(Level.INFO, "{0} > Print Suscribed to: {1}",
                    new Object[] { id, subscription });
        }

        @Override
        public void onNext(String item) {
            LOGGER.log(Level.INFO, "{0} > Print Received Item: {1}",
                    new Object[] { id, item });
            this.subscription.request(NUMBER_OF_ATTEMPS);
        }

        @Override
        public void onError(Throwable throwable) {
            LOGGER.log(Level.SEVERE, throwable,
                    () -> MessageFormat.format("{0} > Print Error!: {1}",
                            id, throwable.getMessage()));
        }

        @Override
        public void onComplete() {
            LOGGER.log(Level.INFO, "{0} > Print Completed!", id);
        }
    }

    private static class MessageLengthPublisherProcessor
            extends SubmissionPublisher<Integer>
            implements Processor<String, Integer> {

        private static final int NUMBER_OF_ATTEMPS = 1;

        private final String id;

        private Subscription subscription;

        MessageLengthPublisherProcessor(String id) {
            this.id = id;
        }

        @Override
        public void onSubscribe(Subscription subscription) {
            this.subscription = subscription;
            this.subscription.request(NUMBER_OF_ATTEMPS);
            LOGGER.log(Level.INFO, "{0} > Msg. Lenght Suscribed to: {1}",
                    new Object[] { id, subscription });
        }

        @Override
        public void onNext(String item) {
            LOGGER.log(Level.INFO, "{0} > Msg. Lenght Received Item: {1}",
                    new Object[] { id, item });

            int newItem = item.length();
            submit(newItem);
            this.subscription.request(NUMBER_OF_ATTEMPS);

            LOGGER.log(Level.INFO, "{0} > Msg. Lenght Submited New Item: {1}",
                    new Object[] { id, newItem });
        }

        @Override
        public void onError(Throwable throwable) {
            LOGGER.log(Level.SEVERE, throwable,
                    () -> MessageFormat.format("{0} > Msg. Lenght Error!: {1}",
                            id, throwable.getMessage()));
        }

        @Override
        public void onComplete() {
            LOGGER.log(Level.INFO, "{0} > Msg. Lenght Completed!", id);
        }
    }

    /**
     * This is the entry point of the application.
     * This method is called by the JVM to start the application.
     *
     * @param args The command line arguments. Additional arguments can be passed to
     *             the program.
     */
    public static void main(String[] args) {

        try (final var publisher = new SubmissionPublisher<String>();
                final var messageProcessor = new MessageLengthPublisherProcessor(newUUID())) {
            publisher.subscribe(new PrintSubscriber(newUUID()));
            publisher.subscribe(new PrintSubscriber(newUUID()));
            publisher.subscribe(messageProcessor);

            // TODO: Register subscribers to the messageProcessor.

            final var numberOfMessages = 10;
            for (var i = 0; i < numberOfMessages; i++) {
                publisher.submit(String.format("Message Number %d", i));
            }

            final var millis = 2000;
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
    }

    private static String newUUID() {
        return UUID.randomUUID().toString();
    }
}