package com.keldorn.exception;

public class InvalidEmailException extends Exception {
    public InvalidEmailException() {
        super("Invalid email address.");
    }

    public InvalidEmailException(String message) {
        super(message);
    }

    public InvalidEmailException(String message, Throwable cause) {
        super(message, cause);
    }
}
