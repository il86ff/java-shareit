package ru.practicum.shareit.server.exception;

public class EmptyEmailException extends RuntimeException {
    public EmptyEmailException(final String message) {
        super(message);
    }
}
