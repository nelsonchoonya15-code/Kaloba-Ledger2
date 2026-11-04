package kaloba.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Ledger {

    private final Map<Integer, Customer> customers = new LinkedHashMap<>();
    private final List<CreditSale> sales = new ArrayList<>();
    private final List<Repayment> repayments = new ArrayList<>();

    public void addCustomer(Customer customer) {
        customers.put(customer.getId(), customer);
    }

    public Customer findCustomer(int id) {
        return customers.get(id);
    }

    public void removeCustomer(int id) {
        customers.remove(id);
        sales.removeIf(sale -> sale.getCustomerId() == id);
        repayments.removeIf(repayment -> repayment.getCustomerId() == id);
    }

    public List<Customer> getAllCustomers() {
        return new ArrayList<>(customers.values());
    }

    public void addSale(CreditSale sale) {
        sales.add(sale);
    }

    public void addRepayment(Repayment repayment) {
        repayments.add(repayment);
    }

    public double getTotalSales(int customerId) {
        double total = 0;
        for (CreditSale sale : sales) {
            if (sale.getCustomerId() == customerId) {
                total += sale.getAmount();
            }
        }
        return total;
    }

    public double getTotalRepayments(int customerId) {
        double total = 0;
        for (Repayment repayment : repayments) {
            if (repayment.getCustomerId() == customerId) {
                total += repayment.getAmount();
            }
        }
        return total;
    }
    public double getBalance(int customerId) {
        double balance = getTotalSales(customerId) - getTotalRepayments(customerId);
        return Math.round(balance * 100.0) / 100.0;
    }
}

