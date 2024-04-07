package com.ocado;

import com.ocado.basket.BasketSplitter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        BasketSplitter basketSplitter = new BasketSplitter("C:\\Staż\\Ocado\\Java\\Zadanie\\Zadanie\\config.json");
        Map<String, List<String>> delivery = basketSplitter.split(List.of("Wine - Port Late Bottled Vintage","Juice - Ocean Spray Cranberry"));
//        Map<String, List<String>> delivery = basketSplitter.split(List.of("Cookies Oatmeal Raisin", "English Muffin","Cheese Cloth","Cheese - St. Andre", "Sole - Dover, Whole, Fresh"));
        delivery.forEach((courier, items) -> System.out.println("Courier: " + courier + ", Items: " + items));
    }
}