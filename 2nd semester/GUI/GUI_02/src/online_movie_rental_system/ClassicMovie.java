package online_movie_rental_system;

public class ClassicMovie extends Movie {

    public ClassicMovie(String title, double basePrice, String category) {
        super(title, basePrice, category);
    }

    @Override
    public double getPrice() {
        // 20% discount
        return getBasePrice() * 0.8;
    }
}