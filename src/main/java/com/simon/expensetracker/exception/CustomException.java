package com.simon.expensetracker.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CustomException extends Exception {
    private HttpStatus statusCode;

    public CustomException(String message, HttpStatus statusCode){
        super(message);
        this.statusCode = statusCode;
    }
}
