package kaloba;

import java.math.BigDecimal;

public class WholesaleCustomer extends Customer {
    private static final BigDecimal CREDIT_LIMIT =
        new BigDecimal("2000.00");

    public WholesaleCustomer(long id, String name, String phone) {
        super(id, name, phone);
    }

    @Override
    public BigDecimal getCreditLimit() {
        return CREDIT_LIMIT;
    }
}
