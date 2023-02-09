package ru.korobko;

import ru.korobko.model.PhoneNumber;
import ru.korobko.service.SparkUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class App 
{
    public static void main( String[] args )
    {
        groupLinesFromFile("lng.txt");
    }

    /**
     * Получить данные из файла и записать в новый файл
     * @param fileName имя файла для чтения данных
     */
    public static void groupLinesFromFile(String fileName) {

        List<String> lines = SparkUtils.readFileWithSpark(fileName);

        //Список групп
        List<List<String>> groups = new ArrayList<>();

        //Телефонные номера с позициями в строке
        List<Map<String, Integer>> numbersWithPositions = new ArrayList<>();

        //группы для слияния
        Map<Integer, Integer> groupsToJoin = new HashMap<>();

        lines.forEach(line -> {
            String[] lineNumbers = line.split(";");
            TreeSet<Integer> foundInGroups = new TreeSet<>();
            List<PhoneNumber> phoneNumbers = new ArrayList<>();
            for (int i = 0; i < lineNumbers.length; i++)
            {
                String number = lineNumbers[i];

                if (number.equals("\"\"")) continue;

                if (numbersWithPositions.size() == i) numbersWithPositions.add(new HashMap<>());

                Map<String, Integer> wordToGroupNumber = numbersWithPositions.get(i);
                Integer wordGroupNumber = wordToGroupNumber.get(number);
                if (wordGroupNumber != null)
                {
                    while (groupsToJoin.containsKey(wordGroupNumber))
                        wordGroupNumber = groupsToJoin.get(wordGroupNumber);
                        foundInGroups.add(wordGroupNumber);
                }
                else
                {
                    phoneNumbers.add(new PhoneNumber(number, i));
                }
            }
            int groupNumber;
            if (!foundInGroups.isEmpty()) {
                groupNumber = foundInGroups.first();
            } else {
                groupNumber = groups.size();
                groups.add(new ArrayList<>());
            }
            phoneNumbers.forEach(number -> numbersWithPositions
                    .get(number.getPosition()).put(number.getValue(), groupNumber));

            foundInGroups.forEach(mergeGroupNumber -> {
                if (mergeGroupNumber != groupNumber)
                {
                    groupsToJoin.put(mergeGroupNumber, groupNumber);
                    groups.get(groupNumber).addAll(groups.get(mergeGroupNumber));
                    groups.set(mergeGroupNumber, null);
                }
            } );
            groups.get(groupNumber).add(line);
        });
        groups.removeAll(Collections.singletonList(null));

        //Группы с более, чем одним элементом
        List<List<String>> finalGroups = groups.stream().filter(g -> g.size() > 1).collect(Collectors.toList());

        System.out.println("Количество групп: " + finalGroups.size());
        System.out.println("--------------------------------");
        //Вывод номера группы и входящих в неё строк
        AtomicInteger count = new AtomicInteger(1);
        finalGroups.parallelStream().sorted((o1, o2) -> o2.size() - o1.size()).forEachOrdered(group -> {
                    System.out.println("Группа " + count.getAndIncrement() + "\n");
                    group.forEach(gr -> {
                        System.out.println(gr + "\n");
                    });
                }
        );
    }



}
