package com.sakunov.labs.service;

import com.sakunov.labs.model.Character;

import java.io.IOException; // Для обработки ошибок ввода-вывода
import java.util.ArrayList; // Для создания динамических списков
import java.util.Hashtable;
import java.util.List;

// Реализация интерфейса CharacterService
public class CharacterServiceImpl implements CharacterService {
    private static final String CHARACTERS_FILE = "characters.csv"; // Константа с именем файла

    @Override // Метод группировки имен персонажей по gender
    public Hashtable<String, List<String>> groupNamesByGender(List<Character> characters) {
        // Создаем Hashtable для хранения результатов
        Hashtable<String, List<String>> groupedByGender = new Hashtable<>();

        // Перебираем всех персонажей
        for (Character character : characters) {
            String gender = character.getGender();  // Получаем пол текущего персонажа
            String name = character.getName();      // И имя

            // Если пол не указан (null или пустая строка), используем "unknown"
            if (gender == null || gender.isEmpty()) {
                gender = "unknown";
            }

            // Пытаемся получить список имен для данного пола
            List<String> names = groupedByGender.get(gender);
            if (names == null) {
                names = new ArrayList<>();          // Если списка нет, создаем новый
                groupedByGender.put(gender, names); // Помещаем пустой список в Hashtable по ключу-полу
            }

            // Добавляем имя текущего персонажа в список
            names.add(name);
        }

        return groupedByGender;
    }

    @Override // Доп. метод добавления нового персонажа
    public void createCharacter(Character newCharacter) throws Exception {
        List<Character> characters = CsvReader.readCharacters(CHARACTERS_FILE); // Загружаем текущий список персонажей

        // Проверяем, существует ли уже персонаж с таким ID
        for (Character character : characters) {
            if (character.getId() == newCharacter.getId()) { // Сравнивая ID существующего с ID нового
                throw new Exception("Персонаж с ID " + newCharacter.getId() + " уже существует");
            }
        }

        characters.add(newCharacter);                               // Добавляем нового персонажа в список
        CsvWriter.writeAllCharacters(characters, CHARACTERS_FILE);  // Перезаписываем файл с новыми данными
    }

    @Override // Доп. метод поиска персонажа по ID
    public Character readCharacter(int id) throws IOException {
        List<Character> characters = CsvReader.readCharacters(CHARACTERS_FILE); // Загружаем текущий список персонажей

        // Перебираем список (ищем персонажа с нужным ID)
        for (Character character : characters) {
            if (character.getId() == id) { // Сравнивая ID текущего персонажа с нужным
                return character;
            }
        }

        return null; // Персонаж не найден
    }

    @Override // Доп. метод редактирования существующего персонажа
    public boolean updateCharacter(Character updatedCharacter) throws Exception {
        List<Character> characters = CsvReader.readCharacters(CHARACTERS_FILE); // Загружаем текущий список персонажей
        boolean found = false;                                                  // Флаг нахождения персонажа

        // Ищем персонажа и обновляем его данные
        for (int i = 0; i < characters.size(); i++) {
            // Сравниваем ID текущего персонажа с ID обновляемого
            if (characters.get(i).getId() == updatedCharacter.getId()) {
                characters.set(i, updatedCharacter); // Заменяем старого персонажа новым
                found = true;
                break;
            }
        }

        // Если нашли и обновили, сохраняем изменения
        if (found) {
            CsvWriter.writeAllCharacters(characters, CHARACTERS_FILE);  // Перезаписываем файл
        }

        return found;
    }

    // Доп. метод удаления персонажа по ID
    @Override
    public boolean deleteCharacter(int id) throws Exception {
        // Загружаем текущий список персонажей
        List<Character> characters = CsvReader.readCharacters(CHARACTERS_FILE);
        Character deletedCharacter = null; // Переменная для хранения удаляемого персонажа (для вывода)

        // Находим удаляемого персонажа для вывода информации (до удаления)
        for (Character character : characters) {
            if (character.getId() == id) {
                deletedCharacter = character; // Запоминаем для вывода
                break;
            }
        }

        /* Удаляем персонажа с указанным ID
           removeIf - метод коллекций, принимает предикат (условие)
           Для каждого character проверяется условие, если true - удаляется
           Возвращает true, если хотя бы один элемент был удален */
        boolean removed = characters.removeIf(character -> character.getId() == id);

        // Если удалили, сохраняем изменения
        if (removed) {
            CsvWriter.writeAllCharacters(characters, CHARACTERS_FILE); // Перезаписываем файл
            System.out.println("Найден персонаж: " + deletedCharacter.getName() + " (ID: " + id + ")");
        }

        return removed;
    }
}