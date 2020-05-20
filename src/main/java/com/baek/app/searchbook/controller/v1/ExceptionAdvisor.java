package com.baek.app.searchbook.controller.v1;

import com.google.gson.Gson;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

@Slf4j
@ControllerAdvice
public class ExceptionAdvisor {

    //#region Exception Handler
    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorResponse> handleIOException(IOException e, WebRequest webRequest) {
        return sendError(e, HttpStatus.BAD_REQUEST, webRequest);

    }

    @ExceptionHandler(IndexOutOfBoundsException.class)
    public ResponseEntity<ErrorResponse> handleIOException2(IndexOutOfBoundsException e, WebRequest webRequest) {
        return sendError(e, HttpStatus.BAD_REQUEST, webRequest);

    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleIOException2(MissingServletRequestParameterException e, WebRequest webRequest) {
        return sendError(e, HttpStatus.BAD_REQUEST, webRequest);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e, WebRequest webRequest) {
        return sendError(e, HttpStatus.INTERNAL_SERVER_ERROR, webRequest);
    }
    //#endregion

    private ResponseEntity<ErrorResponse> sendError(Exception e, HttpStatus httpStatus, WebRequest webRequest) {
        log.error(webRequest.toString() + ";" + new Gson().toJson(webRequest.getParameterMap()), e);
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));

        ErrorResponse em = new ErrorResponse();
        em.setStatusCode(httpStatus.value());
        em.setMessage(e.getMessage());
        em.setDetail(sw.toString());
        return new ResponseEntity<>(em, new HttpHeaders(), httpStatus);
    }

    @Data
    public static class ErrorResponse {
        private int statusCode;
        private String message;
        private String detail;
    }
}
