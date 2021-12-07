package com.offlinebrain.hotpizza.rest.advice;


import com.offlinebrain.hotpizza.exception.ResourceNotFoundException;
import com.offlinebrain.hotpizza.rest.model.error.ErrorResponse;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ResourceNotFoundHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceNotFoundHandler.class);


    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse resourceNotFoundException(ResourceNotFoundException ex) {
        String stacktrace = null;
        if (LOGGER.isDebugEnabled()) {
            stacktrace = ExceptionUtils.getStackTrace(ex);
        }
        return new ErrorResponse(ex.getMessage(), stacktrace);
    }
}
