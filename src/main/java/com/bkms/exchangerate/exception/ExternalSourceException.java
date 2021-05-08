package com.bkms.exchangerate.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExternalSourceException extends RuntimeException {

    private String statusCode;
    private String description;
}

