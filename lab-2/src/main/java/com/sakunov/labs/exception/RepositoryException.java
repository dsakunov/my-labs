package com.sakunov.labs.exception;

// Исключения для ошибок репозитория
public class RepositoryException extends RuntimeException {
    // Конструктор с сообщением и причиной
    public RepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}