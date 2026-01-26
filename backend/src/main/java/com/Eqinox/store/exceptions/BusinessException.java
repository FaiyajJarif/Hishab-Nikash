package com.Eqinox.store.exceptions;

/**
 * Thrown when a business rule is violated
 * (e.g. month already closed, budget locked, etc.)
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
