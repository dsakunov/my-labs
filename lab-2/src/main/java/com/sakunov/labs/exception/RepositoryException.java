package com.sakunov.labs.exception;

// Кастомные исключения
public class RepositoryException extends RuntimeException {
    // Конструктор с двумя параметрами. Передаём сообщение и причину в родительский класс
    // message - сообщение об ошибке, cause - причина ошибки
    // super() - вызов конструктора родительского класса
    public RepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}