package kaloba.model;

/**
 * Parent class for every customer of the shop.
 *
 * ENCAPSULATION: the fields are private and final; other classes can only
 *                read them through the getters.
 * INHERITANCE:   RegularCustomer and WholesaleCustomer extend this class.
 * POLYMORPHISM:  getCreditLimit() is abstract, so each child class answers
 *                differently when we call customer.getCreditLimit().
 */
public abstract class Customer {

    private final int id;
    private final String name;

    protected Customer(int id, String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Customer name cannot be empty");
        }
        this.id = id;
        this.name = name.trim();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    /** The most this customer may owe at one time (in Kwacha). */
    public abstract double getCreditLimit();

    /** "REGULAR" or "WHOLESALE" (this text is what we save in the database). */
    public abstract String getType();

    /**
     * Builds the right kind of customer from the type text.
     * Used when the page registers a customer and when we load from the database.
     */
    public static Customer create(String type, int id, String name) {
        if (type == null) {
            throw new IllegalArgumentException("Customer type must be REGULAR or WHOLESALE");
        }
        switch (type.trim().toUpperCase()) {
            case "REGULAR":
                return new RegularCustomer(id, name);
            case "WHOLESALE":
                return new WholesaleCustomer(id, name);
            default:
                throw new IllegalArgumentException("Customer type must be REGULAR or WHOLESALE");
        }
    }
}

