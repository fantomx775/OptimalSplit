package com.ocado;

import com.ocado.basket.BasketSplitter;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Main {

    public static void main(String[] args) {

        ArrayList<String> test1 = new ArrayList<>(Arrays.asList(
                "Fond - Chocolate", "Chocolate - Unsweetened", "Nut - Almond, Blanched, Whole", "Haggis", "Mushroom - Porcini Frozen", "Cake - Miini Cheesecake Cherry", "Sauce - Mint", "Longan", "Bag Clear 10 Lb", "Nantucket - Pomegranate Pear", "Puree - Strawberry", "Numi - Assorted Teas", "Apples - Spartan", "Garlic - Peeled", "Cabbage - Nappa", "Bagel - Whole White Sesame", "Tea - Apple Green Tea"

        ));
//        Provide absolute path to the test for \json\config.json file
        String pathToConfigFile = "C:\\Staż\\Ocado\\Java\\Zadanie\\Zadanie\\config.json";
        BasketSplitter basketSplitter = new BasketSplitter(pathToConfigFile);
        Map<String, List<String>> delivery = basketSplitter.split(test1);

//        Uncomment the following line to see the output in console
        delivery.forEach((courier, items) -> System.out.println("\nCourier: " + courier + ",\nItems: " + items));
    }
}