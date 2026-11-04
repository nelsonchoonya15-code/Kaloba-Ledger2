package kaloba;

import java.math.BigDecimal;

public abstract class Customer {
    private final long id;
    private final String name;
    private final String phone;

    protected Customer(long id, String name, String phone) {
        if (id <= 0) {
            throw new IllegalArgumentException(
                "Customer ID must be positive."
            );
        }

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Customer name is required."
            );
        }

        this.id = id;
        this.name = name.trim();
        this.phone = phone == null ? "" : phone.trim();
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public abstract BigDecimal getCreditLimit();

    @Override
    public String toString() {
        return id + " - " + name + " (" + phone + ")";
    }
}
    
