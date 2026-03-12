package com.sakunov.labs.repository.impl;

import com.sakunov.labs.config.DatabaseConfig;
import com.sakunov.labs.entity.TeacherEntity;
import com.sakunov.labs.exception.RepositoryException;
import com.sakunov.labs.repository.TeacherDao;
import com.sakunov.labs.repository.TeacherMapper;
import com.sakunov.labs.repository.TeacherRepository;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

// Реализация репозитория через JDBI
public class TeacherRepositoryJdbi implements TeacherRepository {
    private static final Logger log = LoggerFactory.getLogger(TeacherRepositoryJdbi.class);

    private final Jdbi jdbi;        // Основной объект JDBI
    private final TeacherDao dao;   // Реализация интерфейса с аннотациями

    // Конструктор репозитория
    public TeacherRepositoryJdbi(DatabaseConfig databaseConfig) {
        try {
            // Создаем экземпляр JDBI с соединением из пула HikariCP
            this.jdbi = Jdbi.create(databaseConfig.getConnection());

            // Подключаем плагин для работы с аннотациями (@SqlUpdate, @SqlQuery)
            this.jdbi.installPlugin(new SqlObjectPlugin());

            // Регистрируем маппер для преобразования ResultSet в TeacherEntity
            this.jdbi.registerRowMapper(new TeacherMapper());

            // Создаем объект, реализующий интерфейс TeacherDao
            this.dao = jdbi.onDemand(TeacherDao.class);
        } catch (SQLException e) {
            log.error("Ошибка инициализации JDBI!", e);
            throw new RepositoryException("Ошибка инициализации JDBI!", e);
        }
    }

    // Сохранение нового учителя
    @Override
    public int save(TeacherEntity teacher) {
        try {
            // JDBI сам выполнит INSERT и вернет сгенерированный id
            int id = dao.insert(teacher);

            teacher.setId(id);
            return id;
        } catch (Exception e) {
            throw new RepositoryException("Ошибка при сохранении учителя!", e);
        }
    }

    // Поиск учителей по id
    @Override
    public Optional<TeacherEntity> findById(int id) {
        try {
            return dao.findById(id);
        } catch (Exception e) {
            throw new RepositoryException("Ошибка при поиске учителя по id: " + id, e);
        }
    }

    // Получение списка всех учителей
    @Override
    public List<TeacherEntity> findAll() {
        try {
            return dao.findAll();
        } catch (Exception e) {
            throw new RepositoryException("Ошибка при получении всех учителей", e);
        }
    }

    @Override
    public boolean update(TeacherEntity teacher) {
        if (teacher.getId() == null) { // Защита от обновления объекта без id
            log.warn("Попытка обновления учителя без id!");
            return false;
        }

        try {
            int updatedRows = dao.update(teacher);  // Возвращает количество обновленных строк
            return updatedRows > 0;                 // Если обновлена хотя бы одна строка - успех
        } catch (Exception e) {
            throw new RepositoryException("Ошибка при обновлении учителя с id: " + teacher.getId(), e);
        }
    }

    // Удаление учителя по id
    @Override
    public void deleteById(int id) {
        try {
            dao.deleteById(id); // Идемпотентная операция - можно вызывать много раз
        } catch (Exception e) {
            throw new RepositoryException("Ошибка при удалении учителя с id: " + id, e);
        }
    }
}