package kaloba;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public final class CreditSale {
    private final long id;
    private final long customerId;
    private final BigDecimal amount;
    private final LocalDate date;
    private final String description;

    public CreditSale(
            long id,
            long customerId,
            BigDecimal amount,
            LocalDate date,
            String description) {

        if (id <= 0 || customerId <= 0) {
            throw new IllegalArgumentException(
                "Sale and customer IDs must be positive."
            );
        }

        Objects.requireNonNull(amount, "Amount is required.");

        if (amount.signum() <= 0) {
            throw new IllegalArgumentException(
                "Sale amount must be positive."
            );
        }

        this.id = id;
        this.customerId = customerId;
        this.amount = amount;
        this.date = Objects.requireNonNull(
            date, "Sale date is required."
        );
        this.description =
            description == null ? "" : description.trim();
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

    public String getDescription() {
        return description;
    }
}
