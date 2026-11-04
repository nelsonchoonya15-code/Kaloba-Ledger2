package kaloba.model;

import java.time.LocalDate;

public class CreditSale {
    private final int id;
    private final int customerId;
    private final String description;
    private final double amount;
    private final LocalDate date;

    public CreditSale(int customerId, double amount) {
        this(0, customerId, "", amount, LocalDate.now());
    }

    public CreditSale(int id, int customerId, String description, double amount, LocalDate date) {
        this.id = id;
        this.customerId = customerId;
        this.description = description;
        this.amount = amount;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public String getDescription() {
        return description;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDate getDate() {
        return date;
    }
}