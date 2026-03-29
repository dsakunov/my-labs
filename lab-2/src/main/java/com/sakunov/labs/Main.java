package com.sakunov.labs;

import com.sakunov.labs.config.DatabaseConfig;
import com.sakunov.labs.entity.TeacherEntity;
import com.sakunov.labs.exception.RepositoryException;
import com.sakunov.labs.exception.ServiceException;
import com.sakunov.labs.migration.DatabaseMigrator;
import com.sakunov.labs.repository.TeacherRepository;
import com.sakunov.labs.repository.impl.TeacherRepositoryJdbi;
import com.sakunov.labs.service.TeacherService;
import com.sakunov.labs.service.impl.TeacherServiceImpl;
import com.sakunov.labs.util.DatabaseInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    private static final Scanner scanner = new Scanner(System.in);

    private static TeacherRepository teacherRepository;

    private static TeacherService teacherService;

    private static DatabaseConfig databaseConfig;

    public static void main(String[] args) {
        try {
            // Инициализация подключения к БД
            databaseConfig = new DatabaseConfig();

            // Запуск миграций Liquibase
            DatabaseMigrator migrator = new DatabaseMigrator(databaseConfig);
            DatabaseInitializer initializer = new DatabaseInitializer(migrator);
            initializer.createTableIfNotExists();

            // Создание репозитория
            teacherRepository = new TeacherRepositoryJdbi(databaseConfig);

            // Создание сервиса
            teacherService = new TeacherServiceImpl(teacherRepository);

            // Заполнение 10 учителями (если бд пуста)
            initializeWithTenTeachers();

            // Запуск меню
            runInteractiveMenu();

        } catch (RepositoryException e) {
            log.error("Ошибка репозитория: {}", e.getMessage());
            if (e.getCause() != null) {
                log.error("Причина: {}", e.getCause().getMessage());
            }
        } catch (ServiceException e) {
            log.error("Ошибка сервиса: {}", e.getMessage());
            System.out.println("Ошибка: " + e.getMessage());
        } catch (Exception e) {
            log.error("Неожиданная ошибка!", e);
            System.out.println("Произошла непредвиденная ошибка.");
        } finally {

            scanner.close();

            if (databaseConfig != null) {
                databaseConfig.close(); // Закрываем пул соединений
                log.info("Пул соединений с БД закрыт.");
            }

            log.info("Приложение завершено.");
        }
    }

    // Заполнение бд 10 учителями
    private static void initializeWithTenTeachers() {
        List<TeacherEntity> existingTeachers = teacherService.findAll();

        if (!existingTeachers.isEmpty()) {
            log.info("В базе уже есть {} учителей. Пропускаем инициализацию.", existingTeachers.size());
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

        log.info("Создание 10 тестовых учителей через сервис:");

        for (int i = 0; i < names.length; i++) {
            int id = teacherService.save(names[i], experiences[i]);
            log.info("  {}. {} (id: {})", i + 1, names[i], id);
        }
        log.info("Инициализация завершена. В базе теперь {} учителей.", teacherService.findAll().size());
    }

    // Запуск меню
    private static void runInteractiveMenu() {
        boolean running = true;

        while (running) {
            printMenu();
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
                    log.info("Выход из программы...");
                    break;
                default:
                    System.out.println("\nОшибка! Неверный выбор. Пожалуйста, введите число от 1 до 6.");
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n========== МЕНЮ ==========");
        System.out.println("1. Создать нового учителя");
        System.out.println("2. Найти учителя по ID");
        System.out.println("3. Показать всех учителей");
        System.out.println("4. Обновить данные учителя");
        System.out.println("5. Удалить учителя");
        System.out.println("6. Выход");
    }

    // Создание нового учителя
    private static void createTeacher() {
        System.out.print("\nВведите имя учителя: ");
        String name = scanner.nextLine().trim();

        int experience = 0;
        boolean validInput = false;

        while (!validInput) {
            System.out.print("Введите стаж работы (лет): ");
            try {
                experience = Integer.parseInt(scanner.nextLine().trim());
                if (experience < 0) {
                    System.out.println("Ошибка. Стаж не может быть отрицательным!\n");
                } else {
                    validInput = true;
                }
            } catch (NumberFormatException e) {
                System.out.println("Ошибка. Некорректное значение!\n");
            }
        }

        try {
            int id = teacherService.save(name, experience);
            log.info("Учитель успешно создан! ID: {}", id);
            System.out.println("Создан учитель с ID: " + id);
        } catch (ServiceException e) {
            System.out.println("Ошибка при создании: " + e.getMessage());
        }
    }

    // Поиск учителя по id
    private static void findTeacherById() {
        int id = 0;
        boolean validInput = false;

        while (!validInput) {
            System.out.print("\nВведите ID учителя: ");
            try {
                id = Integer.parseInt(scanner.nextLine().trim());
                validInput = true;
            } catch (NumberFormatException e) {
                System.out.println("\nОшибка. Некорректное значение!");
            }
        }

        try {
            TeacherEntity teacher = teacherService.findById(id);
            System.out.println("Найден учитель: " + teacher);
        } catch (ServiceException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    // Вывод всех учителей
    private static void findAllTeachers() {
        try {
            List<TeacherEntity> teachers = teacherService.findAll();
            if (teachers.isEmpty()) {
                System.out.println("\nВ базе данных нет учителей!");
            } else {
                System.out.println("\nНайдено учителей: " + teachers.size());
                for (int i = 0; i < teachers.size(); i++) {
                    System.out.println("  " + (i + 1) + ". " + teachers.get(i));
                }
            }
        } catch (Exception e) {
            System.out.println("Ошибка при получении списка: " + e.getMessage());
        }
    }

    // Обновление данных об учителе
    private static void updateTeacher() {
        int id = 0;
        boolean validId = false;

        while (!validId) {
            System.out.print("\nВведите ID учителя для обновления: ");
            try {
                id = Integer.parseInt(scanner.nextLine().trim());
                validId = true;
            } catch (NumberFormatException e) {
                System.out.println("\nОшибка. Некорректное значение!");
            }
        }

        try {
            TeacherEntity existingTeacher = teacherService.findById(id);
            System.out.println("\nТекущие данные: " + existingTeacher);
            System.out.println("(оставьте поле пустым, чтобы оставить без изменений)");

            System.out.print("Смена имени: " + existingTeacher.getName() + " -> ");
            String name = scanner.nextLine().trim();
            if (name.isEmpty()) {
                name = existingTeacher.getName();
            }

            int experience = existingTeacher.getExperienceYears();
            System.out.print("Смена стажа: " + experience + " -> ");
            String expInput = scanner.nextLine().trim();

            if (!expInput.isEmpty()) {
                try {
                    experience = Integer.parseInt(expInput);
                    if (experience < 0) {
                        System.out.println("\nОшибка. Стаж не может быть отрицательным! Оставлено прежнее значение.");
                        experience = existingTeacher.getExperienceYears();
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Ошибка. Некорректное значение! Оставлено прежнее значение.");
                }
            }

            TeacherEntity updatedTeacher = new TeacherEntity(id, name, experience);
            teacherService.update(updatedTeacher);

            System.out.println("\nДанные учителя успешно обновлены.");
            System.out.println("Обновлённый учитель: " + teacherService.findById(id));

        } catch (ServiceException e) {
            System.out.println("Ошибка при обновлении: " + e.getMessage());
        }
    }

    // Удаление учителя
    private static void deleteTeacher() {
        int id = 0;
        boolean validId = false;

        while (!validId) {
            System.out.print("\nВведите ID учителя для удаления: ");
            try {
                id = Integer.parseInt(scanner.nextLine().trim());
                validId = true;
            } catch (NumberFormatException e) {
                System.out.println("\nОшибка. Некорректное значение!");
            }
        }

        try {
            TeacherEntity existingTeacher = teacherService.findById(id);
            System.out.println("\nНайден учитель: " + existingTeacher);

            teacherService.deleteById(id);
            System.out.println("Учитель с ID " + id + " успешно удалён.");

            teacherService.deleteById(id);
            System.out.println("(идемпотентность: повторное удаление выполнено без ошибок)");

        } catch (ServiceException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
}