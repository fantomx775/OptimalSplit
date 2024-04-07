# Delivery Grouping Library

## Description

The Delivery Grouping Library is designed to facilitate the grouping of items in a customer's shopping cart for delivery. It addresses the scenario where certain items cannot be delivered by standard delivery vehicles due to their size or because they are sourced from external suppliers. These items may require specialized courier services. Additionally, not all items can be delivered by couriers, especially perishable goods. Therefore, the library aims to optimize the grouping of items into delivery groups to minimize the number of required deliveries.

## Task

The task involves creating a library that divides items in a customer's shopping cart into delivery groups. An API has been predefined for the program. The library should load a configuration file containing the available delivery methods for all products offered in the store. Since this configuration is relatively static, it is stored in a file to be read by the library implementation.

## Table of Contents
- [Usage](#usage)
- [Configuration File](#configuration)
- [How it works](#how-it-works)

## Usage

To use library simply import this file to your project:
`OptimalSplit-1.0-SNAPSHOT.jar`
which you will find in `OptimalSplit\target` folder. You can download it from here: https://drive.google.com/file/d/1D6Jt52a3cmZ6jPlrJsD17CqbxioKPNTP/view?usp=sharing

Then you can use class
```javasript
import com.ocado.basket.BasketSplitter

BasketSplitter basketSplitter = new BasketSplitter(pathToConfigFile);
```

## Configuration

The configuration file should contain the available delivery methods for each product offered in the store. It follows a JSON format and should be structured as follows:

```json
{
  "Cookies Oatmeal Raisin": ["Pick-up point", "Parcel locker"],
  "Cheese Cloth": ["Courier", "Parcel locker", "Same day delivery", "Next day shipping", "Pick-up point"],
  "English Muffin": ["Mailbox delivery", "Courier", "In-store pick-up", "Parcel locker", "Next day shipping", "Express Collection"],
  "Ecolab - Medallion": ["Mailbox delivery", "Parcel locker", "Courier", "In-store pick-up"],
  ...
}
```

then pass absolute path to that json file when creating instance of `BasketSplitter` class
```javasript
BasketSplitter basketSplitter = new BasketSplitter(pathToConfigFile);
```
## How it works

The library will minimize number of distinict deliveries and within these deliveries maximize the number of products each delivery.

First we need to load the config file into atribute in the class

```javascript
    public BasketSplitter(String absolutePathToConfigFile) {
        try {
            this.config = new ConfigParser().parse(absolutePathToConfigFile);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error reading config file: " + e.getMessage());
        }
    }
```
For that we use helper class `ConfigParser`

The `ConfigParser` class reads a JSON configuration file specified by its absolute path. It uses Gson to parse the JSON content into a Java `Map<String, List<String>>`, where keys are strings and values are lists of strings. The file content is read character by character and assembled into a string using a `StringBuilder`. A `TypeToken` is employed to maintain generic type information during Gson deserialization. Finally, the parsed configuration map is returned to the caller.
```javascript
public class ConfigParser {
    public Map<String, List<String>> parse(String absolutePathToConfigFile) throws IOException {
        Gson gson = new Gson();

        FileReader reader = new FileReader(absolutePathToConfigFile);
        StringBuilder stringBuilder = new StringBuilder();
        int character;
        while ((character = reader.read()) != -1) {
            stringBuilder.append((char) character);
        }
        reader.close();

        TypeToken<Map<String, List<String>>> typeToken = new TypeToken<>() {};
        Map<String, List<String>> configMap = gson.fromJson(stringBuilder.toString(), typeToken.getType());

        return configMap;
    }
}
```
So first of all we need to minimize number of deliveries. This problem is similar to `Set Cover Problem` which is a NP-Hard problem. In order to find these optimal sets I am creating all possible subsets starting from subsets of size 1 and progressivly increasing the size looking for first set that will cover all items. Once we have that set, or rather its size, we have to ensure that it is the optimal set for loading products in demanded way, because we can come across a better set later on. Take a look at example:
```json
{
    "Item1": ["Courier1", "Courier2"],
    "Item2": ["Courier1", "Courier2"],
    "Item3": ["Courier1", "Courier3"],
    "Item4": ["Courier3"]
}
```
In this case the minimum number of different deliveries is obviously 2. But if the first optimal deliveries we discover are `Courier2` and `Courier3` and stop there, we will miss the optimal solution. As with `Courier2` and `Courier3` we would have the result like so:
```json
    "Courier2": ["Item1", "Item2"],
    "Courier3": ["Item3", "Item4"]
```
and it gives as a split 2 and 2, but the optimal split for our requirements is 3 and 1, like so:
```json
    "Courier1": ["Item1", "Item2", "Item3"],
    "Courier3": ["Item4"]
```
That is why when we find the first 'optimal' set, we still have to search all the sets of its size.

Here in the `split` we in the first `for loop` we look for all potential deliveries (couriers).
The second `for loop` generates all the sets mentioned above going from size 1.
The inner `for loop` checks if set satisfies the cover condition, and if yes it is looking for the most optimal set for required partitioning of the products among couriers.
Other methods used in this method are described in detail underneath.
```javascript
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
```
The `generateSubsets` method generates all possible subsets of a given size `n` from a list of items. It initializes an empty list called `subsets` to store the generated subsets. The method calls a helper function, `generateSubsetsHelper`, passing the list of items, the desired subset size `n`, a starting index, an empty set to represent the current subset, and the list of subsets. The `generateSubsetsHelper` method recursively constructs subsets of size `n` by iterating over the items starting from the given index. When a subset of size `n` is formed, it is added to the `subsets` list. The process continues until all possible subsets of size `n` are generated. Finally, the list of subsets is returned.
```javascript
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
            currentSubset.remove(items.get(i));
        }
    }
```
The `checkIfAllItemsAreCovered` method verifies if all items in a given list are covered by the delivery information for a set of couriers. It takes three parameters: a `Map<String, Set<String>>` representing delivery information, a `Set<String>` of couriers, and a `List<String>` of items to be checked. Within the method, a `HashSet` called `itemsCovered` is initialized to store all items covered by the couriers. It iterates over each courier in the provided set, retrieves the corresponding items from the delivery map, and adds them to the `itemsCovered` set. Finally, it checks if all items from the input list are present in the `itemsCovered` set and returns `true` if so, indicating that all items are covered; otherwise, it returns `false`.
```javascript
    private boolean checkIfAllItemsAreCovered(Map<String, Set<String>> delivery, Set<String> courierSet, List<String> items) {
        Set<String> itemsCovered = new HashSet<>();
        for (String courier : courierSet) {
            itemsCovered.addAll(delivery.get(courier));
        }
        return itemsCovered.containsAll(items);
    }
```
The `countMax` method calculates the maximum size of lists in a given `Map<String, List<String>>`. It initializes a variable `max` to store the maximum size found, initially set to zero. It iterates over each entry in the input map, which consists of a string key and a list of strings value. For each entry, it compares the size of the list with the current `max` value using `Math.max` and updates `max` accordingly. After iterating through all entries, it returns the final `max` value, representing the maximum size of lists among all entries in the map.
```javascript
    public int countMax(Map<String, List<String>> split) {
        int max = 0;
        for (Map.Entry<String, List<String>> entry : split.entrySet()) {
            max = Math.max(max, entry.getValue().size());
        }
        return max;
    }
```
The `splitLocal` method divides a set of items among a set of couriers based on a configuration map. It initializes a new `HashMap` called `split` to store the split information, where each courier is associated with a list of items. It iterates over each courier in the `courierSet` and initializes an empty list for each courier in the `split` map. Then, for each item in the input list of items, it retrieves the couriers associated with that item from a configuration map (`config`). If a courier associated with the item is present in the `courierSet`, the item is added to the corresponding courier's list in the `split` map. Finally, the method returns the resulting split map containing the items assigned to each courier.
```javascript
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
```
The `splitMax` method redistributes items from a split map to ensure that each courier in the result map receives the maximum number of items from the original split. It initializes a new `HashMap` called `result` to store the redistributed items. The method iterates over the original split map until it's empty. Within each iteration, it identifies the courier with the maximum number of items. It retrieves the items associated with this courier and adds them to the `result` map. Then, it removes this courier from the original split map and removes the items assigned to this courier from the lists of all other couriers in the split map. This process continues until all items have been redistributed, and the resulting map containing the maximum item allocation for each courier is returned.
```javascript
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
```





