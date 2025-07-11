package org.pet.project.exception;

public class UniqueConstraintViolationException extends RuntimeException {
    public UniqueConstraintViolationException(String message, Throwable cause) {
        super(message, cause);
    }
}
