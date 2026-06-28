package online_movie_rental_system;

public abstract class Movie {
    private final String title;
    private final double basePrice;
    private final String category;

    public Movie(String title, double basePrice, String category) {
        this.title = title;
        this.basePrice = basePrice;
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public double getBasePrice() {
        return basePrice;
    }

    // Abstract method to be overridden by subclasses
    public abstract double getPrice();
}