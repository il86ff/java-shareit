package ru.practicum.shareit.server.exception;

public class ValidationItemException extends RuntimeException {
    public ValidationItemException(final String message) {
        super(message);
    }
}
