package ru.korobko.utils;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Для чтения из файла и записи в файл
 */
public class FileUtils {

    /**
     * Получить данные из файла
     *
     * @param fileName имя файла
     * @return список строк из файла
     */
    public static List<String> readFileWithSpark(String fileName) {
        JavaSparkContext context = createSparkContext();
        JavaRDD<String> javaRDD = context.textFile(fileName);
        return javaRDD.distinct().filter(line -> line.matches("(\"\\d*\")?(;\"\\d*\")+")).collect();
    }

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


    /**
     * Создать контекст
     *
     * @return JavaSparkContext
     */
    private static JavaSparkContext createSparkContext() {
        return new JavaSparkContext(new SparkConf().setMaster("local").setAppName("App"));
    }

    /**
     * Получить данные из файла без использования Spark
     *
     * @param file файл с данными
     * @return список строк из файла
     */
    public static List<String> readLinesFromFile(File file) {
        Set<String> lines = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            while (reader.ready()) {
                String line = reader.readLine();
                lines.add(line);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Файл отсутствует или не указан");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>(lines);
    }
}
