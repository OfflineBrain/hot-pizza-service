package com.offlinebrain.hotpizza.rest.advice;


import com.offlinebrain.hotpizza.exception.ResourceExistsException;
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
public class ResourceExistsHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceExistsHandler.class);


    @ExceptionHandler(ResourceExistsException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    @ResponseBody
    public ErrorResponse resourceExistsException(ResourceExistsException ex) {
        String stacktrace = null;
        if (LOGGER.isDebugEnabled()) {
            stacktrace = ExceptionUtils.getStackTrace(ex);
        }
        return new ErrorResponse(ex.getMessage(), stacktrace);
    }
}
