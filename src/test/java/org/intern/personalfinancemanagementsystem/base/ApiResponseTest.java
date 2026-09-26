package org.intern.personalfinancemanagementsystem.base;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public class ApiResponseTest {

    @Test
    void success_whenMessageAndDataProvided_shouldReturnSuccessfulResponse() {
        String message = "Request successfully";
        String data = "test data";

        ApiResponse<String> response = ApiResponse.success(message, data);

        assertTrue(response.success());
        assertNotNull(response.data());
        assertNotNull(response.timestamp());

        assertTrue(response.timestamp().isBefore(Instant.now()) || response.timestamp().equals(Instant.now()));
    }

    @Test
    void success_whenHttpStatusProvided_shouldReturnResponseEntityWithCorrectStatus() {
        HttpStatus status = HttpStatus.CREATED;
        String message = "Created successfully";
        String data = "test data";

        ResponseEntity<ApiResponse<String>> response = ApiResponse.success(status, message, data);

        assertEquals(status, response.getStatusCode());

        assertNotNull(response.getBody());
        assertTrue(response.getBody().success());
        assertNotNull(response.getBody().data());
        assertNotNull(response.getBody().timestamp());
    }

    @Test
    void error_whenCodeAndMessageProvided_shouldReturnErrorResponse() {
        String code = "BAD_REQUEST";
        String message = "Invalid request";

        ApiResponse<String> response = ApiResponse.error(code, message);

        assertFalse(response.success());
        assertNotNull(response.data());
        assertNotNull(response.timestamp());
    }

    @Test
    void error_whenCodeMessageAndDetailsProvided_shouldReturnErrorResponseWithDetails() {
        String code = "VALIDATION_ERROR";
        String message = "Validation failed";
        Object details = "Invalid email";

        ApiResponse<String> response = ApiResponse.error(code, message, details);

        assertFalse(response.success());
        assertNotNull(response.data());
        assertNotNull(response.timestamp());
    }
}