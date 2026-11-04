package kaloba;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public final class Repayment {
    private final long id;
    private final long customerId;
    private final BigDecimal amount;
    private final LocalDate date;

    public Repayment(
            long id,
            long customerId,
            BigDecimal amount,
            LocalDate date) {

        if (id <= 0 || customerId <= 0) {
            throw new IllegalArgumentException(
                "Repayment and customer IDs must be positive."
            );
        }

        Objects.requireNonNull(amount, "Amount is required.");

        if (amount.signum() <= 0) {
            throw new IllegalArgumentException(
                "Repayment amount must be positive."
            );
        }

        this.id = id;
        this.customerId = customerId;
        this.amount = amount;
        this.date = Objects.requireNonNull(
            date, "Repayment date is required."
        );
    }

    public long getId() {
        return id;
    }

    public long getCustomerId() {
        return customerId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDate getDate() {
        return date;
    }
}