package com.Project.ecommerce.dto.response;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import java.time.LocalDateTime;

@Getter
@Setter
public class SuccessResponse {
    private Object data;
    private HttpStatus httpStatusCode;
    private LocalDateTime responseTime;
    private String successMessage;

    public SuccessResponse(HttpStatus httpStatusCode, LocalDateTime responseTime, String successMessage) {
        this.httpStatusCode = httpStatusCode;
        this.responseTime = responseTime;
        this.successMessage = successMessage;
    }

    public SuccessResponse(Object data, HttpStatus httpStatusCode, LocalDateTime responseTime, String successMessage) {
        this.data = data;
        this.httpStatusCode = httpStatusCode;
        this.responseTime = responseTime;
        this.successMessage = successMessage;
    }
}