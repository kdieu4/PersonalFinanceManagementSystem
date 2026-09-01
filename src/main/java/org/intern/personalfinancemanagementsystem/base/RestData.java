package org.intern.personalfinancemanagementsystem.base;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RestData<T> {
    private HttpStatus status;

    private String message;

    private T data;

    public RestData(T data) {
        this.status = HttpStatus.OK;
        this.data = data;
    }

    public RestData(String message, T data) {
        this.status = HttpStatus.OK;
        this.message = message;
        this.data = data;
    }

    public static <T> RestData<T> error(HttpStatus code, String message) {
        return new RestData<>(code, message, null);
    }

    public static <T> RestData<T> error(HttpStatus code, String message, T data) {
        return new RestData<>(code, message, data);
    }
}
