package online_movie_rental_system;

public class NewRelease extends Movie {

    public NewRelease(String title, double basePrice, String category) {
        super(title, basePrice, category);
    }

    @Override
    public double getPrice() {
        // 50% more expensive
        return getBasePrice() * 1.5;
    }
}