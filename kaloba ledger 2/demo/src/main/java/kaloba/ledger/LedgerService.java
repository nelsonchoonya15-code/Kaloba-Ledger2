package kaloba.ledger;

import kaloba.CreditSale;
import kaloba.Customer;
import kaloba.Repayment;
import kaloba.WholesaleCustomer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class LedgerService {
    private final Ledger ledger;

    public LedgerService(Ledger ledger) {
        this.ledger = Objects.requireNonNull(ledger, "Ledger is required.");
    }

    public Customer registerRegularCustomer(String name, String phone) {
        Customer customer = new RegularCustomer(
            ledger.nextCustomerId(), name, phone
        );
        ledger.addCustomer(customer);
        return customer;
    }

    public Customer registerWholesaleCustomer(String name, String phone) {
        Customer customer = new WholesaleCustomer(
            ledger.nextCustomerId(), name, phone
        );
        ledger.addCustomer(customer);
        return customer;
    }

    public CreditSale recordSale(
            long customerId,
            BigDecimal amount,
            LocalDate date,
            String description) {
        Customer customer = requireCustomer(customerId);
        Objects.requireNonNull(amount, "Amount is required.");

        BigDecimal newBalance = getBalance(customerId).add(amount);
        if (newBalance.compareTo(customer.getCreditLimit()) > 0) {
            throw new CreditLimitExceededException(
                "Sale would exceed the credit limit of K"
                + customer.getCreditLimit() + "."
            );
        }

        CreditSale sale = new CreditSale(
            ledger.nextSaleId(), customerId, amount, date, description
        );
        ledger.addSale(sale);
        return sale;
    }

    public Repayment recordRepayment(
            long customerId,
            BigDecimal amount,
            LocalDate date) {
        requireCustomer(customerId);
        Objects.requireNonNull(amount, "Amount is required.");

        if (amount.compareTo(getBalance(customerId)) > 0) {
            throw new IllegalArgumentException(
                "Repayment cannot exceed the outstanding balance."
            );
        }

        Repayment repayment = new Repayment(
            ledger.nextRepaymentId(), customerId, amount, date
        );
        ledger.addRepayment(repayment);
        return repayment;
    }

    public BigDecimal getBalance(long customerId) {
        requireCustomer(customerId);
        BigDecimal totalSales = ledger.getSales().stream()
            .filter(sale -> sale.getCustomerId() == customerId)
            .map(CreditSale::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalRepayments = ledger.getRepayments().stream()
            .filter(repayment -> repayment.getCustomerId() == customerId)
            .map(Repayment::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        return totalSales.subtract(totalRepayments);
    }

    public List<Customer> getDebtors() {
        return ledger.getCustomers().stream()
            .filter(customer -> getBalance(customer.getId()).signum() > 0)
            .collect(Collectors.toList());
    }

    public List<CreditSale> getSales(long customerId) {
        requireCustomer(customerId);
        return ledger.getSales().stream()
            .filter(sale -> sale.getCustomerId() == customerId)
            .collect(Collectors.toList());
    }

    public List<Repayment> getRepayments(long customerId) {
        requireCustomer(customerId);
        return ledger.getRepayments().stream()
            .filter(repayment -> repayment.getCustomerId() == customerId)
            .collect(Collectors.toList());
    }

    private Customer requireCustomer(long customerId) {
        return ledger.findCustomer(customerId).orElseThrow(
            () -> new IllegalArgumentException(
                "Customer " + customerId + " is not registered."
            )
        );
    }
}
