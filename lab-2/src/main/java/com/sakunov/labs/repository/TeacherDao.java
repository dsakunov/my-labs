package com.sakunov.labs.repository;

import com.sakunov.labs.entity.TeacherEntity;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;  // Регистрирует маппер для этого интерфейса
import org.jdbi.v3.sqlobject.customizer.Bind;           // Привязывает параметр метода по имени
import org.jdbi.v3.sqlobject.customizer.BindBean;       // Привязывает все поля объекта как параметр
import org.jdbi.v3.sqlobject.statement.SqlQuery;        // Указывает, что метод выполняет SELECT
import org.jdbi.v3.sqlobject.statement.SqlUpdate;       // Указывает, что метод выполняет INSERT/UPDATE/DELETE

import java.util.List;
import java.util.Optional;


// Интерфейс для работы с учителями
// JDBI автоматически реализует эти методы на основе аннотаций
@RegisterRowMapper(TeacherMapper.class) // Говорим JDBI, как превращать строки БД в объекты TeacherEntity
public interface TeacherDao {
    // Метод для вставки нового учителя. @SqlUpdate - аннотация для запросов, которые ИЗМЕНЯЮТ данные
    // INSERT INTO teacher - вставить в таблицу teacher
    // (name, experience_years) - в колонки name и experience_years
    // VALUES (:name, :experienceYears) - значения из параметров :name и :experienceYears
    // RETURNING id - вернуть сгенерированный ID
    // @BindBean - привязка всех полей объекта
    @SqlUpdate("INSERT INTO teacher (name, experience_years) VALUES (:name, :experienceYears) RETURNING id")
    int insert(@BindBean TeacherEntity teacher);

    // Метод для поиска по id. @SqlQuery - аннотация для запросов, которые ВОЗВРАЩАЮТ данные
    // @Bind - привязка одного параметра
    @SqlQuery("SELECT id, name, experience_years FROM teacher WHERE id = :id")
    Optional<TeacherEntity> findById(@Bind("id") int id);

    // Метод для получения всех учителей.
    @SqlQuery("SELECT id, name, experience_years FROM teacher ORDER BY id")
    List<TeacherEntity> findAll();

    // Метод для обновления данных учителя
    // SET - устанавливаем новые значения для колонок
    // WHERE id = :id - обновляем только запись с указанным ID
    @SqlUpdate("UPDATE teacher SET name = :name, experience_years = :experienceYears WHERE id = :id")
    int update(@BindBean TeacherEntity teacher);

    // Метод для удаления учителя по id
    @SqlUpdate("DELETE FROM teacher WHERE id = :id")
    void deleteById(@Bind("id") int id);
}