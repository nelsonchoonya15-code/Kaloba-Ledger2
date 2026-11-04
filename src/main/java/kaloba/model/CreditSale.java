package kaloba.model;

public class CreditSale {
    private final int customerId;
    private final double amount;

    public CreditSale(int customerId, double amount) {
        this.customerId = customerId;
        this.amount = amount;
    }

    public int getCustomerId() {
        return customerId;
    }

    public double getAmount() {
        return amount;
    }
}