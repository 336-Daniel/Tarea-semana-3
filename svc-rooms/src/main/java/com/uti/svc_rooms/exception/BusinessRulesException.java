package com.uti.svc_rooms.exception;


public class BusinessRulesException extends RuntimeException {

    public BusinessRulesException(String message) {
        super(message);
    }

    public BusinessRulesException(String message, Throwable cause) {
        super(message, cause);
    }
}
