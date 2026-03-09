package com.sakunov.labs.repository.impl;

import com.sakunov.labs.config.DatabaseConfig;
import com.sakunov.labs.entity.TeacherEntity;
import com.sakunov.labs.exception.RepositoryException;
import com.sakunov.labs.repository.TeacherDao;
import com.sakunov.labs.repository.TeacherMapper;
import com.sakunov.labs.repository.TeacherRepository;

// Главный класс JDBI
import org.jdbi.v3.core.Jdbi;
// Позволяет использовать аннотации в интерфейсах, чтобы не писать вручную весь код для работы с БД
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

// Реализация репозитория через JDBI
public class TeacherRepositoryJdbi implements TeacherRepository {

    private final Jdbi jdbi;        // Главный класс библиотеки
    private final TeacherDao dao;   // Интерфейс с аннотациями

    // Конструктор репозитория
    public TeacherRepositoryJdbi() {
        try {
            // Создаем экземпляр Jdbi с connection pool от HikariCP
            this.jdbi = Jdbi.create(DatabaseConfig.getConnection());

            // Устанавливаем plugin для поддержки sql object
            this.jdbi.installPlugin(new SqlObjectPlugin());

            // Регистрируем маппер для преобразования строк ResultSet в объекты TeacherEntity
            this.jdbi.registerRowMapper(new TeacherMapper());

            // Создает объект, реализующий интерфейс TeacherDao
            // onDemand - методы DAO выполняются немедленно, каждый раз создавая новое соединение (из пула)
            this.dao = jdbi.onDemand(TeacherDao.class);
        } catch (SQLException e) {
            throw new RepositoryException("Ошибка инициализации JDBI", e);
        }
    }

    // Метод сохранения нового учителя
    @Override
    public int save(TeacherEntity teacher) {
        try {
            // JDBI автоматически вставляет и возвращает сгенерированный ID
            int id = dao.insert(teacher);
            teacher.setId(id);
            return id;
        } catch (Exception e) {
            throw new RepositoryException("Ошибка при сохранении учителя", e);
        }
    }

    // Метод поиска учителя по id
    @Override
    public Optional<TeacherEntity> findById(int id) {
        try {
            return dao.findById(id);
        } catch (Exception e) {
            throw new RepositoryException("Ошибка при поиске учителя по id: " + id, e);
        }
    }

    // Метод для получения списка всех учителей
    @Override
    public List<TeacherEntity> findAll() {
        try {
            return dao.findAll();
        } catch (Exception e) {
            throw new RepositoryException("Ошибка при получении всех учителей", e);
        }
    }

    // Метод для обновления данных учителя
    @Override
    public boolean update(TeacherEntity teacher) {
        if (teacher.getId() == null) { // защита от попытки обновить объект без id
            return false;
        }

        try {
            // Выполняем update и получаем количество изменённых строк
            int updatedRows = dao.update(teacher);

            // Если обновлена хотя бы одна строка - успех
            return updatedRows > 0;
        } catch (Exception e) {
            throw new RepositoryException("Ошибка при обновлении учителя с id: " + teacher.getId(), e);
        }
    }

    // Метод для удаления учителя по id
    @Override
    public void deleteById(int id) {
        try {
            dao.deleteById(id);
            // Не возвращаем результат, так как идемпотентность гарантирует безопасность
        } catch (Exception e) {
            throw new RepositoryException("Ошибка при удалении учителя с id: " + id, e);
        }
    }
}