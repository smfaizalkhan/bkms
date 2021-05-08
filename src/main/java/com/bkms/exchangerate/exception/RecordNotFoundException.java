package com.bkms.exchangerate.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecordNotFoundException extends RuntimeException {

    private String description;
}
