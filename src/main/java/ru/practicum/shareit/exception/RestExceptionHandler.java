package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
@ControllerAdvice
public class RestExceptionHandler extends ExceptionHandlerExceptionResolver {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        log.error("Ошибки валидации тела запроса: {}", errors);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errors);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Set<String>> handleException(NotFoundException exception) {
        Set<String> messages = new HashSet<>();
        messages.add(exception.getMessage());
        log.error("Ошибка поиска: {}", messages);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(messages);
    }

    @ExceptionHandler(EmptyEmailException.class)
    public ResponseEntity<Set<String>> handleException(EmptyEmailException exception) {
        Set<String> messages = new HashSet<>();
        messages.add(exception.getMessage());
        log.error("Ошибка поиска: {}", messages);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(messages);
    }

    @ExceptionHandler({DuplicateEmailException.class})
    public ResponseEntity<Set<String>> handleException(DuplicateEmailException exception) {
        Set<String> messages = new HashSet<>();
        messages.add(exception.getMessage());
        log.error("Ошибка заполнения тела запроса: {}", messages);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(messages);
    }

    @ExceptionHandler(ValidationItemException.class)
    public ResponseEntity<Set<String>> handleException(ValidationItemException exception) {
        Set<String> messages = new HashSet<>();
        messages.add(exception.getMessage());
        log.error("Ошибка валидации предмета: {}", messages);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(messages);
    }

    @ExceptionHandler(ItemOwnerMismatchException.class)
    public ResponseEntity<Set<String>> handleException(ItemOwnerMismatchException exception) {
        Set<String> messages = new HashSet<>();
        messages.add(exception.getMessage());
        log.error("Ошибка обновление предмета: {}", messages);
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(messages);
    }
}
