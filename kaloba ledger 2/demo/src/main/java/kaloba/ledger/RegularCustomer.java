package kaloba.ledger;

import java.math.BigDecimal;

import kaloba.Customer;

public class RegularCustomer extends Customer {
    private static final BigDecimal CREDIT_LIMIT =
        new BigDecimal("500.00");

    public RegularCustomer(long id, String name, String phone) {
        super(id, name, phone);
    }

    @Override
    public BigDecimal getCreditLimit() {
        return CREDIT_LIMIT;
    }
}