package com.sakunov.labs.service;

import com.sakunov.labs.entity.TeacherEntity;
import java.util.List;

// Интерфейс сервисного слоя для работы с учителями
public interface TeacherService {
    int save(String name, Integer experienceYears);

    TeacherEntity findById(int id);

    TeacherEntity findByName(String name);

    List<TeacherEntity> findAll();

    void update(TeacherEntity teacher);

    void deleteById(int id);
}