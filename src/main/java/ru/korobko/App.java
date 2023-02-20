package ru.korobko;

import ru.korobko.model.Word;
import ru.korobko.utils.FileUtils;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class App {
    public static void main(String[] args) {
        ZonedDateTime start = ZonedDateTime.now();
        System.out.println("Начало выполнения программы: " + start);
        List<String> lines = FileUtils.readFromFile(args[0]);

        List<List<String>> groups = new ArrayList<>();

        List<Map<String, Integer>> wordsWithPositions = new ArrayList<>();

        Map<Integer, Integer> groupsToJoin = new HashMap<>();

        List<List<String>> finalGroups = groupLinesFromFile(lines, groups, wordsWithPositions, groupsToJoin);

        FileUtils.writeToFile("output.txt", finalGroups);
        ZonedDateTime finish = ZonedDateTime.now();
        System.out.println("Конец выполнения программы: " + finish);
        System.out.println("Время выполнения программы: "
                + ((finish.toInstant().toEpochMilli() - start.toInstant().toEpochMilli()) / 1000) + " секунд");
    }

    /**
     * Сгруппировать номера по заданному условию (см. README)
     * @param lines список строк из файла
     * @param groups список групп
     * @param wordsWithPositions телефонные номера с номерами позиций (колонок)
     * @param groupsToJoin номера пар групп для слияния
     *
     * @return список сгруппированных номеров
     */
    public static List<List<String>> groupLinesFromFile(List<String> lines,
                                          List<List<String>> groups,
                                          List<Map<String, Integer>> wordsWithPositions,
                                          Map<Integer, Integer> groupsToJoin) {

        lines.forEach(line -> {
            String[] lineNumbers = line.split(";");
            TreeSet<Integer> foundInGroups = new TreeSet<>();
            List<Word> words = new ArrayList<>();
            for (int i = 0; i < lineNumbers.length; i++) {
                String word = lineNumbers[i];

                if (wordsWithPositions.size() == i) wordsWithPositions.add(new HashMap<>());
                if (word.replaceAll("\"", "").isBlank()) continue;
                Map<String, Integer> wordToGroupNumber = wordsWithPositions.get(i);
                Integer wordGroupNumber = wordToGroupNumber.get(word);
                if (wordGroupNumber != null) {
                    while (groupsToJoin.containsKey(wordGroupNumber))
                        wordGroupNumber = groupsToJoin.get(wordGroupNumber);
                    foundInGroups.add(wordGroupNumber);
                } else {
                    words.add(new Word(word, i));
                }
            }
            int groupNumber;
            if (!foundInGroups.isEmpty()) {
                groupNumber = foundInGroups.first();
            } else {
                groupNumber = groups.size();
                groups.add(new ArrayList<>());
            }
            words.forEach(word -> wordsWithPositions
                    .get(word.getPosition()).put(word.getValue(), groupNumber));

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
