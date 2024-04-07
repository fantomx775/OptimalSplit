package com.ocado;

import com.ocado.basket.BasketSplitter;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;


public class BasketSplitterTest {

    // Provide absolute path to the test for \json\test-config.json file
    private final String pathToConfigFile = "C:\\Staż\\Ocado\\Java\\Zadanie\\Zadanie\\OptSplt\\OptimalSplit\\src\\test\\java\\com\\ocado\\json\\test-config.json";
    @Test
    void testCheckIfAllItemsAreCovered() {
        BasketSplitter basketSplitter = new BasketSplitter(pathToConfigFile);

        Map<String, Set<String>> delivery = new HashMap<>();
        delivery.put("Courier1", new HashSet<>(Arrays.asList("Item1", "Item2")));
        delivery.put("Courier2", new HashSet<>(Arrays.asList("Item3", "Item4")));

        Set<String> courierSet = new HashSet<>(Arrays.asList("Courier1", "Courier2"));

        List<String> items = Arrays.asList("Item1", "Item2", "Item3", "Item4");

        try {
            Method method = BasketSplitter.class.getDeclaredMethod("checkIfAllItemsAreCovered", Map.class, Set.class, List.class);
            method.setAccessible(true);
            boolean result = (boolean) method.invoke(basketSplitter, delivery, courierSet, items);

            assertTrue(result);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testGenerateSubsets() {
        BasketSplitter basketSplitter = new BasketSplitter(pathToConfigFile);
        List<String> items = Arrays.asList("Item1", "Item2", "Item3", "Item4");

        List<HashSet<String>> subsets = basketSplitter.generateSubsets(items, 2);

        List<HashSet<String>> expectedSubsets = new ArrayList<>();
        expectedSubsets.add(new HashSet<>(Arrays.asList("Item1", "Item2")));
        expectedSubsets.add(new HashSet<>(Arrays.asList("Item1", "Item3")));
        expectedSubsets.add(new HashSet<>(Arrays.asList("Item1", "Item4")));
        expectedSubsets.add(new HashSet<>(Arrays.asList("Item2", "Item3")));
        expectedSubsets.add(new HashSet<>(Arrays.asList("Item2", "Item4")));
        expectedSubsets.add(new HashSet<>(Arrays.asList("Item3", "Item4")));

        assertTrue(subsets.containsAll(expectedSubsets) && expectedSubsets.containsAll(subsets));
    }

    @Test
    void testGenerateSubsetsComplex() {
        BasketSplitter basketSplitter = new BasketSplitter(pathToConfigFile);

        List<String> items = Arrays.asList("Item1", "Item2", "Item3", "Item4", "Item5");

        List<HashSet<String>> subsets = basketSplitter.generateSubsets(items, 3);

        List<HashSet<String>> expectedSubsets = new ArrayList<>();
        expectedSubsets.add(new HashSet<>(Arrays.asList("Item1", "Item2", "Item3")));
        expectedSubsets.add(new HashSet<>(Arrays.asList("Item1", "Item2", "Item4")));
        expectedSubsets.add(new HashSet<>(Arrays.asList("Item1", "Item2", "Item5")));
        expectedSubsets.add(new HashSet<>(Arrays.asList("Item1", "Item3", "Item4")));
        expectedSubsets.add(new HashSet<>(Arrays.asList("Item1", "Item3", "Item5")));
        expectedSubsets.add(new HashSet<>(Arrays.asList("Item1", "Item4", "Item5")));
        expectedSubsets.add(new HashSet<>(Arrays.asList("Item2", "Item3", "Item4")));
        expectedSubsets.add(new HashSet<>(Arrays.asList("Item2", "Item3", "Item5")));
        expectedSubsets.add(new HashSet<>(Arrays.asList("Item2", "Item4", "Item5")));
        expectedSubsets.add(new HashSet<>(Arrays.asList("Item3", "Item4", "Item5")));

        assertTrue(subsets.containsAll(expectedSubsets) && expectedSubsets.containsAll(subsets));
    }

    @Test
    void testCountMax1() {
        BasketSplitter basketSplitter = new BasketSplitter(pathToConfigFile);

        Map<String, List<String>> split = new HashMap<>();
        split.put("Courier1", Arrays.asList("Item1", "Item2", "Item3"));
        split.put("Courier2", Arrays.asList("Item4", "Item5"));
        split.put("Courier3", Arrays.asList("Item6", "Item7", "Item8", "Item9"));

        int max = basketSplitter.countMax(split);

        assertEquals(4, max);
    }

    @Test
    void testCountMax2() {
        BasketSplitter basketSplitter = new BasketSplitter(pathToConfigFile);

        Map<String, List<String>> split = new HashMap<>();
        split.put("Courier1", Arrays.asList("Item1", "Item2"));
        split.put("Courier2", Arrays.asList("Item3", "Item4", "Item5"));
        split.put("Courier3", Arrays.asList("Item6", "Item7", "Item8", "Item9", "Item10"));

        int max = basketSplitter.countMax(split);

        assertEquals(5, max);
    }

    @Test
    void testCountMaxMultipleMax() {
        BasketSplitter basketSplitter = new BasketSplitter(pathToConfigFile);

        Map<String, List<String>> split = new HashMap<>();
        split.put("Courier1", Arrays.asList("Item1", "Item2", "Item3"));
        split.put("Courier2", Arrays.asList("Item4", "Item5", "Item6"));
        split.put("Courier3", Arrays.asList("Item7", "Item8", "Item9"));

        int max = basketSplitter.countMax(split);

        assertEquals(3, max);
    }

    @Test
    void testSplitLocal() {
        // Provide absolute path to the test for \json\test-split-local.json file
        String pathToConfigFile = "C:\\Staż\\Ocado\\Java\\Zadanie\\Zadanie\\OptSplt\\OptimalSplit\\src\\test\\java\\com\\ocado\\json\\test-split-local.json";
        BasketSplitter basketSplitter = new BasketSplitter(pathToConfigFile);

        HashSet<String> courierSet = new HashSet<>(Arrays.asList("Courier1", "Courier2"));
        List<String> items = Arrays.asList("Item1", "Item2", "Item3", "Item4", "Item5");

        Map<String, List<String>> split = basketSplitter.splitLocal(courierSet, items);

        Map<String, List<String>> expectedSplit = new HashMap<>();
        expectedSplit.put("Courier1", Arrays.asList("Item1"));
        expectedSplit.put("Courier2", Arrays.asList("Item1", "Item3", "Item4", "Item5"));
        expectedSplit.forEach((key, value) -> Collections.sort(value));
        split.forEach((key, value) -> Collections.sort(value));

        assertEquals(expectedSplit.get("Courier1").get(0), split.get("Courier1").get(0));
        assertEquals(expectedSplit.get("Courier2").get(0), split.get("Courier2").get(0));
        assertEquals(expectedSplit.get("Courier2").get(1), split.get("Courier2").get(1));
        assertEquals(expectedSplit.get("Courier2").get(2), split.get("Courier2").get(2));
        assertEquals(expectedSplit.get("Courier2").get(3), split.get("Courier2").get(3));
    }

    @Test
    void testSplitMax() {
        // Provide absolute path to the test for \json\test-max-split.json file
        String pathToConfigFile = "C:\\Staż\\Ocado\\Java\\Zadanie\\Zadanie\\OptSplt\\OptimalSplit\\src\\test\\java\\com\\ocado\\json\\test-max-split.json";
        BasketSplitter basketSplitter = new BasketSplitter(pathToConfigFile);

        Map<String, List<String>> split = new HashMap<>();
        split.put("Courier1", Arrays.asList("Item1", "Item2", "Item3"));
        split.put("Courier2", Arrays.asList("Item3", "Item4"));

        split.put("Courier1", new LinkedList<>(Arrays.asList("Item1", "Item2", "Item3")));
        split.put("Courier2", new LinkedList<>(Arrays.asList("Item3", "Item4")));

        Map<String, List<String>> maxSplit = basketSplitter.splitMax(split);

        Map<String, List<String>> expectedMaxSplit = new HashMap<>();
        expectedMaxSplit.put("Courier1", Arrays.asList("Item1", "Item2", "Item3"));
        expectedMaxSplit.put("Courier2", Arrays.asList("Item4"));

        assertEquals(expectedMaxSplit, maxSplit);
    }


}
