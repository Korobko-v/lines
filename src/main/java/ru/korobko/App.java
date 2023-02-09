package ru.korobko;

import ru.korobko.model.PhoneNumber;
import ru.korobko.utils.FileUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class App {
    public static void main(String[] args) {
        List<String> lines = FileUtils.readFileWithSpark(args[0]);

        List<List<String>> groups = new ArrayList<>();

        List<Map<String, Integer>> numbersWithPositions = new ArrayList<>();

        Map<Integer, Integer> groupsToJoin = new HashMap<>();

        List<List<String>> finalGroups = groupLinesFromFile(lines, groups, numbersWithPositions, groupsToJoin);

        FileUtils.writeToFile("output.txt", finalGroups);
    }

    /**
     * Сгруппировать номера по заданному условию (см. README)
     * @param lines список строк из файла
     * @param groups список групп
     * @param numbersWithPositions телефонные номера с номерами позиций (колонок)
     * @param groupsToJoin номера пар групп для слияния
     *
     * @return список сгруппированных номеров
     */
    public static List<List<String>> groupLinesFromFile(List<String> lines,
                                          List<List<String>> groups,
                                          List<Map<String, Integer>> numbersWithPositions,
                                          Map<Integer, Integer> groupsToJoin) {

        lines.forEach(line -> {
            String[] lineNumbers = line.split(";");
            TreeSet<Integer> foundInGroups = new TreeSet<>();
            List<PhoneNumber> phoneNumbers = new ArrayList<>();
            for (int i = 0; i < lineNumbers.length; i++) {
                String number = lineNumbers[i];

                if (number.equals("\"\"")) continue;

                if (numbersWithPositions.size() == i) numbersWithPositions.add(new HashMap<>());

                Map<String, Integer> wordToGroupNumber = numbersWithPositions.get(i);
                Integer wordGroupNumber = wordToGroupNumber.get(number);
                if (wordGroupNumber != null) {
                    while (groupsToJoin.containsKey(wordGroupNumber))
                        wordGroupNumber = groupsToJoin.get(wordGroupNumber);
                    foundInGroups.add(wordGroupNumber);
                } else {
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
                if (mergeGroupNumber != groupNumber) {
                    groupsToJoin.put(mergeGroupNumber, groupNumber);
                    groups.get(groupNumber).addAll(groups.get(mergeGroupNumber));
                    groups.set(mergeGroupNumber, null);
                }
            });
            groups.get(groupNumber).add(line);
        });
        groups.removeAll(Collections.singletonList(null));

        return groups.stream().filter(g -> g.size() > 1).collect(Collectors.toList());
    }
}
