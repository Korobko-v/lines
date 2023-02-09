package ru.korobko.service;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import java.util.List;

/**
 * Для чтения из файла с помощью Apache Spark
 */
public class SparkUtils {

    /**
     * Получить данные из файла
     * @param fileName имя файла
     * @return список строк из файла
     */
    public static List<String> readFileWithSpark(String fileName) {
        JavaSparkContext context = createSparkContext();
        JavaRDD<String> javaRDD = context.textFile(fileName);
        return javaRDD.distinct().collect();
    }

    /**
     * Создать контекст
     * @return JavaSparkContext
     */
    private static JavaSparkContext createSparkContext() {
        return new JavaSparkContext(new SparkConf().setMaster("local").setAppName("sparkApp"));
    }
}
