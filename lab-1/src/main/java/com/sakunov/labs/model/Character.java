package com.sakunov.labs.model;

// Персонаж
public class Character {
    private int id;             // ID
    private String name;        // Имя
    private String status;      // Статус
    private String species;     // Вид
    private String type;        // Тип
    private String gender;      // Пол
    private String origin;      // Происхождение
    private String location;    // Локация
    private String created;     // Дата создания записи

    // Конструктор по умолчанию
    public Character() {
    }

    // Конструктор с параметрами
    public Character(int id, String name, String status, String species, String type,
                     String gender, String origin, String location, String created) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.species = species;
        this.type = type;
        this.gender = gender;
        this.origin = origin;
        this.location = location;
        this.created = created;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    @Override // Переопределение метода toString()
    public String toString() {
        return String.format("Character{id=%d, name='%s', status='%s', species='%s', gender='%s'}",
                id, name, status, species, gender); // Возвращаем строку
    }

    // Метод для преобразования объекта в строку формата CSV
    public String toCsvString() {
        return String.format("%d,%s,%s,%s,%s,%s,%s,%s,%s",
                id, name, status, species,
                type == null ? "" : type, // Если тип null, записываем пустую строку
                gender,
                origin,
                location,
                created);
    }
}