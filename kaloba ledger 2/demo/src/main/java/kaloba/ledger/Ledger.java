package kaloba.ledger;

import kaloba.CreditSale;
import kaloba.Customer;
import kaloba.Repayment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class Ledger {
    private final Map<Long, Customer> customers = new LinkedHashMap<>();
    private final List<CreditSale> sales = new ArrayList<>();
    private final List<Repayment> repayments = new ArrayList<>();

    private long nextCustomerId = 1;
    private long nextSaleId = 1;
    private long nextRepaymentId = 1;

    long nextCustomerId() {
        return nextCustomerId++;
    }

    long nextSaleId() {
        return nextSaleId++;
    }

    long nextRepaymentId() {
        return nextRepaymentId++;
    }

    void addCustomer(Customer customer) {
        customers.put(customer.getId(), customer);
    }

    void addSale(CreditSale sale) {
        sales.add(sale);
    }

    void addRepayment(Repayment repayment) {
        repayments.add(repayment);
    }

    Optional<Customer> findCustomer(long customerId) {
        return Optional.ofNullable(customers.get(customerId));
    }

    List<Customer> getCustomers() {
        return Collections.unmodifiableList(
            new ArrayList<>(customers.values())
        );
    }

    List<CreditSale> getSales() {
        return Collections.unmodifiableList(new ArrayList<>(sales));
    }

    List<Repayment> getRepayments() {
        return Collections.unmodifiableList(new ArrayList<>(repayments));
    }
}
