package com.razard.search.app.api.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<ErrorMessage> error(final String message, final HttpStatus httpStatus,
                                               final HttpServletRequest request) {
        return new ResponseEntity<>(new ErrorMessage(message, request.getRequestURI(), httpStatus), httpStatus);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> handleException(final HttpServletRequest request, Exception e) {
        log.error("Exception :", e);
        return error(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorMessage> illegalArgumentException(final HttpServletRequest request, Exception e) {
        log.error("Exception :", e);
        return error(e.getMessage(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorMessage> missingServletRequestParameterException(final HttpServletRequest request, Exception e) {
        log.error("Exception :", e);
        return error(e.getMessage(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorMessage> noHandlerFoundException(final HttpServletRequest request, Exception e) {
        log.error("Exception :", e);
        return error(e.getMessage(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorMessage> constraintViolationException(final HttpServletRequest request, Exception e) {
        log.error("Exception :", e);
        return error(e.getMessage(), HttpStatus.BAD_REQUEST, request);
    }
}
