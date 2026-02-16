package com.sakunov.labs.service;

import com.sakunov.labs.model.Character;
import com.sakunov.labs.util.FileUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Сервис для чтения данных из CSV файлов
public class CsvReader {
    // Метод читает всех персонажей из файла
    public static List<Character> readCharacters(String fileName) throws IOException {
        List<Character> characters = new ArrayList<>();  // Создаем пустой список

        // try-with-resources - автоматически закрывает BufferedReader после выполнения блока
        try (BufferedReader reader = FileUtil.createReader(fileName)) {
            String line;                // Переменная для хранения текущей строки
            boolean isHeader = true;    // Флаг для пропуска первой строки (заголовка)
            int lineNumber = 0;         // Счетчик строк для отладки

            // Читаем файл построчно
            while ((line = reader.readLine()) != null) {
                lineNumber++; // Увеличиваем счетчик строк

                if (isHeader) {
                    // Пропускаем строку с заголовками колонок
                    isHeader = false;
                    continue;
                }

                // Если строка пустая или состоит только из пробелов - пропускаем
                if (line.trim().isEmpty()) {
                    continue;
                }

                // Парсим строку и добавляем персонажа в список
                // Вызываем вспомогательный метод
                Character character = parseCsvLine(line, lineNumber);
                if (character != null) {
                    characters.add(character); // Добавляем в список
                }
            } // Здесь автоматически вызывается reader.close()
        }

        return characters;
    }

    // Метод парсинга строки
    private static Character parseCsvLine(String line, int lineNumber) {
        // Разделяем строку по запятым, -1 чтобы сохранить пустые поля в конце
        String[] fields = line.split(",", -1);

        // Проверяем, что у нас достаточно полей (должно быть 9)
        if (fields.length < 9) {
            System.err.println("Строка " + lineNumber + " содержит недостаточно полей: " + line);
            return null;
        }

        // try-catch для обработки ошибок преобразования типов
        try {
            // Извлекаем значения из полей, обрезая пробелы
            // fields[0].trim() - удаляет пробелы в начале и конце строки
            // Integer.parseInt() - преобразует строку в целое число
            int id = Integer.parseInt(fields[0].trim());

            // Остальные поля - строковые
            String name = fields[1].trim();
            String status = fields[2].trim();
            String species = fields[3].trim();
            String type = fields[4].trim();
            String gender = fields[5].trim();
            String origin = fields[6].trim();
            String location = fields[7].trim();
            String created = fields[8].trim();

            // Преобразуем пустые строки в null для типа
            if (type.isEmpty()) {
                type = null;
            }

            // Создаем и возвращаем нового персонажа
            return new Character(id, name, status, species, type, gender, origin, location, created);

        } catch (NumberFormatException e) {
            // Ошибка при преобразовании ID в число
            System.err.println("Ошибка при парсинге строки " + lineNumber + ": " + line);
            return null;
        }
    }
}