package com.sakunov.labs.entity;

// Модель данных учитель
public class TeacherEntity {
    // Тип Integer, а не int
    // int: не может быть null (по умолчанию 0), Integer: может быть null (ещё не сохранён в БД)
    // У новой сущности, ещё не сохранённой в БД, id = null
    private Integer id;             // Идентификатор
    private String name;            // Имя
    private Integer experienceYears;// Стаж работы

    // Конструктор по умолчанию
    public TeacherEntity() {
    }

    // Конструктор для создания нового учителя (без id)
    // Используется, когда мы создаём учителя для сохранения в БД
    // ID ещё не известен, его сгенерирует БД
    public TeacherEntity(String name, Integer experienceYears) {
        this.name = name;
        this.experienceYears = experienceYears;
    }

    // Конструктор для полного обновления учителя
    // Используется, когда мы загружаем учителя из БД или обновляем его
    public TeacherEntity(Integer id, String name, Integer experienceYears) {
        this.id = id;
        this.name = name;
        this.experienceYears = experienceYears;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(Integer experienceYears) {
        this.experienceYears = experienceYears;
    }

    @Override
    public String toString() {
        return "TeacherEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", experienceYears=" + experienceYears +
                '}';
    }
}