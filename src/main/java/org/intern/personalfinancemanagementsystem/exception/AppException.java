package org.intern.personalfinancemanagementsystem.exception;

import lombok.Getter;
import org.intern.personalfinancemanagementsystem.constant.ErrorMessage;
import org.springframework.http.HttpStatus;

@Getter
public class AppException extends RuntimeException {
    private HttpStatus status;
    private String errorCode;
    private String errorMessage;
    private String[] params;

    public AppException(String message) {
        super(message);
        this.errorMessage = message;
    }

    public AppException(String errorCode, String errorMessage) {
        super(String.valueOf(errorMessage));
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }

    public AppException(String[] params, String message) {
        super(message);
        this.errorCode = ErrorMessage.INTERNAL_SERVER_ERROR;
        this.params = params;
        this.errorMessage = message;
    }

    public AppException(String message, String[] params) {
        super(message);
        this.errorMessage = message;
        this.errorCode = ErrorMessage.INTERNAL_SERVER_ERROR;
        this.params = params;
    }

    public AppException(HttpStatus status, String message) {
        super(message);
        this.status = status;
        this.errorMessage = message;
        this.errorCode = ErrorMessage.INTERNAL_SERVER_ERROR;
    }

    public AppException(HttpStatus status, String message, String code) {
        super(message);
        this.status = status;
        this.errorMessage = message;
        this.errorCode = code;
    }
}
