package com.sakunov.labs.repository;

import com.sakunov.labs.entity.TeacherEntity;
import java.util.List;
import java.util.Optional;

// Контракт репозитория для работы с учителями в бд
public interface TeacherRepository {
    int save(TeacherEntity teacher);

    Optional<TeacherEntity> findById(int id);

    List<TeacherEntity> findAll();

    boolean update(TeacherEntity teacher);

    void deleteById(int id);
}