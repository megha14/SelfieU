package com.dailyselfie.selfie;

import com.dailyselfie.repository.SelfieRecord;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.UUID;

/**
 * This is a utility class to aid in the construction of
 * Selfie objects with random names, urls, and durations.
 * The class also provides a facility to convert objects
 * into JSON using Jackson, which is the format that the
 * SelfieSvc controller is going to expect data in for
 * integration testing.
 *
 * @author jules
 */
public class TestData {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Construct and return a Selfie object with a
     * rnadom name, url, and duration.
     *
     * @return
     */
    public static SelfieRecord randomSelfie() {
        // Information about the selfie
        // Construct a random identifier using Java's UUID class
        String id = UUID.randomUUID().toString();
        String title = id;

        String contentType = "image/jpeg";

        return new SelfieRecord(title, contentType);
    }

    /**
     * Convert an object to JSON using Jackson's ObjectMapper
     *
     * @param o
     * @return
     * @throws Exception
     */
    public static String toJson(Object o) throws Exception {
        return objectMapper.writeValueAsString(o);
    }
}
