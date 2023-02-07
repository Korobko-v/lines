package ru.korobko;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class App 
{
    public static void main( String[] args )
    {
        groupLinesFromFile("lng.txt");
    }

    public static void groupLinesFromFile(String fileName) {
        File file = new File(fileName);
        List<String> lines = readLinesFromFile(file);

        //лист со всеми группами
        List<List<String>> groups = new ArrayList<>();
        for (int i = 0; i < lines.size() - 1; i++) {

            //при добавлении в группу значение строки в общем списке перетирается
            //если во внешний цикл попадает не пустая строка, значит в группах её нет
            if (!lines.get(i).isEmpty()) {
                List<String> group = new ArrayList<>();
                group.add(lines.get(i));
                //внутренний цикл проходит по всем следующим строкам, проверяя на соответствие заданному условию
                //(совпадение значений в колонке)
                for (int j = i + 1; j < lines.size(); j++) {
                    if (lineIsInsideTheGroup(group, lines.get(j))) {
                        group.add(lines.get(j));
                        lines.set(j, "");
                    }
                }

                if (group.size() > 1) {
                    groups.add(group);
                }
            }
        }
        //Вывод номера группы и входящих в неё строк
        AtomicInteger count = new AtomicInteger(1);
        groups.stream().sorted((o1, o2) -> o2.size() - o1.size()).forEach(group -> {
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
            String[] shortList = string1.split(";").length < string2.split(";").length ?
                    string1.split(";")
                    : string2.split(";");
            String[] longList = string2.split(";").length > string1.split(";").length ?
                    string2.split(";")
                    : string1.split(";");
            for (int j = 0; j < shortList.length; j++) {
                if (shortList[j].equals(longList[j])) {
                    return true;
                }
            }
        }
        return false;
    }

    //получить список строк из файла
    public static List<String> readLinesFromFile(File file) {
        Set<String> lines = new HashSet<>();
        try(BufferedReader reader = new BufferedReader(new FileReader(file))) {
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
