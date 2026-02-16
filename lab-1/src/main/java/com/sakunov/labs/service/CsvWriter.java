package com.sakunov.labs.service;

import com.sakunov.labs.model.Character;
import com.sakunov.labs.util.FileUtil;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.List;

// Сервис для записи данных в CSV файлы
public class CsvWriter {
    // Метод записывает результаты группировки по gender в файл
    public static void writeGroupedByGender(Hashtable<String, List<String>> groupedData, String fileName)
            throws IOException {
        // Аналогично: try-with-resources - автоматически закрывает BufferedWriter
        try (BufferedWriter writer = FileUtil.createWriter(fileName)) {
            writer.write("gender,count,names"); // Записываем заголовок
            writer.newLine();                       // Переход на новую строку

            // Перебираем все ключи (gender) в Hashtable
            // groupedData.keySet() возвращает множество всех ключей
            // String.join(delimiter, iterable) - соединяет элементы через разделитель
            for (String gender : groupedData.keySet()) {
                List<String> names = groupedData.get(gender);           // Получаем список имен для текущего пола
                String namesString = String.join("; ", names);  // Объединяем имена через точку с запятой

                // Записываем строку с данными
                // Используем кавычки для поля names, так как там могут быть запятые
                // String.format() создает строку по шаблону
                writer.write(String.format("%s,%d,\"%s\"",
                        gender,         // Пол
                        names.size(),   // Количество (count)
                        namesString));  // Список имен в кавычках
                writer.newLine();
            }
        }
    }

    // Метод для записи всех персонажей в файл
    public static void writeAllCharacters(List<Character> characters, String fileName) throws IOException {
        // Также используем try-with-resources
        try (BufferedWriter writer = FileUtil.createWriter(fileName)) {
            // Записываем заголовок (те же, что и в исходном файле)
            writer.write("id,name,status,species,type,gender,origin/name,location/name,created");
            writer.newLine();

            /* Записываем каждого персонажа
               Получаем CSV строки из объекта
               Character.toCsvString() форматирует поля в правильном порядке
               и обрабатывает null значения (превращает в пустые строки) */
            for (Character character : characters) {
                writer.write(character.toCsvString());
                writer.newLine();
            }
        }
    }
}