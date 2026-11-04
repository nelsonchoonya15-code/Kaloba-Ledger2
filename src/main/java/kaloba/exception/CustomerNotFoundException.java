package kaloba.exception;

public class CustomerNotFoundException extends Exception {
    public CustomerNotFoundException(int customerId) {
        super("Customer not found: " + customerId);
    }
}
