package ru.practicum.shareit.server.exception;

public class UnknownState extends RuntimeException {
    public UnknownState(final String message) {
        super(message);
    }
}

