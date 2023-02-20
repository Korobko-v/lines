package ru.korobko;

import org.junit.Test;
import ru.korobko.utils.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Integration tests for app
 */
public class LinesIntegrationTests {

    @Test
    public void readFromFileAndWriteToNewFileTest() throws IOException {
        File input = new File("input.txt");
        try (FileWriter writer = new FileWriter(input)) {
            writer.write("\"111\";\"123\";\"222\""  + "\n");
            writer.write("\"200\";\"123\";\"100\"" + "\n");
            writer.write("\"300\";\"\";\"100\"" + "\n");
            writer.write("\"144\";\"333\";\"500\"" + "\n");
            //Дубликат не должен быть прочитан
            writer.write("\"144\";\"333\";\"500\"" + "\n");
            writer.write("\"244\";\"333\";\"800\"" + "\n");
            //Строка не соответствует заданному паттерну и не должна быть прочитана
            writer.write("\"244\";\"333\"\"800");
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<String> list = FileUtils.readFromFile(input.getName());
        assertEquals(5, list.size());
        //Проверка на успешную фильтрацию дубликатов
        assertEquals(1, list.stream().filter(s -> s.equals("\"144\";\"333\";\"500\"")).count());
        assertFalse(list.contains("\"244\";\"333\"\"800"));
        List<List<String>> groups = App.groupLinesFromFile(list, new ArrayList<>(), new ArrayList<>(), new HashMap<>());

        File output = new File("output.txt");
        FileUtils.writeToFile(output.getName(), groups);

        List<String> outputFileLines = Files.lines(Path.of(output.getName())).collect(Collectors.toList());
        //Проверка верного вывода количества групп
        assertTrue(outputFileLines.get(0).contains("Количество групп: " + 2));

        assertTrue(input.delete());
        assertTrue(output.delete());
    }

    @Test
    public void nonMatchingStringsTest() {
        File input = new File("input.txt");
        try (FileWriter writer = new FileWriter(input)) {

            //Строки, не соответствующие шаблону
            writer.write("\"11\"1\";\"123\";\"222\""  + "\n");
            writer.write("\"33\"\"\"123\";\"100\"" + "\n");
            writer.write("\"4.0.4\";\"123\";\"100\"" + "\n");


            //Строка с числом в кавычках и пустыми "колонками" без кавычек проходит
            writer.write("\"81917.1\";;" + "\n");
            writer.write("\"81917\";;" + "\n");
            writer.write("\"81917\";917;" + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<String> list = FileUtils.readFromFile(input.getName());
        assertEquals(3, list.size());

        //в список строк прошли только валидные строки
        assertTrue(list.contains("\"81917.1\";;"));
        assertTrue(list.contains("\"81917\";;"));
        assertTrue(list.contains("\"81917\";917;"));

        assertTrue(input.delete());
    }
}
