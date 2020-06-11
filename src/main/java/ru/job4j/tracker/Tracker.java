package ru.job4j.tracker;

import java.util.ArrayList;
import java.util.Random;
import java.util.Arrays;

/**
 * @version $Id$
 * @since 0.1
 */
public class Tracker implements ITracker {
    /**
     * Массив для хранение заявок.
     */
    private final ArrayList<Item> items = new ArrayList<Item>();

    /**
     * Метод реализующий добавление заявки в хранилище
     * @param item новая заявка
     */
    public Item add(Item item) {
        item.setId(this.generateId());
        items.add(item);
        return item;
    }

    /**
     * Метод генерирует уникальный ключ для заявки.
     * Так как у заявки нет уникальности полей, имени и описание. Для идентификации нам нужен уникальный ключ.
     * @return Уникальный ключ.
     */
    private String generateId() {
        Random rnd = new Random();
        return String.valueOf(System.currentTimeMillis() + rnd.nextLong());
    }

    /**
     * Метод возвращает список всех заявок
     * @return Все заявки
     */
    public ArrayList<Item> findAll() {
        return items;
    }

    /**
     * Метод возвращает все заявки с указанным именем
     * @param key - Имя в заявке
     * @return Все заявки с указанным именем
     */
    public ArrayList<Item> findByName(String key) {
        ArrayList<Item> itemsIncludeKey = new ArrayList<Item>();
        for (Item item : items) {
            if (item.getName().equals(key)) {
                itemsIncludeKey.add(item);
            }
        }
        return itemsIncludeKey;
    }

    /**
     * Метод возвращает заявку с указанным ключом
     * @param id - уникальный ключ
     * @return Заявка с указанным ключом
     */
    public Item findById(String id) {
        return (indexOf(id) != -1) ? (items.get(indexOf(id))) : (null);
    }

    /**
     * Метод возвращает индекс в массиве с заявками по указанному ключу
     * @param id - уникальный ключ
     * @return Индекс в массиве по указанному ключу
     */
    private int indexOf(String id) {
        int rsl = -1;
        for (Item item : items) {
            if (item.getId().equals(id)) {
                rsl = items.indexOf(item);
                break;
            }
        }
        return rsl;
    }

    /**
     * Метод заменяет заявку с указанным уникальный номером на новую. Уникальный ключ не меняется
     * @param id - уникальный ключ заявки, которую необходимо заменить
     * @param item - новая заявка
     * @return true - если заявка успешно заменена, false - в противном случае
     */
    public boolean replace(String id, Item item) {
        boolean rsl = false;
        int index = indexOf(id);
        if (index != -1) {
            items.set(index, item);
            item.setId(id);
            rsl = true;
        }
        return rsl;
    }

    /**
     * Метод удаляет заявку по уникальному ключу
     * @param id - уникальный ключ заявки, которую необходимо удалить
     * @return true - если заявка успешно удалена, false - в противном случае
     */
    public boolean delete(String id) {
        boolean rsl = false;
        int index = indexOf(id);
        if (index != -1) {
            items.remove(index);
            rsl = true;
        }
        return rsl;
    }
}