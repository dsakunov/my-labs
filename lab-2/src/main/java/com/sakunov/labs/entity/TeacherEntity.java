package com.sakunov.labs.entity;

// Сущность учителя
public class TeacherEntity {
    private Integer id;
    private String name;
    private Integer experienceYears;

    public TeacherEntity() {
    }

    // Конструктор для создания нового учителя (без id)
    public TeacherEntity(String name, Integer experienceYears) {
        this.name = name;
        this.experienceYears = experienceYears;
    }

    // Конструктор для обновления учителя
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