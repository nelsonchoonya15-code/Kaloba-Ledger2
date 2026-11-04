package kaloba.model;

import java.time.LocalDate;

public class Repayment {
    private final int id;
    private final int customerId;
    private final double amount;
    private final LocalDate date;

    public Repayment(int id, int customerId, double amount, LocalDate date) {
        this.id = id;
        this.customerId = customerId;
        this.amount = amount;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDate getDate() {
        return date;
    }
}