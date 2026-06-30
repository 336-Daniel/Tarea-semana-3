package com.uti.svc_rooms.exception;

/**
 * Excepción lanzada cuando un recurso (habitación) no es encontrado
 * Retorna código HTTP 404 - Not Found
 */
public class ResourceNotfoundException extends RuntimeException {

    public ResourceNotfoundException(String message) {
        super(message);
    }

    public ResourceNotfoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
