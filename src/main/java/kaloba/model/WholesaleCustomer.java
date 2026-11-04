package kaloba.model;

/** A customer who buys in bulk. Bigger credit limit than a regular customer. */
public class WholesaleCustomer extends Customer {

    public static final double CREDIT_LIMIT = 5000.00;

    public WholesaleCustomer(int id, String name) {
        super(id, name);
    }

    @Override
    public double getCreditLimit() {
        return CREDIT_LIMIT;
    }

    @Override
    public String getType() {
        return "WHOLESALE";
    }
}
