package online_movie_rental_system;

public class OnlineRentalStore extends RentalCart {

    public static void main(String[] args) {
        OnlineRentalStore rentalCart = new OnlineRentalStore();

        Movie movie1 = new RegularMovie("Inception", 4.99, "Action");
        Movie movie2 = new NewRelease("Dune: Part Two", 7.99, "Sci-Fi");
        Movie movie3 = new ClassicMovie("The Godfather", 5.99, "Drama");
        Movie movie4 = new RegularMovie("Interstellar", 4.99, "Sci-Fi");

        rentalCart.addMovie(movie1);
        rentalCart.addMovie(movie2);
        rentalCart.addMovie(movie3);
        rentalCart.addMovie(movie4);

        // Formatted to 2 decimal places for better presentation
        System.out.printf("Total price: %.2f PLN%n", rentalCart.getTotalPrice());

        rentalCart.removeMovie(movie2);

        System.out.printf("Total price after removing a movie: %.2f PLN%n", rentalCart.getTotalPrice());
    }
}