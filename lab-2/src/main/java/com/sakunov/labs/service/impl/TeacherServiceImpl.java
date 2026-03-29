package com.sakunov.labs.service.impl;

import com.sakunov.labs.entity.TeacherEntity;
import com.sakunov.labs.exception.ServiceException;
import com.sakunov.labs.repository.TeacherRepository;
import com.sakunov.labs.service.TeacherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

// Реализация сервисного слоя
public class TeacherServiceImpl implements TeacherService {
    private static final Logger log = LoggerFactory.getLogger(TeacherServiceImpl.class);

    private static final String TEACHER_NOT_FOUND_MESSAGE = "Учитель не найден";

    private final TeacherRepository teacherRepository;

    // Конструктор: принимаем репозиторий
    public TeacherServiceImpl(TeacherRepository teacherRepository) {
        this.teacherRepository = teacherRepository;
    }

    @Override
    public int save(String name, Integer experienceYears) {
        if (name == null || name.trim().isEmpty()) {
            log.warn("Попытка создать учителя с пустым именем");
            throw new ServiceException("Имя учителя не может быть пустым");
        }
        if (experienceYears == null || experienceYears < 0) {
            log.warn("Попытка создать учителя с некорректным стажем: {}", experienceYears);
            throw new ServiceException("Стаж работы не может быть отрицательным");
        }

        // Создаём объект учителя и сохраняем его через репозиторий
        TeacherEntity teacher = new TeacherEntity(name, experienceYears);
        int id = teacherRepository.save(teacher);
        log.info("Учитель сохранён с ID: {}", id);
        return id;
    }

    @Override
    public TeacherEntity findById(int id) {
        Optional<TeacherEntity> teacherOpt = teacherRepository.findById(id);

        if (teacherOpt.isEmpty()) {
            log.warn("Учитель с ID {} не найден", id);
            throw new ServiceException(TEACHER_NOT_FOUND_MESSAGE + " с ID: " + id);
        }

        log.info("Учитель с ID {} найден", id);
        return teacherOpt.get();
    }

    @Override
    public TeacherEntity findByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            log.warn("Попытка поиска учителя по пустому имени");
            throw new ServiceException("Имя для поиска не может быть пустым");
        }

        // Получаем всех учителей и ищем нужного по имени
        List<TeacherEntity> allTeachers = teacherRepository.findAll();

        for (TeacherEntity teacher : allTeachers) {
            if (name.equals(teacher.getName())) {
                log.info("Учитель с именем '{}' найден", name);
                return teacher;
            }
        }

        // Если не нашли — бросаем исключение
        log.warn("Учитель с именем '{}' не найден", name);
        throw new ServiceException(TEACHER_NOT_FOUND_MESSAGE + " с именем: " + name);
    }

    @Override
    public List<TeacherEntity> findAll() {
        log.info("Запрос на получение всех учителей");
        return teacherRepository.findAll();
    }

    @Override
    public void update(TeacherEntity teacher) {
        if (teacher == null) {
            log.warn("Попытка обновить null-учителя");
            throw new ServiceException("Объект учителя не может быть null");
        }
        if (teacher.getId() == null) {
            log.warn("Попытка обновить учителя без ID");
            throw new ServiceException("ID учителя обязателен для обновления");
        }
        if (teacher.getName() == null || teacher.getName().trim().isEmpty()) {
            log.warn("Попытка обновить учителя с пустым именем");
            throw new ServiceException("Имя учителя не может быть пустым");
        }

        if (teacherRepository.findById(teacher.getId()).isEmpty()) {
            log.warn("Попытка обновить несуществующего учителя с ID: {}", teacher.getId());
            throw new ServiceException(TEACHER_NOT_FOUND_MESSAGE + " с ID: " + teacher.getId());
        }

        // Обновляем данные через репозиторий
        boolean updated = teacherRepository.update(teacher);
        if (!updated) {
            log.error("Не удалось обновить учителя с ID: {}", teacher.getId());
            throw new ServiceException("Ошибка при обновлении учителя с ID: " + teacher.getId());
        }

        log.info("Учитель с ID {} успешно обновлён", teacher.getId());
    }

    @Override
    public void deleteById(int id) {
        // Удаляем учителя по ID через репозиторий
        teacherRepository.deleteById(id);
        log.info("Учитель с ID {} удалён (или уже отсутствовал)", id);
    }
}