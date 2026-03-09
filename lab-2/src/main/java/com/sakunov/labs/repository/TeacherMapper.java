package com.sakunov.labs.repository;

import com.sakunov.labs.entity.TeacherEntity;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

// Маппер для преобразования ResultSet в TeacherEntity
public class TeacherMapper implements RowMapper<TeacherEntity> {

    @Override
    public TeacherEntity map(ResultSet rs, StatementContext ctx) throws SQLException {
        TeacherEntity teacher = new TeacherEntity();

        // Читаем данные из текущей строки ResultSet и заполняем объект
        teacher.setId(rs.getInt("id"));
        teacher.setName(rs.getString("name"));
        teacher.setExperienceYears(rs.getInt("experience_years"));
        return teacher;
    }
}