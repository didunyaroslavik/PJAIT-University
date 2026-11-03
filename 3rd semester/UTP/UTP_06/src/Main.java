import model.User;
import processing.ObjectProcessor;

public class Main {
    public static void main(String[] args) {
        ObjectProcessor processor = new ObjectProcessor();

        System.out.println("Case 1: valid object");
        User validUser = new User(1, "JanKowalski", "jan.kowalski@example.com", 30, "superSecret123");
        System.out.println("Result: " + processor.process(validUser));
        System.out.println("----------------------------------------");

        System.out.println("Case 2: validation error (blank username)");
        User invalidUserBlankName = new User(2, " ", "anna.nowak@example.com", 25, "password");
        System.out.println("Result: " + processor.process(invalidUserBlankName));
        System.out.println("----------------------------------------");

        System.out.println("Case 3: validation error (age out of range)");
        User invalidUserAge = new User(3, "PiotrZielinski", "piotr.z@example.com", 16, "anotherPass");
        System.out.println("Result: " + processor.process(invalidUserAge));
        System.out.println("----------------------------------------");
    }
}
