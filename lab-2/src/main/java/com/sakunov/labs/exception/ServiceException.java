package com.sakunov.labs.exception;

// Собственное исключение сервисного слоя
public class ServiceException extends RuntimeException {
    public ServiceException(String message) {
        super(message);
    }
}