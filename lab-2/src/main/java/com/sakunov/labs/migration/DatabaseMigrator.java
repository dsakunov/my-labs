package com.sakunov.labs.migration;

import com.sakunov.labs.config.DatabaseConfig;
import liquibase.Liquibase;                             // Главный класс для работы с миграциями
import liquibase.database.jvm.JdbcConnection;           // Адаптер для подключения к БД через JDBC
import liquibase.resource.ClassLoaderResourceAccessor;  // Доступ к файлам ресурсов через ClassLoader

// Импорт SLF4J (Simple Logging Facade for Java) - библиотеки для логирования
import org.slf4j.Logger;        // Интерфейс логгера
import org.slf4j.LoggerFactory; // Фабрика для создания логгеров

import java.sql.Connection;

// Класс для управления миграциями бд
public class DatabaseMigrator {
    // Создаем логгер для этого класса
    private static final Logger log = LoggerFactory.getLogger(DatabaseMigrator.class);

    // Метод для запуска миграций
    // Читает файлы миграций из resources/db/changelog/
    public static void runMigrations() {
        System.out.println("\nЗапуск Liquibase миграций...");

        // Connection - это ресурс, который нужно закрывать, чтобы вернуть соединение в пул
        try (Connection connection = DatabaseConfig.getConnection()) {
            // Создаем объект Liquibase с параметрами
            Liquibase liquibase = new Liquibase(
                    "db.changelog/db.changelog-master.xml",  // Путь к мастер-файлу миграций
                    new ClassLoaderResourceAccessor(),   // Доступ к файлам через ClassLoader
                    new JdbcConnection(connection)  // Чтобы Liquibase мог выполнять SQL через наше соединение
            );

            // Применяем все миграции
            liquibase.update(); // выполняет все ещё не применённые миграции
            System.out.println("Liquibase миграции успешно применены.");

        } catch (Exception e) {
            log.error("Ошибка применения миграций БД: ", e);
            throw new RuntimeException("Ошибка при выполнении миграций", e);
        }
    }
}