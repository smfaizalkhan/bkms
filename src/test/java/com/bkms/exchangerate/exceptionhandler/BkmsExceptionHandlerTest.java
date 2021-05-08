package com.bkms.exchangerate.exceptionhandler;

import com.bkms.exchangerate.error.ApiError;
import com.bkms.exchangerate.exception.ExternalSourceException;
import com.bkms.exchangerate.exception.RecordNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.InputMismatchException;

import static org.junit.jupiter.api.Assertions.*;

class BkmsExceptionHandlerTest {

    @InjectMocks
    private BkmsExceptionHandler bkmsExceptionHandler;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void handleExternalSourceException() {
        ExternalSourceException externalSourceException = new ExternalSourceException("202","You have provided one or more invalid Currency Codes. [Required format: currencies=EUR,USD,GBP,...]");
        ResponseEntity<Object> responseEntity = bkmsExceptionHandler.handleExternalSourceException(externalSourceException);
        assertTrue(responseEntity.getBody() instanceof ApiError);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        ApiError apiError = (ApiError) responseEntity.getBody();
        assertEquals(externalSourceException.getDescription(),apiError.getDescription());
    }

    @Test
    void handleRecordNotFoundException() {
        RecordNotFoundException recordNotFoundException = new RecordNotFoundException();
        ResponseEntity<Object> responseEntity = bkmsExceptionHandler.handleRecordNotFoundException(recordNotFoundException);
        assertTrue(responseEntity.getBody() instanceof ApiError);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.NO_CONTENT);
        ApiError apiError = (ApiError) responseEntity.getBody();
        assertEquals(recordNotFoundException.getDescription(),apiError.getDescription());


    }

    @Test
    void handleInputMismatchException() {
        InputMismatchException inputMismatchException = new InputMismatchException(String.format("Date %s is not between 2000-01-01 and yesterday","1999-05-05"));
        ResponseEntity<Object> responseEntity = bkmsExceptionHandler.handleInputMismatchException(inputMismatchException);
        assertTrue(responseEntity.getBody() instanceof ApiError);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);
        ApiError apiError = (ApiError) responseEntity.getBody();
        assertEquals(inputMismatchException.getMessage(),apiError.getDescription());
    }
}