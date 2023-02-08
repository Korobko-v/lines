package ru.korobko;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class App 
{
    public static void main( String[] args )
    {
        groupLinesFromFile("lngtest.txt");
    }

    public static void groupLinesFromFile(String fileName) {
        File file = new File(fileName);
        List<String> lines = readLinesFromFile(file);

        //лист со всеми группами
        List<List<String>> groups = new ArrayList<>();
        for (int i = 0; i < lines.size() - 1; i++) {

            //при добавлении в группу значение строки в общем списке перетирается
            //если во внешний цикл попадает не пустая строка, значит в группах её нет
            if (!lines.get(i).isBlank()) {
                List<String> group = new ArrayList<>();
                group.add(lines.get(i));
                //внутренний цикл проходит по всем следующим строкам, проверяя на соответствие заданному условию
                //(совпадение значений в колонке)
                for (int j = i + 1; j < lines.size(); j++) {
                    if (lineIsInsideTheGroup(group, lines.get(j))) {
                        group.add(lines.get(j));
                        lines.set(j, "");
                        j = i;
                    }
                }
                if (group.size() > 1) {
                    groups.add(group);
                }
            }
        }
        //Вывод номера группы и входящих в неё строк
        AtomicInteger count = new AtomicInteger(1);
        groups.parallelStream().sorted((o1, o2) -> o2.size() - o1.size()).forEachOrdered(group -> {
                    System.out.println("Группа " + count.getAndIncrement() + "\n");
                    group.forEach(gr -> {
                        System.out.println(gr + "\n");
                    });
                }
        );
    }

    //метод проверяет, подходит ли строка для группы
    public static boolean lineIsInsideTheGroup(List<String> groups, String string2) {

        for (int i = 0; i < groups.size(); i++) {
            String string1 = groups.get(i);
            //сравниваем каждую строку группы с проверяемой строкой и проходим циклом по короткой во избежание IndexOutOfBoundsException
            String[] split1 = string1.split(";");
            String[] split2 = string2.split(";");
            String[] shortList = split1.length < split2.length ?
                    split1
                    : split2;
            String[] longList = split1.length >= split2.length ?
                    split1
                    : split2;
            for (int j = 0; j < shortList.length; j++) {
                if (!shortList[j].equals("\"\"") && shortList[j].equals(longList[j])) {
                    return true;
                }
            }
        }
        return false;
    }

    //получить список строк из файла
    public static List<String> readLinesFromFile(File file) {
        Set<String> set = new HashSet<>();

        try (Stream<String> lines = Files.lines(Paths.get(file.getName()))) {
            set = lines.collect(Collectors.toSet());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>(set);
    }
}
