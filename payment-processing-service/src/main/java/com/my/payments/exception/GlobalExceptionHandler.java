package com.my.payments.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.my.payments.constant.ErrorCodeEnum;
import com.my.payments.pojo.ErrorResponse;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ProcessingException.class)
    public ResponseEntity<ErrorResponse> handleProcessingException(
    		ProcessingException ex) {
    	log.error("Handling ProcessingException: {}", ex.getMessage(), ex);
    	
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getErrorCode(),
                ex.getMessage()
        );
        
        log.info("ErrorResponse: {}", errorResponse);
        return new ResponseEntity<>(errorResponse, ex.getHttpStatus());
    }
    
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
    		Exception ex) {
    	log.error("Handling handleException: {}", ex.getMessage(), ex);
    	
        ErrorResponse errorResponse = new ErrorResponse(
        		ErrorCodeEnum.GENERIC_ERROR.getErrorCode(),
        		ErrorCodeEnum.GENERIC_ERROR.getErrorMessage()
        );
        
        log.info("ErrorResponse: {}", errorResponse);
        return new ResponseEntity<>(
        		errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
}