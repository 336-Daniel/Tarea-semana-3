package com.uti.svcreservations.exception;

public class RoomsServiceException extends RuntimeException {
    public RoomsServiceException(String message) {
        super(message);
    }

    public RoomsServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
