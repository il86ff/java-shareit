package ru.practicum.shareit.exception;

public class EmptyResultSet extends RuntimeException {
    public EmptyResultSet(final String message) {
        super(message);
    }
}