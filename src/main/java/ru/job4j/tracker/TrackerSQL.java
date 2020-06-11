package ru.job4j.tracker;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.io.InputStream;
import java.util.Properties;

public class TrackerSQL implements ITracker, AutoCloseable {
    private Connection connection;
    private static final Logger LOG = LogManager.getLogger(TrackerSQL.class.getName());

    public TrackerSQL() {
        try (InputStream in = TrackerSQL.class.getClassLoader().getResourceAsStream("app.properties")) {
            Properties config = new Properties();
            config.load(in);
            Class.forName(config.getProperty("driver-class-name"));
            this.connection = DriverManager.getConnection(
                    config.getProperty("url"),
                    config.getProperty("username"),
                    config.getProperty("password")
            );
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        try (Statement st = this.connection.createStatement()) {
            st.execute("create table if not exists item(id serial primary key, name varchar(200));");
            st.execute("delete from item;");
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public TrackerSQL(Connection connection) {
        this.connection = connection;
    }

    public boolean available() {
        return this.connection != null;
    }

    @Override
    public Item add(Item item) {
        try (PreparedStatement st = connection
                .prepareStatement("insert into item(name) values(?)", Statement.RETURN_GENERATED_KEYS)) {
            st.setString(1, item.getName());
            st.executeUpdate();
            try (ResultSet generatedKeys = st.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    item.setId(String.valueOf(generatedKeys.getInt(1)));
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

        return item;
    }

    @Override
    public boolean replace(String id, Item item) {
        boolean result = false;
        try (PreparedStatement st = connection
                .prepareStatement("update item set name = (?) where id = (?)")) {
            st.setString(1, item.getName());
            st.setInt(2, Integer.parseInt(id));
            if (st.executeUpdate() > 0) {
                result = true;
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

        return result;
    }

    @Override
    public boolean delete(String id) {
        boolean result = false;
        try (PreparedStatement st = connection
                .prepareStatement("delete from item where id = (?)")) {
            st.setInt(1, Integer.parseInt(id));
            if (st.executeUpdate() > 0) {
                result = true;
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

        return result;
    }

    @Override
    public List<Item> findAll() {
        List<Item> list = new ArrayList<Item>();
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery("select * from item")) {
            while (rs.next()) {
                Item item = new Item(rs.getString("name"));
                item.setId(rs.getString("id"));
                list.add(item);
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

        return list;
    }

    @Override
    public List<Item> findByName(String key) {
        List<Item> list = new ArrayList<Item>();
        try (PreparedStatement st = connection
                .prepareStatement("select * from item where name = (?)")) {
            st.setString(1, key);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    Item item = new Item(rs.getString("name"));
                    item.setId(rs.getString("id"));
                    list.add(item);
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

        return list;
    }

    @Override
    public Item findById(String id) {
        Item item = null;
        try (PreparedStatement st = connection
                .prepareStatement("select * from item where id = (?)")) {
            st.setInt(1, Integer.parseInt(id));
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    item = new Item(rs.getString("name"));
                    item.setId(id);
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

        return item;
    }

    @Override
    public void close() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }


    public static void main(String[] args) throws SQLException {
        Input input = new ConsoleInput();
        Input validate = new ValidateInput(input);
        try (TrackerSQL tracker = new TrackerSQL()) {
            ArrayList<UserAction> actions = new ArrayList<UserAction>();

            actions.add(new CreateAction());
            actions.add(new ShowAction());
            actions.add(new ReplaceAction());
            actions.add(new DeleteAction());
            actions.add(new FindByIdAction());
            actions.add(new FindByNameAction());
            actions.add(new ExitAction());

            new StartUI(validate, tracker, System.out::println).init(actions);
        }
    }
}
