package com.sakunov.labs.util;

import java.io.*;

// Утилита для работы с файлами
public class FileUtil {
    // Путь к директории resources
    private static final String RESOURCES_DIR = "lab-1/src/main/resources/";

    // Приватный конструктор
    private FileUtil() {
    }

    // Метод для проверки существования файла в директории
    // exists() - метод класса File, возвращает true если файл/директория существует
    public static boolean checkFileExists(String fileName) {
        return new File(RESOURCES_DIR + fileName).exists();
    }

    /* Метод для создания BufferedReader для чтения файла
       ->& Это класс для чтения текстовых файлов. Использует буфер (временную память)
           для уменьшения обращений к диску. FileReader читает по одному символу, тогда как
           BufferedReader читает блоками */
    public static BufferedReader createReader(String fileName) throws FileNotFoundException {
        return new BufferedReader(new FileReader(RESOURCES_DIR + fileName));
    }

    /* Метод для создания BufferedWriter для записи в файл
       ->& Аналогично. FileWriter записывает каждый символ сразу.
           BufferedWriter делает это блоками */
    public static BufferedWriter createWriter(String fileName) throws IOException {
        return new BufferedWriter(new FileWriter(RESOURCES_DIR + fileName));
    }
}