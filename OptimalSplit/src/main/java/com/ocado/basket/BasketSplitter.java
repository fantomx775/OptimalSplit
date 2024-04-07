package com.ocado.basket;

import java.io.IOException;
import java.util.*;

public class BasketSplitter {

    private final Map<String, List<String>> config;
    public BasketSplitter(String absolutePathToConfigFile) {
        try {
            this.config = new ConfigParser().parse(absolutePathToConfigFile);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error reading config file: " + e.getMessage());
        }
    }

    public static List<List<Integer>> generatePermutations(int n, int size) {
        List<List<Integer>> permutations = new ArrayList<>();
        generatePermutationsHelper(n, size, new ArrayList<>(), permutations);
        return permutations;
    }

    private static void generatePermutationsHelper(int n, int size, List<Integer> current, List<List<Integer>> permutations) {
        if (n == 0 && current.size() == size) {
            permutations.add(new ArrayList<>(current));
            return;
        }

        if (current.size() >= size) {
            return;
        }

        current.add(0);
        generatePermutationsHelper(n, size, current, permutations);
        current.remove(current.size() - 1);

        if (n > 0) {
            current.add(1);
            generatePermutationsHelper(n - 1, size, current, permutations);
            current.remove(current.size() - 1);
        }
    }
    public static void displayHashMap(Map<String, Set<String>> map) {
        for (Map.Entry<String, Set<String>> entry : map.entrySet()) {
            String key = entry.getKey();
            Set<String> values = entry.getValue();
            System.out.println("Key: " + key + ", Values: " + values);
        }
    }
    private boolean checkIfAllItemsAreCovered(Map<String, Set<String>> delivery, Set<String> courierSet, List<String> items) {
        Set<String> itemsCovered = new HashSet<>();
        for (String courier : courierSet) {
            itemsCovered.addAll(delivery.get(courier));
        }
        return itemsCovered.containsAll(items);
    }

    public void displayItems(List<String> items) {
        for (String item : items) {
            System.out.println("ITEM " + item);
            config.get(item).forEach(System.out::println);
            System.out.println();
        }
    }

    public List<HashSet<String>> generateSubsets(List<String> items, int n) {
        List<HashSet<String>> subsets = new ArrayList<>();
        generateSubsetsHelper(items, n, 0, new HashSet<>(), subsets);
        return subsets;
    }

    private void generateSubsetsHelper(List<String> items, int n, int startIndex, HashSet<String> currentSubset, List<HashSet<String>> subsets) {
        if (currentSubset.size() == n) {
            subsets.add(new HashSet<>(currentSubset));
            return;
        }
        for (int i = startIndex; i < items.size(); i++) {
            currentSubset.add(items.get(i));
            generateSubsetsHelper(items, n, i + 1, currentSubset, subsets);
            currentSubset.remove(items.get(i)); // Remove the last added item to backtrack
        }
    }

    private void generateSubsetsHelper(List<String> items, int n, int startIndex, List<String> currentSubset, List<List<String>> subsets) {
        if (currentSubset.size() == n) {
            subsets.add(new ArrayList<>(currentSubset));
            return;
        }
        for (int i = startIndex; i < items.size(); i++) {
            currentSubset.add(items.get(i));
            generateSubsetsHelper(items, n, i + 1, currentSubset, subsets);
            currentSubset.remove(currentSubset.size() - 1);
        }
    }

    public int countMax(Map<String, List<String>> split) {
        int max = 0;
        for (Map.Entry<String, List<String>> entry : split.entrySet()) {
            max = Math.max(max, entry.getValue().size());
        }
        return max;
    }

    public Map<String, List<String>> splitLocal(HashSet<String> courierSet, List<String> items) {
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

    public Map<String, List<String>> splitMax(Map<String, List<String>> split){
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
    //TODO: CHECK EDGE CASES - EACH COURIER HAS ONLY ONE ITEM
    public Map<String, List<String>> split(List<String> items) {
        displayItems(items);

        HashMap<String, Set<String>> delivery = new HashMap<>();
        for (String item : items) {
            List<String> courier_list = config.get(item);
            for (String courier : courier_list) {
                delivery.putIfAbsent(courier, new HashSet<>());
                delivery.get(courier).add(item);
            }
        }
        List<String> courierNames = new ArrayList<>(delivery.keySet());
//        System.out.println(courierNames);
        boolean found = false;
        int curr_max = 0;
        Map<String, List<String>> curr_split = new HashMap<>();
        for(int i = 1; i < courierNames.size(); i++) {
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
//            List<List<Integer>> permutations = generatePermutations(i, courierNames.size());
//            for (List<Integer> permutation : permutations) {
//                Set<String> courierSet = new HashSet<>();
//                for (int j = 0; j < permutation.size(); j++) {
//                    if (permutation.get(j) == 1) {
//                        courierSet.add(courierNames.get(j));
//                    }
//                }
//                if (checkIfAllItemsAreCovered(delivery, courierSet, items)) {
//                    found = true;
//                    courierSet.forEach(System.out::println);
//                    return config;
//
//                }
//            }
        }
        return splitMax(curr_split);
    }

    public String getConfig() {
        return config.toString();
    }
}