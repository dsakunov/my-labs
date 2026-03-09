package com.sakunov.labs.config;

import com.zaxxer.hikari.HikariConfig;      // Класс для настройки пула соединений
import com.zaxxer.hikari.HikariDataSource;  // Сам пул соединений

import java.sql.Connection;     // Интерфейс для соединения с бд
import java.sql.SQLException;   // Исключения для ошибок SQL

public class DatabaseConfig {
    // Строка подключения к бд
    // jdbc:postgresql://[хост]:[порт]/[имя_базы]
    // Порт 5432 был занят, поэтому использовал 5433
    private static final String JDBC_URL = "jdbc:postgresql://localhost:5433/teacherdb";

    // Имя пользователя
    private static final String USERNAME = "teacher";

    // Пароль пользователя
    private static final String PASSWORD = "teacher123";

    // Реализация пула соединений
    // DataSource - это стандартный интерфейс для получения соединений с БД
    // HikariCP предоставляет его реализацию
    private static final HikariDataSource dataSource;

    static {
        try {
            // Загружаем JDBC драйвер
            // JDBC - API для работы с БД
            // Драйвер - реализация интерфейсов для бд
            Class.forName("org.postgresql.Driver"); // Динамическая загрузка класса драйвера
            System.out.println("\nPostgreSQL JDBC Driver зарегистрирован.");

            // Настройка пула
            // HikariConfig - класс для настройки параметров пула
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(JDBC_URL);    // Куда подключиться
            config.setUsername(USERNAME);   // Кто подключается
            config.setPassword(PASSWORD);   // Пароль

            // Настройки пула соединений
            config.setMaximumPoolSize(10);        // Максимальное число соединений
            config.setMinimumIdle(5);             // Минимальное количество соединений, которые пул всегда держит открытыми
            config.setIdleTimeout(300000);        // Время простоя соединения (5 минут)
            config.setConnectionTimeout(30000);   // Сколько ждать соединения из пула, прежде чем упасть с ошибкой (30 секунд)
            config.setMaxLifetime(1800000);       // Максимальное время жизни соединения (30 минут)

            // Создание пула
            // Создаем пул соединений с настройками
            // В этот момент HikariCP пытается создать minimumIdle соединений (если бд недоступна -> ошибка)
            dataSource = new HikariDataSource(config);
            System.out.println("HikariCP connection pool инициализирован.");
        } catch (ClassNotFoundException e) { // Если драйвер PostgreSQL не найден
            System.err.println("PostgreSQL JDBC Driver не найден!");
            throw new RuntimeException("Driver not found", e);
        }
    }

    // Приватный конструктор
    private DatabaseConfig() {
    }

    // Получение соединения из пула
    // @return Connection - соединение с бд из пула
    // @throws SQLException - если соединение не может быть установлено
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    // Закрытие пула соединений
    // dataSource != null - защита от случая, если пул не удалось инициализировать
    // !dataSource.isClosed() - проверяем, не закрыт ли уже пул, чтобы не закрывать дважды
    public static void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            System.out.println("HikariCP connection pool закрыт.");
        }
    }
}