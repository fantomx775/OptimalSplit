package com.ocado.basket;

import java.io.IOException;
import java.util.*;
/**
 * A class for splitting basket items optimally among couriers.
 */
public class BasketSplitter {

    /**
     * Constructs a BasketSplitter object.
     *
     * @param absolutePathToConfigFile The absolute path to the configuration file.
     */
    private final Map<String, List<String>> config;
    public BasketSplitter(String absolutePathToConfigFile) {
        try {
            this.config = new ConfigParser().parse(absolutePathToConfigFile);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error reading config file: " + e.getMessage());
        }
    }
    /**
     * Checks if all items in the basket are covered by the given delivery configuration.
     *
     * @param delivery    The delivery configuration mapping courier names to the items they deliver.
     * @param courierSet  The set of couriers involved in the delivery.
     * @param items       The list of items in the basket.
     * @return True if all items are covered by the delivery, false otherwise.
     */
    protected boolean checkIfAllItemsAreCovered(Map<String, Set<String>> delivery, Set<String> courierSet, List<String> items) {
        Set<String> itemsCovered = new HashSet<>();
        for (String courier : courierSet) {
            itemsCovered.addAll(delivery.get(courier));
        }
        return itemsCovered.containsAll(items);
    }

    protected List<HashSet<String>> generateSubsets(List<String> items, int n) {
        List<HashSet<String>> subsets = new ArrayList<>();
        generateSubsetsHelper(items, n, 0, new HashSet<>(), subsets);
        return subsets;
    }

    /**
     * Generates all possible subsets of size n from the given list of items.
     *
     * @param items The list of items.
     * @param n     The size of subsets to generate.
     * @return A list of subsets.
     */
    private void generateSubsetsHelper(List<String> items, int n, int startIndex, HashSet<String> currentSubset, List<HashSet<String>> subsets) {
        if (currentSubset.size() == n) {
            subsets.add(new HashSet<>(currentSubset));
            return;
        }
        for (int i = startIndex; i < items.size(); i++) {
            currentSubset.add(items.get(i));
            generateSubsetsHelper(items, n, i + 1, currentSubset, subsets);
            currentSubset.remove(items.get(i));
        }
    }

    /**
     * Counts the maximum number of items in split (Map<String, List<String>> of couriers as keys and lists of items as values).
     *
     * @param split The split to analyze.
     * @return The maximum number of items in split.
     */
    protected int countMax(Map<String, List<String>> split) {
        int max = 0;
        for (Map.Entry<String, List<String>> entry : split.entrySet()) {
            max = Math.max(max, entry.getValue().size());
        }
        return max;
    }

    /**
     * Splits the items among couriers.
     *
     * @param courierSet The set of couriers.
     * @param items      The list of items to be split.
     * @return The split of items among couriers.
     */
    protected Map<String, List<String>> splitLocal(HashSet<String> courierSet, List<String> items) {
        Map<String, List<String>> split = new HashMap<>();
        for (String courier : courierSet) {
            split.put(courier, new ArrayList<>());
        }
        for (String item : items) {
            for (String courier : config.get(item)) {
                if (courierSet.contains(courier)) {
                    split.get(courier).add(item);
                }
            }
        }
        return split;
    }

    /**
     * Splits the items among couriers to maximize the number of items each courier gets, to meet the requirements.
     *
     * @param split The initial split of items among couriers.
     * @return The optimized split to maximize the number of items each courier gets.
     */
    protected Map<String, List<String>> splitMax(Map<String, List<String>> split){
        Map<String, List<String>> result = new HashMap<>();
        while (!split.isEmpty()) {
            int max = 0;
            String maxCourier = "";
            for (Map.Entry<String, List<String>> entry : split.entrySet()) {
                if (entry.getValue().size() > max) {
                    max = entry.getValue().size();
                    maxCourier = entry.getKey();
                }
            }
            List<String> items = split.get(maxCourier);
            result.put(maxCourier, items);
            split.remove(maxCourier);
            for (Map.Entry<String, List<String>> entry : split.entrySet()) {
                entry.getValue().removeAll(items);
            }
        }
        return result;
    }

    /**
     * Splits the items among couriers to optimize the distribution of items.
     *
     * @param items The list of items to be split.
     * @return The optimized split of items among couriers.
     */
    public Map<String, List<String>> split(List<String> items) {
        HashMap<String, Set<String>> delivery = new HashMap<>();
        for (String item : items) {
            List<String> courier_list = config.get(item);
            for (String courier : courier_list) {
                delivery.putIfAbsent(courier, new HashSet<>());
                delivery.get(courier).add(item);
            }
        }
        List<String> courierNames = new ArrayList<>(delivery.keySet());
        boolean found = false;
        int curr_max = 0;
        Map<String, List<String>> curr_split = new HashMap<>();
        for(int i = 1; i <= courierNames.size(); i++) {
            if(found) {
                break;
            }
            List<HashSet<String>> subsets = generateSubsets(courierNames, i);
            for (HashSet<String> courierSet : subsets) {
                if (checkIfAllItemsAreCovered(delivery, courierSet, items)) {
                    found = true;
                    Map<String, List<String>> new_split = splitLocal(courierSet, items);
                    int new_max = countMax(new_split);
                    if (new_max > curr_max) {
                        curr_max = new_max;
                        curr_split = new_split;
                    }
                }
            }
        }
        return splitMax(curr_split);
    }
}