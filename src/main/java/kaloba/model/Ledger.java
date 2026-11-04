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

    public void addSale(CreditSale sale) {
        sales.add(sale);
    }

    public void addRepayment(Repayment repayment) {
        repayments.add(repayment);
    }

    public List<Customer> getAllCustomers() {
        return List.copyOf(customers.values());
    }

    public double getBalance(int customerId) {
        double balance = 0;
        for (CreditSale sale : sales) {
            if (sale.getCustomerId() == customerId) {
                balance += sale.getAmount();
            }
        }
        for (Repayment repayment : repayments) {
            if (repayment.getCustomerId() == customerId) {
                balance -= repayment.getAmount();
            }
        }
        return balance;
    }

    public void removeCustomer(int customerId) {
        customers.remove(customerId);
        sales.removeIf(sale -> sale.getCustomerId() == customerId);
        repayments.removeIf(repayment -> repayment.getCustomerId() == customerId);
    }

    public Customer findCustomer(int customerId) {
        return customers.get(customerId);
    }
}