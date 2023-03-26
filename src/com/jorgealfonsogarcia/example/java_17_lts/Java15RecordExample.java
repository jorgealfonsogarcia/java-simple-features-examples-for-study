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

package com.jorgealfonsogarcia.example.java_17_lts;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An example class demonstrating the use of Records in Java 15.
 * The records showcase the simplicity and conciseness of using Records in Java
 * 15 to create classes with a small number of attributes and no additional
 * methods.
 * 
 * @author Jorge Garcia
 * @since 17
 */
public class Java15RecordExample {

    private static final Logger LOGGER = Logger.getLogger(Java15RecordExample.class.getName());

    record Employee(String fullName, int salary, LocalDate dateOfBirth) {

        int getAge() {
            return Period.between(dateOfBirth, LocalDate.now()).getYears();
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
        final var employees = List.of(
                new Employee("John Doe", 10000,
                        LocalDate.parse("1977-02-15")),
                new Employee("Jane Doe", 10000,
                        LocalDate.parse("1992-08-03")),
                new Employee("Bob Smith", 15000,
                        LocalDate.parse("1969-11-28")),
                new Employee("Mary Smith", 15000,
                        LocalDate.parse("1987-06-21")));

        employees.stream()
                .mapToInt(Employee::salary)
                .average()
                .ifPresentOrElse(
                        salaryAvg -> LOGGER.log(Level.INFO, "The salary average is: {0}", salaryAvg),
                        () -> LOGGER.info("There is no data to get the salary average."));

        employees.stream()
                .mapToInt(Employee::getAge)
                .average()
                .ifPresentOrElse(
                        ageAvg -> LOGGER.log(Level.INFO, "The age average is: {0}", (int) ageAvg),
                        () -> LOGGER.info("There is no data to get the age average."));
    }
}