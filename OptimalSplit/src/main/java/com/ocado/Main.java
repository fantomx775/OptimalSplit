package com.ocado;

import com.ocado.basket.BasketSplitter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        BasketSplitter basketSplitter = new BasketSplitter("C:\\Staż\\Ocado\\Java\\Zadanie\\Zadanie\\config.json");
        Map<String, List<String>> delivery = basketSplitter.split(List.of("Cookies Oatmeal Raisin","Cheese Cloth", "English Muffin", "Ecolab - Medallion", "Chocolate - Unsweetened", "Chocolate - Unsweetened", "Garlic - Peeled"));

//        Uncomment the following line to see the output in console
//        delivery.forEach((courier, items) -> System.out.println("\nCourier: " + courier + ",\nItems: " + items));
    }
}