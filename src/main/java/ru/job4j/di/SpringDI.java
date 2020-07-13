package ru.job4j.di;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SpringDI {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.scan("ru.job4j");
        context.refresh();

        StartUI ui = context.getBean(StartUI.class);
        ui.add("Text1");
        ui.add("Text2");
        ui.print();

        System.out.println(ui.askStr());

        StartUI another = context.getBean(StartUI.class);
        another.print();
    }
}