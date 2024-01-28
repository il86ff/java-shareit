package ru.practicum.shareit.exception;

public class ItemOwnerMismatchException extends RuntimeException {
    public ItemOwnerMismatchException(final String message) {
        super(message);
    }
}
