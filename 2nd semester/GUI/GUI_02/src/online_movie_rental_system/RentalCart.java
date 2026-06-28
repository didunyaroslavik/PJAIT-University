package online_movie_rental_system;

public abstract class RentalCart {
    private Movie[] cart;
    private int movieCount;

    public RentalCart() {
        this.cart = new Movie[5]; // Initial capacity
        this.movieCount = 0;
    }

    public void addMovie(Movie movie) {
        if (movie == null) return;

        // Expand array if full
        if (movieCount == cart.length) {
            Movie[] newCart = new Movie[cart.length * 2];
            for (int i = 0; i < cart.length; i++) {
                newCart[i] = cart[i];
            }
            cart = newCart;
        }

        cart[movieCount] = movie;
        movieCount++;
    }

    public void removeMovie(Movie movie) {
        if (movie == null) return;

        int indexToRemove = -1;
        for (int i = 0; i < movieCount; i++) {
            if (cart[i].equals(movie)) {
                indexToRemove = i;
                break;
            }
        }

        // If found, shift elements to the left to fill the gap
        if (indexToRemove != -1) {
            for (int i = indexToRemove; i < movieCount - 1; i++) {
                cart[i] = cart[i + 1];
            }
            cart[movieCount - 1] = null;
            movieCount--;
        }
    }

    public double getTotalPrice() {
        double total = 0;

        for (int i = 0; i < movieCount; i++) {
            total += cart[i].getPrice();
        }

        // Additional Challenge: 10% discount if renting more than 3 movies at once
        if (movieCount > 3) {
            total *= 0.90;
        }

        return total;
    }
}