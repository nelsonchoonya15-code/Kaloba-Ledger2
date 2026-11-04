package kaloba.ledger;

public final class CreditLimitExceededException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public CreditLimitExceededException(String message) {
        super(message);
    }
}
