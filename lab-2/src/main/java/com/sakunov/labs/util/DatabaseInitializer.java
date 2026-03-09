package com.sakunov.labs.util;

import com.sakunov.labs.migration.DatabaseMigrator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Инициализатор базы данных
public class DatabaseInitializer {
    private static final Logger log = LoggerFactory.getLogger(DatabaseInitializer.class);

    private final DatabaseMigrator databaseMigrator;

    public DatabaseInitializer(DatabaseMigrator databaseMigrator) {
        this.databaseMigrator = databaseMigrator;
    }

    // Создание таблицы через Liquibase миграции
    public void createTableIfNotExists() {
        databaseMigrator.runMigrations();
        log.info("Таблица 'teacher' успешно создана или уже существует");
    }
}