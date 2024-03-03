package ru.practicum.shareit.exception;

public class UnknownState extends RuntimeException {
    public UnknownState(final String message) {
        super(message);
    }
}

