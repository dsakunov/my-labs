package com.sakunov.labs.repository;

import com.sakunov.labs.entity.TeacherEntity;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import java.util.List;
import java.util.Optional;

// JDBI DAO интерфейс для работы с учителями
@RegisterRowMapper(TeacherMapper.class) // Указываем, как преобразовывать строки БД в объекты
public interface TeacherDao {
    // Вставка нового учителя, RETURNING id - получаем сгенерированный БД идентификатор
    @SqlUpdate("INSERT INTO teacher (name, experience_years) VALUES (:name, :experienceYears) RETURNING id")
    @GetGeneratedKeys("id")
    int insert(@BindBean TeacherEntity teacher);

    // Поиск учителя по id, результат может отсутствовать -> Optional
    @SqlQuery("SELECT id, name, experience_years FROM teacher WHERE id = :id")
    Optional<TeacherEntity> findById(@Bind("id") int id);

    // Получение всех учителей, отсортированных по id
    @SqlQuery("SELECT id, name, experience_years FROM teacher ORDER BY id")
    List<TeacherEntity> findAll();

    // Обновление данных учителя, возвращает количество обновленных строк
    @SqlUpdate("UPDATE teacher SET name = :name, experience_years = :experienceYears WHERE id = :id")
    int update(@BindBean TeacherEntity teacher);

    // Удаление учителя по id (идемпотентная операция)
    @SqlUpdate("DELETE FROM teacher WHERE id = :id")
    void deleteById(@Bind("id") int id);
}