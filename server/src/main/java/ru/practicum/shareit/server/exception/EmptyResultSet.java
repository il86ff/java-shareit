package ru.practicum.shareit.server.exception;

public class EmptyResultSet extends RuntimeException {
    public EmptyResultSet(final String message) {
        super(message);
    }
}