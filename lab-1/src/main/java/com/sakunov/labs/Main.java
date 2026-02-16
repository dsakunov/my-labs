package com.sakunov.labs;

import com.sakunov.labs.model.Character;                // Класс-модель персонажа
import com.sakunov.labs.service.CharacterService;       // Интерфейс сервиса
import com.sakunov.labs.service.CharacterServiceImpl;   // Реализация сервиса
import com.sakunov.labs.service.CsvReader;              // Для чтения файла
import com.sakunov.labs.service.CsvWriter;              // Для записи файла
import com.sakunov.labs.util.FileUtil;                  // Утилиты для работы с файлами

import java.util.Hashtable; // Класс Hashtable
import java.util.List;      // Интерфейс List
import java.util.Scanner;   // Для ввода с консоли

public class Main {
    private static final String INPUT_FILE = "characters.csv";          // Входной файл
    private static final String OUTPUT_FILE = "grouped_by_gender.csv";  // Выходной файл с результатом

    public static void main(String[] args) {
        try {
            // Проверка существования файла: Вызов утилитного метода для проверки
            if (!FileUtil.checkFileExists(INPUT_FILE)) {
                System.err.println("ОШИБКА: Файл " + INPUT_FILE + " не найден в директории resources");
                System.err.println("Пожалуйста, поместите файл " + INPUT_FILE + " в папку lab-1/src/main/resources/");
                return;
            }

            // 1) Чтение данных из файла
            System.out.println("Чтение данных из файла...");
            // Вызываем статический метод CsvReader.readCharacters() для чтения файла
            // Этот метод вернет список объектов Character, созданных из строк CSV
            List<Character> characters = CsvReader.readCharacters(INPUT_FILE);
            System.out.println("Загружено персонажей: " + characters.size());

            // 2) Обработка данных - группировка по gender
            // Создаем экземпляр сервиса для работы с персонажами
            CharacterService characterService = new CharacterServiceImpl();
            // Ключ - пол (String), значение - список имен (List<String>)
            Hashtable<String, List<String>> groupedByGender = characterService.groupNamesByGender(characters);

            // Вывод результата группировки
            System.out.println("\nРезультат группировки по полу:");
            for (String gender : groupedByGender.keySet()) {    // Проходим по всем ключам Hashtable (по всем найденным значениям пола)
                List<String> names = groupedByGender.get(gender);   // Список имен для данного пола
                System.out.println(gender + " (" + names.size() + "): " + names);   // Пол (кол-во): [список имен]
            }

            // 3) Запись результата в файл
            CsvWriter.writeGroupedByGender(groupedByGender, OUTPUT_FILE);
            System.out.println("\nРезультат сохранен в файл " + OUTPUT_FILE);

            // ДОП. ЗАДАНИЕ: CRUD операции
            performCrudOperations(characterService);

        } catch (Exception e) { // Exception - "родитель" всех проверяемых исключений
            System.err.println("Ошибка при выполнении программы: " + e.getMessage());
        }
    }

    // Метод для выполнения CRUD операций
    private static void performCrudOperations(CharacterService characterService) {
        Scanner scanner = new Scanner(System.in);   // Для чтения ввода пользователя
        boolean running = true;                     // Пока running = true, меню будет показываться

        while (running) {
            // Меню операций
            System.out.println("\n--- CRUD операции с персонажами ---");
            System.out.println("1. Добавить персонажа (Create)");
            System.out.println("2. Найти персонажа по ID (Read)");
            System.out.println("3. Обновить персонажа (Update)");
            System.out.println("4. Удалить персонажа (Delete)");
            System.out.println("5. Показать всех персонажей");
            System.out.println("6. Выйти");
            System.out.print("Выберите действие -> ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Очистка буфера после nextInt()

            try {
                switch (choice) {
                    case 1: // CREATE - создание нового персонажа
                        // Метод для ввода данных персонажа
                        Character newCharacter = createCharacterFromInput(scanner);

                        // Метод сервиса для сохранения персонажа
                        characterService.createCharacter(newCharacter);

                        System.out.println("Персонаж успешно добавлен!");
                        break;

                    case 2: // READ - чтение персонажа по ID
                        System.out.print("Введите ID персонажа: ");
                        int id = scanner.nextInt(); // Читаем ID для поиска
                        scanner.nextLine();

                        // Ищем персонажа через сервис
                        Character character = characterService.readCharacter(id);

                        // Проверка, найден ли персонаж
                        if (character != null) {
                            System.out.println("Найден персонаж: " + character);
                        } else {
                            System.out.println("Персонаж с ID " + id + " не найден");
                        }
                        break;

                    case 3:// UPDATE - обновление существующего персонажа
                        System.out.print("Введите ID персонажа для обновления: ");
                        int updateId = scanner.nextInt();
                        scanner.nextLine();

                        // Создаем нового персонажа с обновленными данными
                        Character updatedCharacter = createCharacterFromInput(scanner);
                        updatedCharacter.setId(updateId); // Устанавливаем ID

                        // Вызов метода обновления
                        boolean updated = characterService.updateCharacter(updatedCharacter);

                        // Проверка результата обновления
                        if (updated) {
                            System.out.println("Персонаж успешно обновлен!");
                        } else {
                            System.out.println("Персонаж с ID " + updateId + " не найден");
                        }
                        break;

                    case 4: // DELETE - удаление персонажа
                        System.out.print("Введите ID персонажа для удаления: ");
                        int deleteId = scanner.nextInt();
                        scanner.nextLine();

                        // Вызов метода удаления
                        boolean deleted = characterService.deleteCharacter(deleteId);

                        // Проверка результата удаления
                        if (deleted) {
                            System.out.println("Персонаж успешно удален!");
                        } else {
                            System.out.println("Персонаж с ID " + deleteId + " не найден");
                        }
                        break;

                    case 5: // Показать всех персонажей
                        // Чтение всех персонажей из файла
                        List<Character> allCharacters = CsvReader.readCharacters(INPUT_FILE);

                        // Вывод информации о каждом
                        System.out.println("\nВсе персонажи (" + allCharacters.size() + "):");
                        for (Character c : allCharacters) {
                            System.out.println(c);
                        }
                        break;

                    case 6: // Выход из меню
                        running = false; // Смена флага для выхода из цикла
                        System.out.println("Выход из CRUD операций");
                        break;

                    default:
                        System.out.println("Неверный выбор. Попробуйте снова.");
                }
            } catch (Exception e) {
                System.err.println("Ошибка при выполнении CRUD операции: " + e.getMessage());
            }
        }
        scanner.close();
    }

    // Метод для создания персонажа (для операции CREATE)
    private static Character createCharacterFromInput(Scanner scanner) {
        System.out.println("Введите данные персонажа:");

        System.out.print("ID: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Имя: ");
        String name = scanner.nextLine();

        System.out.print("Статус (Alive/Dead/unknown): ");
        String status = scanner.nextLine();

        System.out.print("Вид (Human/Alien/unknown): ");
        String species = scanner.nextLine();

        System.out.print("Тип (оставьте пустым если нет): ");
        String type = scanner.nextLine();

        System.out.print("Пол (Male/Female/unknown): ");
        String gender = scanner.nextLine();

        System.out.print("Происхождение: ");
        String origin = scanner.nextLine();

        System.out.print("Локация: ");
        String location = scanner.nextLine();

        // Создаем нового персонажа
        return new Character(
                id,
                name,
                status,
                species,
                type.isEmpty() ? null : type, // Пустую строку превращаем в null
                gender,
                origin,
                location,
                java.time.Instant.now().toString()); // Текущее время как дата создания
    }
}