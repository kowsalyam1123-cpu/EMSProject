package com.tekpyramid.boot_sample.exception;

import com.tekpyramid.boot_sample.dto.ResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseDto handularDuplicationException(DuplicateEmailException e) {
        log.error("Duplicate email error: {}", e.getMessage());
        return ResponseDto.builder().error(true).message(e.getMessage()).data(null).build();
    }

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseDto hundleDataNotFound(DataNotFoundException e) {
        log.error("Data not found: {}", e.getMessage());
        return ResponseDto.builder().error(true).message(e.getMessage()).data(null).build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseDto handleValidation(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        log.error("Validation error: {}", errors);
        return ResponseDto.builder().error(true).message("Validation failed").data(errors).build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseDto handleIllegalArgument(IllegalArgumentException e) {
        log.error("Illegal argument: {}", e.getMessage());
        return ResponseDto.builder().error(true).message(e.getMessage()).data(null).build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseDto handleGeneric(Exception e) {
        log.error("Unexpected error occurred", e);
        return ResponseDto.builder() .error(true)
                .message("Internal server error")
                .data(null)
                .build();
    }
}