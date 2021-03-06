package ru.job4j.di;

import ru.job4j.tracker.ConsoleInput;

public class Main {
    public static void main(String[] args) {
        Context context = new Context();
        context.reg(Store.class);
        context.reg(ConsoleInput.class);
        context.reg(StartUI.class);

        StartUI ui = context.get(StartUI.class);
        ui.add("Text1");
        ui.add("Text2");
        ui.print();

        System.out.println(ui.askStr());
    }
}