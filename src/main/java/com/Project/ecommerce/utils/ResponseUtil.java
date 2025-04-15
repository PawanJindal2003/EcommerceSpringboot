package com.Project.ecommerce.utils;

import com.Project.ecommerce.dto.response.ErrorResponse;
import com.Project.ecommerce.dto.response.SuccessResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ResponseUtil {
    // success with only message
    public SuccessResponse success(HttpStatus httpStatusCode, String message) {
        return new SuccessResponse(httpStatusCode, LocalDateTime.now(), message);
    }

    // success with data
    public SuccessResponse successWithData(HttpStatus httpStatusCode, List<?> data) {
        return new SuccessResponse(data, httpStatusCode, LocalDateTime.now(), null);
    }

    // success with data and success message
    public SuccessResponse successWithDataAndMessage(List<?> data, HttpStatus httpStatusCode, String successMessage) {
        return new SuccessResponse(data, httpStatusCode, LocalDateTime.now(), successMessage);
    }

    // error only with message
    public ErrorResponse fail(HttpStatus httpStatusCode, List<String> messages) {
        return new ErrorResponse(httpStatusCode, LocalDateTime.now(), messages);
    }
}
