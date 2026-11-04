package kaloba.ledger;

import java.math.BigDecimal;
import java.time.LocalDate;

import kaloba.CreditSale;
import kaloba.Customer;
import kaloba.Repayment;

public class Main {
    public static void main(String[] args) {
        Ledger ledger = new Ledger();
        LedgerService service = new LedgerService(ledger);

        LocalDate date = LocalDate.of(2026, 11, 1);

        Customer mary = service.registerRegularCustomer(
            "Mary Banda", "0970000001"
        );

        Customer john = service.registerWholesaleCustomer(
            "John Phiri", "0970000002"
        );

        System.out.println("Registered: " + mary);
        System.out.println("Registered: " + john);

        service.recordSale(
            mary.getId(),
            new BigDecimal("150.00"),
            date,
            "Groceries"
        );

        service.recordSale(
            mary.getId(),
            new BigDecimal("100.00"),
            date,
            "Cooking oil"
        );

        service.recordRepayment(
            mary.getId(),
            new BigDecimal("70.00"),
            date
        );

        System.out.println(
            "\nMary's balance: K"
            + service.getBalance(mary.getId())
        );

        try {
            service.recordSale(
                mary.getId(),
                new BigDecimal("400.00"),
                date,
                "Additional goods"
            );
        } catch (CreditLimitExceededException exception) {
            System.out.println(exception.getMessage());
        }

        System.out.println(
            "Balance after rejected sale: K"
            + service.getBalance(mary.getId())
        );

        System.out.println("\nCustomers who owe money:");

        for (Customer customer : service.getDebtors()) {
            System.out.println(
                customer.getName() + ": K"
                + service.getBalance(customer.getId())
            );
        }

        System.out.println("\nMary's credit sales:");

        for (CreditSale sale : service.getSales(mary.getId())) {
            System.out.println(
                sale.getDate() + " | "
                + sale.getDescription() + " | K"
                + sale.getAmount()
            );
        }

        System.out.println("\nMary's repayments:");

        for (Repayment repayment :
                service.getRepayments(mary.getId())) {

            System.out.println(
                repayment.getDate() + " | K"
                + repayment.getAmount()
            );
        }
    }
}