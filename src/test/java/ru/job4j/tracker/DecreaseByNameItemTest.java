package ru.job4j.tracker;

import org.junit.Test;

import java.util.List;
import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class DecreaseByNameItemTest {
    @Test
    public void whenDecreaseByName() {
        List<Item> items = Arrays.asList(
                new Item("Pavel"),
                new Item("Ivan"),
                new Item("Oleg")
        );
        Collections.sort(items, new DecreaseByNameItem());
        assertThat(new String[] {items.get(0).getName(), items.get(1).getName(), items.get(2).getName()},
                    is(new String[] {"Pavel", "Oleg", "Ivan"}));
    }
}

