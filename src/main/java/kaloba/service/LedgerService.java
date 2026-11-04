package kaloba.service;


import kaloba.exception.CreditLimitExceededException;
import kaloba.exception.CustomerNotFoundException;
import kaloba.model.CreditSale;
import kaloba.model.Customer;
import kaloba.model.Ledger;
import kaloba.model.Repayment;
import kaloba.storage.Database;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LedgerService {

    private final Ledger ledger;
    private final Database database;

    public LedgerService(Ledger ledger, Database database) {
        this.ledger = ledger;
        this.database = database;
        loadSavedData();
    }

    private void loadSavedData() {
        for (Customer customer : database.loadCustomers()) {
            ledger.addCustomer(customer);
        }
        for (CreditSale sale : database.loadSales()) {
            ledger.addSale(sale);
        }
        for (Repayment repayment : database.loadRepayments()) {
            ledger.addRepayment(repayment);
        }
    }

    public Customer registerCustomer(String name, String type) {
        Customer toSave = Customer.create(type, 0, name);   // checks the name and type (throws if bad)
        int newId = database.insertCustomer(toSave.getName(), toSave.getType());

        Customer saved = Customer.create(type, newId, name);   // same customer, now with its real id
        ledger.addCustomer(saved);
        return saved;
    }

    public List<Customer> getAllCustomers() {
        return ledger.getAllCustomers();
    }

    public List<Customer> getCustomersWhoOwe() {
        List<Customer> owing = new ArrayList<>();
        for (Customer customer : ledger.getAllCustomers()) {
            if (ledger.getBalance(customer.getId()) > 0) {
                owing.add(customer);
            }
        }
        return owing;
    }

    public Customer updateCustomer(int id, String name, String type) throws CustomerNotFoundException {
        findOrThrow(id);
        Customer updated = Customer.create(type, id, name);   // validates name and type
        database.updateCustomer(id, updated.getName(), updated.getType());
        ledger.addCustomer(updated);                           // replaces the old object
        return updated;
    }

    public void deleteCustomer(int id) throws CustomerNotFoundException {
        findOrThrow(id);
        double balance = ledger.getBalance(id);
        if (balance > 0) {
            throw new IllegalArgumentException(
                    "Cannot delete: this customer still owes " + money(balance));
        }
        database.deleteCustomer(id);
        ledger.removeCustomer(id);
    }

    public CreditSale recordSale(int customerId, String description, double amount)
            throws CustomerNotFoundException, CreditLimitExceededException {

        Customer customer = findOrThrow(customerId);
        if (amount <= 0) {
            throw new IllegalArgumentException("Sale amount must be more than zero");
        }

        double currentBalance = ledger.getBalance(customerId);
        double limit = customer.getCreditLimit();   // POLYMORPHISM: Regular and Wholesale give different answers

        if (currentBalance + amount > limit) {
            throw new CreditLimitExceededException(
                    "Credit limit exceeded: " + customer.getName() + " owes " + money(currentBalance)
                            + " and the limit is " + money(limit)
                            + ". A sale of " + money(amount) + " is not allowed.");
        }

        LocalDate today = LocalDate.now();
        int saleId = database.insertSale(customerId, description, amount, today);
        CreditSale sale = new CreditSale(saleId, customerId, description, amount, today);
        ledger.addSale(sale);
        return sale;
    }

    public Repayment recordRepayment(int customerId, double amount) throws CustomerNotFoundException {
        findOrThrow(customerId);
        if (amount <= 0) {
            throw new IllegalArgumentException("Repayment amount must be more than zero");
        }

        double currentBalance = ledger.getBalance(customerId);
        if (amount > currentBalance + 0.005) {
            throw new IllegalArgumentException(
                    "Repayment of " + money(amount) + " is more than the " + money(currentBalance) + " owed");
        }

        LocalDate today = LocalDate.now();
        int repaymentId = database.insertRepayment(customerId, amount, today);
        Repayment repayment = new Repayment(repaymentId, customerId, amount, today);
        ledger.addRepayment(repayment);
        return repayment;
    }

    public double getBalance(int customerId) {
        return ledger.getBalance(customerId);
    }

    private Customer findOrThrow(int id) throws CustomerNotFoundException {
        Customer customer = ledger.findCustomer(id);
        if (customer == null) {
            throw new CustomerNotFoundException(id);
        }
        return customer;
    }

    private String money(double amount) {
        return String.format(Locale.US, "K%.2f", amount);
    }
}