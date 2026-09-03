package org.intern.personalfinancemanagementsystem.base;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

import java.time.Instant;
import java.util.Objects;

public record ApiResponse<T>(
        Boolean success,
        RestData<T> data,
        Instant timestamp
) {
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, RestData.success(message, data), Instant.now());
    }

//    public static <T> ResponseEntity<ApiResponse<T>> success(HttpStatus status, T data) {
//        ApiResponse<T> response = new ApiResponse<>(true, new RestData<>(data), Instant.now());
//        return new ResponseEntity<>(response, status);
//    }

    public static <T> ResponseEntity<ApiResponse<T>> success(HttpStatus status, String message, T data) {
        ApiResponse<T> response = new ApiResponse<>(true, RestData.success(message, data), Instant.now());
        return new ResponseEntity<>(response, status);
    }

//    public static <T> ResponseEntity<ApiResponse<T>> success(String message, MultiValueMap<String, String> header, T data) {
//        return success(HttpStatus.OK, message, header, data);
//    }
//
//    public static <T> ResponseEntity<ApiResponse<T>> success(HttpStatus status, String message, MultiValueMap<String, String> header, T data) {
//        ApiResponse<T> response = new ApiResponse<>(true, new RestData<>(message, data), Instant.now());
//        HttpHeaders responseHeaders = new HttpHeaders();
//        responseHeaders.addAll((HttpHeaders) header);
//        return ResponseEntity.ok().headers(responseHeaders).body(response);
//    }


    public static <T> ApiResponse<T> error(String code, String message) {
        return new ApiResponse<>(false, RestData.error(code, message), Instant.now());
    }

    public static <T> ApiResponse<T> error(String code, String message, Object details) {
        return new ApiResponse<>(false, RestData.error(code, message, details), Instant.now());
    }
}
