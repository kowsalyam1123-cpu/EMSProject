package com.tekpyramid.boot_sample.exception;

public class DuplicateEmailException extends RuntimeException{


    public DuplicateEmailException(String message) {
        super(message);
    }


}
