package com.sakunov.labs;

import com.sakunov.labs.config.DatabaseConfig;
import com.sakunov.labs.entity.TeacherEntity;
import com.sakunov.labs.exception.RepositoryException;
import com.sakunov.labs.repository.TeacherRepository;
import com.sakunov.labs.repository.impl.TeacherRepositoryJdbi;
import com.sakunov.labs.util.DatabaseInitializer;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static TeacherRepository teacherRepository;

    public static void main(String[] args) {
        try {
            // Инициализация базы данных через Liquibase миграции
            // Метод внутри себя вызывает Liquibase
            // Liquibase проверяет, какие миграции уже были применены, и применяет только новые
            DatabaseInitializer.createTableIfNotExists();

            // Создание репозитория
            // TeacherRepositoryJdbi - реализация через JDBI
            // JDBI внутри использует HikariCP для получения соединений из пула
            teacherRepository = new TeacherRepositoryJdbi();

            // Заполнение 10 учителями (если бд пуста)
            initializeWithTenTeachers();

            // Запуск меню
            runInteractiveMenu();

        } catch (RepositoryException e) {
            System.err.println("\nОшибка репозитория: " + e.getMessage());
            if (e.getCause() != null) {
                System.err.println("Причина: " + e.getCause().getMessage());
            }
        } catch (Exception e) {
            System.err.println("\nНеожиданная ошибка: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();

            // Закрываем HikariCP connection pool
            DatabaseConfig.close();
            System.out.println("Приложение завершено!");
        }
    }

    // Метод инициализации бд 10 учителями
    private static void initializeWithTenTeachers() {
        // Проверяем, есть ли уже данные
        // findAll возвращает список всех учителей из бд
        List<TeacherEntity> existingTeachers = teacherRepository.findAll();

        // Если список не пустой, значит данные уже есть, не инициализируем
        if (!existingTeachers.isEmpty()) {
            System.out.println("\nВ базе уже есть " + existingTeachers.size() + " учителей. Пропускаем инициализацию.");
            return;
        }

        String[] names = {
                "Иван Петров",
                "Мария Сидорова",
                "Алексей Иванов",
                "Елена Смирнова",
                "Дмитрий Козлов",
                "Ольга Новикова",
                "Сергей Морозов",
                "Анна Волкова",
                "Павел Соколов",
                "Наталья Лебедева"
        };

        // Стаж работы
        int[] experiences = {10, 5, 15, 8, 12, 3, 20, 7, 25, 4};

        System.out.println("\nСоздание 10 тестовых учителей:");

        // Цикл создания 10 учителей
        for (int i = 0; i < names.length; i++) {
            // Создаем объект учителя с именем и стажем из массивов
            TeacherEntity teacher = new TeacherEntity(names[i], experiences[i]);

            // Сохраняем учителя в бд и получаем сгенерированный id
            // save() возвращает автоматически сгенерированный базой данных ID
            int id = teacherRepository.save(teacher);

            System.out.println("  " + (i+1) + ". " + teacher + " (id: " + id + ")");
        }
        System.out.println("Инициализация завершена. В базе теперь " + teacherRepository.findAll().size() + " учителей.");
    }

    // Метод для запуска меню
    private static void runInteractiveMenu() {
        boolean running = true;

        while (running) {
            printMenu(); // Вывод меню
            System.out.print("Выберите действие: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    createTeacher();
                    break;
                case "2":
                    findTeacherById();
                    break;
                case "3":
                    findAllTeachers();
                    break;
                case "4":
                    updateTeacher();
                    break;
                case "5":
                    deleteTeacher();
                    break;
                case "6":
                    running = false;
                    System.out.println("\nВыход из программы...");
                    break;
                default:
                    System.out.println("\nОшибка! Неверный выбор. Пожалуйста, введите число от 1 до 6.");
            }
        }
    }

    // Метод для вывода меню
    private static void printMenu() {
        System.out.println("\n========== МЕНЮ ==========");
        System.out.println("1. Создать нового учителя");
        System.out.println("2. Найти учителя по ID");
        System.out.println("3. Показать всех учителей");
        System.out.println("4. Обновить данные учителя");
        System.out.println("5. Удалить учителя");
        System.out.println("6. Выход");
    }

    // Метод для создания нового учителя
    private static void createTeacher() {
        System.out.print("\nВведите имя учителя: ");
        String name = scanner.nextLine().trim();

        int experience = 0;
        boolean validInput = false;

        // Цикл до тех пор, пока не будет введено корректное значение
        while (!validInput) {
            System.out.print("Введите стаж работы (лет): ");
            try {
                // Пытаемся преобразовать ввод в число
                experience = Integer.parseInt(scanner.nextLine().trim());

                // Проверка на отрицательное значение
                if (experience < 0) {
                    System.out.println("Ошибка! Стаж не может быть отрицательным.\n");
                } else {
                    validInput = true;
                }
            } catch (NumberFormatException e) { // И случай, если введено не число
                System.out.println("Ошибка! Некорректное значение.\n");
            }
        }

        // Создаем объект и сохраняем в бд
        TeacherEntity teacher = new TeacherEntity(name, experience);
        int id = teacherRepository.save(teacher);
        System.out.println("\nУчитель успешно создан! ID: " + id);
        System.out.println("Создан: " + teacher);
    }

    // Метод для поиска учителя по id
    private static void findTeacherById() {
        int id = 0;
        boolean validInput = false;

        while (!validInput) {
            System.out.print("\nВведите ID учителя: ");
            try {
                id = Integer.parseInt(scanner.nextLine().trim());
                validInput = true;
            } catch (NumberFormatException e) {
                System.out.println("\nОшибка! Некорректное значение.");
            }
        }

        // Optional - контейнер, который может содержать или не содержать значение
        Optional<TeacherEntity> teacherOpt = teacherRepository.findById(id);

        // isPresent() возвращает true, если значение есть
        if (teacherOpt.isPresent()) {
            // get() извлекает значение из Optional
            System.out.println("Найден учитель: " + teacherOpt.get());
        } else {
            System.out.println("Учитель с ID " + id + " не найден.");
        }
    }

    // Метод для вывода всех учителей
    private static void findAllTeachers() {
        // Получение данных
        List<TeacherEntity> teachers = teacherRepository.findAll();

        // Проверка на пустоту
        if (teachers.isEmpty()) {
            System.out.println("\nВ базе данных нет учителей.");
        } else {
            System.out.println("\nНайдено учителей: " + teachers.size());
            for (int i = 0; i < teachers.size(); i++) {
                System.out.println("  " + (i+1) + ". " + teachers.get(i));
            }
        }
    }

    // Метод для
    private static void updateTeacher() {
        int id = 0;
        boolean validId = false;

        while (!validId) {
            System.out.print("\nВведите ID учителя для обновления: ");
            try {
                id = Integer.parseInt(scanner.nextLine().trim());
                validId = true;
            } catch (NumberFormatException e) {
                System.out.println("\nОшибка! Некорректное значение.");
            }
        }

        // Проверка существования учителя
        Optional<TeacherEntity> existingTeacher = teacherRepository.findById(id);
        if (existingTeacher.isEmpty()) {
            System.out.println("\nУчитель с ID " + id + " не найден. Обновление невозможно.");
            return; // В таком случае сразу выходим
        }

        // Вывод текущих данных
        System.out.println("\nТекущие данные: " + existingTeacher.get());
        System.out.println("(Оставьте поле пустым, чтобы оставить без изменений)");

        // Ввод нового имени
        System.out.print("Смена имени: " + existingTeacher.get().getName() + " -> ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            name = existingTeacher.get().getName();
        }

        // И нового стажа
        int experience = existingTeacher.get().getExperienceYears();
        System.out.print("Смена стажа: " + experience + " -> ");
        String expInput = scanner.nextLine().trim();

        // Если поле не пустое, пытаемся преобразовать в число
        if (!expInput.isEmpty()) {
            try {
                experience = Integer.parseInt(expInput);

                // Проверка на отрицательное значение
                if (experience < 0) {
                    System.out.println("\nОшибка! Стаж не может быть отрицательным. Оставлено прежнее значение.");
                    experience = existingTeacher.get().getExperienceYears(); // Тогда возвращаем старое значение
                }
            } catch (NumberFormatException e) {
                System.out.println("Ошибка! Некорректное значение. Оставлено прежнее значение.");
            }
        }

        // Создание обновленного объекта
        TeacherEntity updatedTeacher = new TeacherEntity(id, name, experience);

        // update() возвращает true, если обновление успешно (затронута хотя бы 1 строка)
        boolean success = teacherRepository.update(updatedTeacher);

        if (success) {
            System.out.println("\nДанные учителя успешно обновлены!");

            // Повторный запрос из БД для отображения актуальных данных
            System.out.println("Обновленный учитель: " + teacherRepository.findById(id).get());
        } else {
            System.out.println("\nОшибка! Не удалось обновить данные учителя.");
        }
    }

    // Метод для удаления учителя
    private static void deleteTeacher() {
        int id = 0;
        boolean validId = false;

        while (!validId) {
            System.out.print("\nВведите ID учителя для удаления: ");
            try {
                id = Integer.parseInt(scanner.nextLine().trim());
                validId = true;
            } catch (NumberFormatException e) {
                System.out.println("\nОшибка! Некорректное значение.");
            }
        }

        // Проверка существования учителя
        Optional<TeacherEntity> existingTeacher = teacherRepository.findById(id);
        if (existingTeacher.isEmpty()) {
            System.out.println("\nУчитель с ID " + id + " не найден.");
            return;
        }

        // Вывод информации о нем
        System.out.println("\nНайден учитель: " + existingTeacher.get());

        // Удаление (первое, обычное)
        teacherRepository.deleteById(id);
        System.out.println("Учитель с ID " + id + " успешно удален.");

        // Тут демонстрация идемпотентности
        // Повторное удаление того же id
        // То есть, учителя уже нет, но метод не вызывает ошибку -> повторный вызов безопасен
        teacherRepository.deleteById(id);
        System.out.println("(идемпотентность: повторное удаление выполнено без ошибок)");
    }
}