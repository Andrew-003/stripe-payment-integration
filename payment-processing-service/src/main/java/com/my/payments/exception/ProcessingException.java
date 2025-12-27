package com.my.payments.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ProcessingException extends RuntimeException {
    
	private static final long serialVersionUID = -1834342033523671581L;

	private final String errorCode;
    private final String errorMessage;
    private final HttpStatus httpStatus;

    public ProcessingException(
    		String errorCode, 
    		String errorMessage, 
    		HttpStatus httpStatus) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.httpStatus = httpStatus;
    }
}