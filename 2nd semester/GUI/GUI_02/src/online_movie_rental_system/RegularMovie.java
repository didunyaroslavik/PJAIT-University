package online_movie_rental_system;

public class RegularMovie extends Movie {

    public RegularMovie(String title, double basePrice, String category) {
        super(title, basePrice, category);
    }

    @Override
    public double getPrice() {
        return getBasePrice();
    }
}