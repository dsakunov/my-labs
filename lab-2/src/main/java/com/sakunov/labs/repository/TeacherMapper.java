package com.sakunov.labs.repository;

import com.sakunov.labs.entity.TeacherEntity;

import org.jdbi.v3.core.mapper.RowMapper;           // Интерфейс для маппера строк
import org.jdbi.v3.core.statement.StatementContext; // Контекст выполнения запроса
import java.sql.ResultSet;                          // Представляет строку результата из БД
import java.sql.SQLException;

// Маппер для преобразования строки из базы данных (ResultSet) в Java-объект (TeacherEntity)
public class TeacherMapper implements RowMapper<TeacherEntity> {
    // map - преобразований одной строки в объект
    // ResultSet rs - результат запроса, указывающий на текущую строку
    // StatementContext ctx - контекст выполнения, содержит доп. информацию
    // TeacherEntity - созданный объект учителя
    @Override
    public TeacherEntity map(ResultSet rs, StatementContext ctx) throws SQLException {
        TeacherEntity teacher = new TeacherEntity(); // Создаем пустой объект
        teacher.setId(rs.getInt("id")); // Читаем колонку "id" как int и устанавливаем в объект
        teacher.setName(rs.getString("name")); // Строку (имя)
        teacher.setExperienceYears(rs.getInt("experience_years")); // И число (стаж)
        return teacher; // И возвращаем готовый объект
    }
}