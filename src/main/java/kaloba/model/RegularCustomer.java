package kaloba.model;

/** A normal shop customer. Small credit limit. */
public class RegularCustomer extends Customer {

    public static final double CREDIT_LIMIT = 500.00;

    public RegularCustomer(int id, String name) {
        super(id, name);
    }

    @Override
    public double getCreditLimit() {
        return CREDIT_LIMIT;
    }

    @Override
    public String getType() {
        return "REGULAR";
    }
}

