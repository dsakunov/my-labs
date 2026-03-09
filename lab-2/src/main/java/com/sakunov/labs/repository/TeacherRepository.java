package com.sakunov.labs.repository;

import com.sakunov.labs.entity.TeacherEntity;
import java.util.List;
import java.util.Optional;

// Контракт для работы с учителями в базе данных
public interface TeacherRepository {

    // Метод для сохранения учителя
    int save(TeacherEntity teacher);

    // Метод для поиска учителя по id
    Optional<TeacherEntity> findById(int id);

    // Метод для получения всех учителей из бд
    List<TeacherEntity> findAll();

    // Метод для обновления учителя
    boolean update(TeacherEntity teacher);

    // Метод для удаления учителя по id
    void deleteById(int id);
}