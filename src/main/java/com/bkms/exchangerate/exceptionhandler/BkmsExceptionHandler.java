package com.bkms.exchangerate.exceptionhandler;

import com.bkms.exchangerate.error.ApiError;
import com.bkms.exchangerate.exception.ExternalSourceException;
import com.bkms.exchangerate.exception.RecordNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.InputMismatchException;

@RestControllerAdvice
public class BkmsExceptionHandler {

    @ExceptionHandler(value = ExternalSourceException.class)
    public ResponseEntity<Object> handleExternalSourceException(ExternalSourceException ex){
        final ApiError apiError = new ApiError(ex.getStatusCode(),"External SourceException",ex.getDescription());
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(value = InputMismatchException.class)
    public ResponseEntity<Object> handleInputMismatchException(InputMismatchException ex){
        final ApiError apiError = new ApiError(String.valueOf(HttpStatus.BAD_REQUEST.value()),ex.getClass().getSimpleName(),ex.getMessage());
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(value = RecordNotFoundException.class)
    public ResponseEntity<Object> handleRecordNotFoundException(RecordNotFoundException ex){
        final ApiError apiError = new ApiError(HttpStatus.NO_CONTENT.toString(),"RecordNotFoundException",ex.getDescription());
        return new ResponseEntity<>(apiError,HttpStatus.NO_CONTENT);
    }
}
