package ru.practicum.shareit.server.exception;

public class ItemOwnerMismatchException extends RuntimeException {
    public ItemOwnerMismatchException(final String message) {
        super(message);
    }
}
