package kaloba.storage;

import kaloba.model.CreditSale;
import kaloba.model.Customer;
import kaloba.model.Repayment;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * SAVING DATA: all the SQL lives here and nowhere else.
 * The data is stored in a SQLite file (kaloba.db), so it survives a restart.
 * This class only saves and loads. It knows nothing about credit limits.
 */
public class Database {

    private final String url;

    public Database(String fileName) {
        this.url = "jdbc:sqlite:" + fileName;   // SQLite creates the file if it is missing
        createTables();
    }

    private Connection connect() throws SQLException {
        Connection connection = DriverManager.getConnection(url);
        try (Statement statement = connection.createStatement()) {
            // SQLite ignores foreign keys unless we switch them on for each connection
            statement.execute("PRAGMA foreign_keys = ON");
        }
        return connection;
    }

    private void createTables() {
        try (Connection connection = connect(); Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS customers ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "name TEXT NOT NULL, "
                    + "type TEXT NOT NULL)");

            statement.execute("CREATE TABLE IF NOT EXISTS credit_sales ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "customer_id INTEGER NOT NULL, "
                    + "description TEXT NOT NULL, "
                    + "amount REAL NOT NULL, "
                    + "sale_date TEXT NOT NULL, "
                    + "FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE)");

            statement.execute("CREATE TABLE IF NOT EXISTS repayments ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "customer_id INTEGER NOT NULL, "
                    + "amount REAL NOT NULL, "
                    + "repayment_date TEXT NOT NULL, "
                    + "FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE)");
        } catch (SQLException e) {
            throw new RuntimeException("Could not create tables: " + e.getMessage(), e);
        }
    }

    // ---------------- saving ----------------

    /** Saves a new customer and returns the id SQLite gave it. */
    public int insertCustomer(String name, String type) {
        return insert("INSERT INTO customers (name, type) VALUES (?, ?)", name, type);
    }

    public void updateCustomer(int id, String name, String type) {
        execute("UPDATE customers SET name = ?, type = ? WHERE id = ?", name, type, id);
    }

    /** Deletes the customer. Their sales and repayments are deleted too (ON DELETE CASCADE). */
    public void deleteCustomer(int id) {
        execute("DELETE FROM customers WHERE id = ?", id);
    }

    public int insertSale(int customerId, String description, double amount, LocalDate date) {
        return insert("INSERT INTO credit_sales (customer_id, description, amount, sale_date) VALUES (?, ?, ?, ?)",
                customerId, description, amount, date.toString());
    }

    public int insertRepayment(int customerId, double amount, LocalDate date) {
        return insert("INSERT INTO repayments (customer_id, amount, repayment_date) VALUES (?, ?, ?)",
                customerId, amount, date.toString());
    }

    // ---------------- loading (used once, when the app starts) ----------------

    public List<Customer> loadCustomers() {
        List<Customer> customers = new ArrayList<>();
        try (Connection connection = connect();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery("SELECT id, name, type FROM customers ORDER BY id")) {
            while (rs.next()) {
                customers.add(Customer.create(rs.getString("type"), rs.getInt("id"), rs.getString("name")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not load customers: " + e.getMessage(), e);
        }
        return customers;
    }

    public List<CreditSale> loadSales() {
        List<CreditSale> sales = new ArrayList<>();
        try (Connection connection = connect();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(
                     "SELECT id, customer_id, description, amount, sale_date FROM credit_sales ORDER BY id")) {
            while (rs.next()) {
                sales.add(new CreditSale(rs.getInt("id"), rs.getInt("customer_id"),
                        rs.getString("description"), rs.getDouble("amount"),
                        LocalDate.parse(rs.getString("sale_date"))));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not load sales: " + e.getMessage(), e);
        }
        return sales;
    }

    public List<Repayment> loadRepayments() {
        List<Repayment> repayments = new ArrayList<>();
        try (Connection connection = connect();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(
                     "SELECT id, customer_id, amount, repayment_date FROM repayments ORDER BY id")) {
            while (rs.next()) {
                repayments.add(new Repayment(rs.getInt("id"), rs.getInt("customer_id"),
                        rs.getDouble("amount"), LocalDate.parse(rs.getString("repayment_date"))));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not load repayments: " + e.getMessage(), e);
        }
        return repayments;
    }

    // ---------------- small helpers ----------------

    /**
     * Runs an INSERT and returns the new row's id.
     * The ? marks are filled with the values, which keeps the SQL safe from SQL injection.
     */
    private int insert(String sql, Object... values) {
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            fill(statement, values);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                keys.next();
                return keys.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not save: " + e.getMessage(), e);
        }
    }

    /** Runs an UPDATE or DELETE. */
    private void execute(String sql, Object... values) {
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            fill(statement, values);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Could not save: " + e.getMessage(), e);
        }
    }

    private void fill(PreparedStatement statement, Object[] values) throws SQLException {
        for (int i = 0; i < values.length; i++) {
            statement.setObject(i + 1, values[i]);   // ? number 1 is the first value
        }
    }
}
