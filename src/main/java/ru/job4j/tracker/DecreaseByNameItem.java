package ru.job4j.tracker;

import java.util.Comparator;

public class DecreaseByNameItem implements Comparator<Item> {
    @Override
    public int compare(Item first, Item second) {
        return -1 * first.getName().compareTo(second.getName());
    }
}