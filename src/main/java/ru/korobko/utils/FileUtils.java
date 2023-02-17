package ru.korobko.utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Для чтения из файла и записи в файл
 */
public class FileUtils {

    //public static String pattern = "(^[^\";]*(;\"[^\";]\")*$)|(^\"[^\";]*\"(;\"[^\";]*\")*$)";
    public static String pattern = "(\\d+(\\.\\d+)?|\\d*)*(;(\\d+(\\.\\d+)?|\\d*))*";
    public static String pattern2 = "\"(\\d+(\\.\\d+)?|\\d*)\"(;\"(\\d+(\\.\\d+)?|\\d*)\")*";

    /**
     * Получить данные из файла
     *
     * @param fileName имя файла
     * @return список строк из файла
     */
    public static List<String> readFromFile(String fileName) {
        try {
            return Files.lines(Path.of(fileName))
                    .distinct().filter(line ->
                            line.matches(pattern) ||
                            line.matches(pattern2))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("File doesn't exist");
        }
    }

    /**
     * Запись в файл
     * @param fileName имя файла
     * @param finalGroups список групп для записи
     * @return "SUCCESS", если файл записан удачно, иначе ошибка
     */
    public static String writeToFile(String fileName, List<List<String>> finalGroups) {
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write("Количество групп: " + finalGroups.size() + "\n");
            writer.write("--------------------------------" + "\n");
            AtomicInteger count = new AtomicInteger(1);
            finalGroups.parallelStream().sorted((o1, o2) -> o2.size() - o1.size()).forEachOrdered(group -> {
                try {
                    writer.write("Группа " + count.getAndIncrement() + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                group.forEach(gr -> {
                    try {
                        writer.write(gr + "\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                    }
            );
        } catch (IOException e) {
            return "Failed to upload groups";
        }
        return "SUCCESS";
    }
}


