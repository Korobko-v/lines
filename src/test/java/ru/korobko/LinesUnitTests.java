package ru.korobko;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit test for app.
 */
public class LinesUnitTests
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );
    }

    @Test
    public void divideIntoGroupsTest() {
        List<String> list = new ArrayList<>();
        list.add("\"111\";\"123\";\"222\"");
        list.add("\"200\";\"123\";\"100\"");
        list.add("\"300\";\"\";\"100\"");

        List<List<String>> groups = App.groupLinesFromFile(list, new ArrayList<>(), new ArrayList<>(), new HashMap<>());
        assertEquals(1, groups.size());

        list.add("\"144\";\"333\";\"500\"");
        List<List<String>> groups2 = App.groupLinesFromFile(list, new ArrayList<>(), new ArrayList<>(), new HashMap<>());
        assertEquals(1, groups2.size());

        list.add("\"244\";\"333\";\"800\"");
        List<List<String>> groups3 = App.groupLinesFromFile(list, new ArrayList<>(), new ArrayList<>(), new HashMap<>());

        assertEquals(2, groups3.size());

        assertFalse(groups3.get(0).contains("\"244\";\"333\";\"800\""));
        assertFalse(groups3.get(1).contains("\"111\";\"123\";\"222\""));

        assertTrue(groups3.get(0).contains("\"111\";\"123\";\"222\""));
        assertTrue(groups3.get(1).contains("\"244\";\"333\";\"800\""));

        list.add("255;100;1");
        list.add("256;100;0");

        List<List<String>> groups4 = App.groupLinesFromFile(list, new ArrayList<>(), new ArrayList<>(), new HashMap<>());
        groups4.forEach(group-> {
            System.out.println("GROUP: " + "\n");
            group.forEach(g -> System.out.println(g));
        });

        assertEquals(3, groups4.size());
    }

    @Test
    public void ignoreEmptyLinesTest() {
        List<String> list = new ArrayList<>();
        list.add("\"111\";\"123\";\"222\"");
        list.add("\"200\";\"123\";\"100\"");
        list.add("\"300\";\"\";\"100\"");
        //Совпадает с предыдущей строкой по пустому значению во второй колонке. По условию строка не должна попасть в группу
        list.add("\"10\";\"\";\"13\"");

        List<List<String>> groups = App.groupLinesFromFile(list, new ArrayList<>(), new ArrayList<>(), new HashMap<>());
        assertTrue(groups.get(0).contains("\"300\";\"\";\"100\""));
        assertFalse(groups.get(0).contains("\"10\";\"\";\"13\""));
    }
}
