package com.Project.ecommerce.dto.response;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ErrorResponse {
    private List<?> data;
    private HttpStatus httpStatusCode;
    private LocalDateTime responseTime;
    private List<String> errorMessages;

    public ErrorResponse(HttpStatus httpStatusCode, LocalDateTime responseTime, List<String> errorMessages) {
        this.httpStatusCode = httpStatusCode;
        this.responseTime = responseTime;
        this.errorMessages = errorMessages;
    }

    public ErrorResponse(List<?> data, HttpStatus httpStatusCode, LocalDateTime responseTime, List<String> errorMessages) {
        this.data = data;
        this.httpStatusCode = httpStatusCode;
        this.responseTime = responseTime;
        this.errorMessages = errorMessages;
    }
}