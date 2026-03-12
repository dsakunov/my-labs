package com.sakunov.labs.migration;

import com.sakunov.labs.config.DatabaseConfig;
import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.Connection;

// Класс для управления миграциями бд
public class DatabaseMigrator {
    // Создаем логгер для этого класса
    private static final Logger log = LoggerFactory.getLogger(DatabaseMigrator.class);

    private final DatabaseConfig databaseConfig;

    public DatabaseMigrator(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
    }

    // Запуск миграций
    public void runMigrations() {
        log.info("Запуск Liquibase миграций...");

        try (Connection connection = databaseConfig.getConnection()) {
            Liquibase liquibase = new Liquibase(
                    "db.changelog/db.changelog-master.xml", // Путь к мастер-файлу
                    new ClassLoaderResourceAccessor(),                  // Доступ к ресурсам
                    new JdbcConnection(connection)                      // Соединение с бд
            );

            liquibase.update(); // Применяем все новые миграции
            log.info("Liquibase миграции успешно применены.");

        } catch (Exception e) {
            log.error("Ошибка применения миграций БД!", e);
            throw new RuntimeException("Ошибка применения миграций БД!", e);
        }
    }
}