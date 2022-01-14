package com.fitness.purchaseservice.exception;

import com.fitness.sharedapp.common.Status;
import com.fitness.sharedapp.exception.GlobalExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class GlobalExHandler extends GlobalExceptionHandler {
    public GlobalExHandler(Status status) {
        super(status);
    }
}
