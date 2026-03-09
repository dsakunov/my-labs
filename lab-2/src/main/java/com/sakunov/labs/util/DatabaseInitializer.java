package com.sakunov.labs.util;

import com.sakunov.labs.migration.DatabaseMigrator;

// Инициализатор базы данных
public class DatabaseInitializer {
    // Приватный конструктор
    private DatabaseInitializer() {
    }

    // Создание таблицы через Liquibase миграции
    public static void createTableIfNotExists() {
        // Запускаем миграции Liquibase
        DatabaseMigrator.runMigrations();
        System.out.println("Таблица 'teacher' успешно создана или уже существует.");
    }
}