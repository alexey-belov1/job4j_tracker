package ru.job4j.tracker;

import org.junit.Test;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.*;

public class TrackerSQLTest {

    private List<String> toListString(List<Item> list) {
        return list.stream().map(Item::getName).collect(Collectors.toList());
    }

    public Connection init() {
        try (InputStream in = TrackerSQLTest.class.getClassLoader().getResourceAsStream("app.properties")) {
            Properties config = new Properties();
            config.load(in);
            Class.forName(config.getProperty("driver-class-name"));
            return DriverManager.getConnection(
                    config.getProperty("url"),
                    config.getProperty("username"),
                    config.getProperty("password")

            );
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Test
    public void checkConnection() throws SQLException {
        try (TrackerSQL sql = new TrackerSQL(ConnectionRollback.create(this.init()))) {
            assertThat(sql.available(), is(true));
        }
    }

    @Test
    public void whenAddAndFindByName() throws SQLException {
        try (TrackerSQL tracker = new TrackerSQL(ConnectionRollback.create(this.init()))) {
            String name = "test";
            tracker.add(new Item(name));
            assertThat(toListString(tracker.findByName(name)), is(List.of(name)));
        }
    }

    @Test
    public void whenAddAndFindById() throws SQLException {
        try (TrackerSQL tracker = new TrackerSQL(ConnectionRollback.create(this.init()))) {
            Item item = new Item("test");
            tracker.add(item);
            assertThat(tracker.findById(item.getId()).getName(), is(item.getName()));
        }
    }

    @Test
    public void whenFindAll() throws SQLException {
        try (TrackerSQL tracker = new TrackerSQL(ConnectionRollback.create(this.init()))) {
            String name1 = "test1";
            String name2 = "test2";
            tracker.add(new Item(name1));
            tracker.add(new Item(name2));
            assertThat(toListString(tracker.findAll()), is(List.of(name1, name2)));
        }
    }

    @Test
    public void whenHasSameName() throws SQLException {
        try (TrackerSQL tracker = new TrackerSQL(ConnectionRollback.create(this.init()))) {
            String name1 = "test1";
            String name2 = "test2";
            tracker.add(new Item(name1));
            tracker.add(new Item(name2));
            tracker.add(new Item(name1));
            assertThat(toListString(tracker.findByName(name1)), is(List.of(name1, name1)));
        }
    }

    @Test
    public void whenReplace() throws SQLException {
        try (TrackerSQL tracker = new TrackerSQL(ConnectionRollback.create(this.init()))) {
            Item item1 = new Item("test1");
            Item item2 = new Item("test2");
            tracker.add(item1);
            String id = item1.getId();
            tracker.replace(id, item2);
            assertThat(tracker.findById(id).getName(), is("test2"));
        }
    }

    @Test
    public void whenDelete() throws SQLException {
        try (TrackerSQL tracker = new TrackerSQL(ConnectionRollback.create(this.init()))) {
            Item item = new Item("test");
            tracker.add(item);
            tracker.delete(item.getId());
            assertThat(tracker.findById(item.getId()), is(nullValue()));
        }
    }
}