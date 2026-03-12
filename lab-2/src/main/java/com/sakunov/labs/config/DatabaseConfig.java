package com.sakunov.labs.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.Connection;
import java.sql.SQLException;

// Конфигурация подключения к бд
public class DatabaseConfig {
    // Создаем логгер для этого класса
    private static final Logger log = LoggerFactory.getLogger(DatabaseConfig.class);

    // Пул соединений HikariCP
    private final HikariDataSource dataSource;

    public DatabaseConfig() {
        // Читаем параметры из переменных окружения
        String jdbcUrl = System.getenv("DB_URL");
        String username = System.getenv("DB_USERNAME");
        String password = System.getenv("DB_PASSWORD");

        if (jdbcUrl == null || username == null || password == null) {
            log.error("Не заданы переменные окружения для подключения к бд!");
            throw new RuntimeException("Не заданы переменные окружения для подключения к бд!");
        }

        try {
            // Загружаем драйвер
            Class.forName("org.postgresql.Driver");
            log.info("PostgreSQL JDBC Driver зарегистрирован.");

            // Настройка пула соединений
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(jdbcUrl);
            config.setUsername(username);
            config.setPassword(password);

            config.setMaximumPoolSize(10);      // Макс. число соединений
            config.setMinimumIdle(5);           // Мин. кол-во соединений, которые пул всегда держит открытыми
            config.setIdleTimeout(300000);      // Время простоя соединения (5 минут)
            config.setConnectionTimeout(30000); // Сколько ждать соединения из пула, прежде чем упасть с ошибкой (30 секунд)
            config.setMaxLifetime(1800000);     // Макс. время жизни соединения (30 минут)

            // Создание пула
            dataSource = new HikariDataSource(config);
            log.info("HikariCP connection pool инициализирован.");

        } catch (ClassNotFoundException e) {
            log.error("PostgreSQL JDBC Driver не найден!", e);
            throw new RuntimeException("PostgreSQL JDBC Driver не найден!", e);
        }
    }

    // Получение соединения из пула
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    // Закрытие пула соединений
    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            log.info("HikariCP connection pool закрыт.");
        }
    }
}