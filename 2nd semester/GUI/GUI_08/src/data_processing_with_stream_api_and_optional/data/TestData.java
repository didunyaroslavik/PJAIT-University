package data_processing_with_stream_api_and_optional.data;

import data_processing_with_stream_api_and_optional.model.Post;
import data_processing_with_stream_api_and_optional.model.User;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class TestData {

    public static List<User> getUsers() {
        return List.of(
            new User("anna_k",     "Warszawa",  1200, true),
            new User("bartek99",   "Kraków",     340, false),
            new User("zosia_m",    "Warszawa",  8900, true),
            new User("tomek_dev",  "Gdańsk",     120, false),
            new User("kasia.foto", "Wrocław",   4500, true),
            new User("pawel_x",    "Kraków",      89, false)
        );
    }

    public static List<Post> getPosts() {
        List<User> u = getUsers();

        return List.of(
            new Post("p1",  u.get(0), "Java Stream API to świetna sprawa!",
                     Arrays.asList("java","programowanie","tech"),
                     245, 30, LocalDate.of(2024,3,10), "TECH"),

            new Post("p2",  u.get(1), "Kraków dziś piękny :)",
                     Arrays.asList("miasto","lifestyle"),
                     87, 5, LocalDate.of(2024,3,11), "LIFESTYLE"),

            new Post("p3",  u.get(2), "Nowe przepisy podatkowe – co musisz wiedzieć",
                     Arrays.asList("news","prawo","podatki"),
                     523, 98, LocalDate.of(2024,3,12), "NEWS"),

            new Post("p4",  u.get(0), "Mój ulubiony #java trick – metoda reduce()",
                     Arrays.asList("java","programowanie"),
                     310, 45, LocalDate.of(2024,3,13), "TECH"),

            new Post("p5",  u.get(3), "Docker + Spring Boot w 10 minut",
                     Arrays.asList("docker","java","tech"),
                     198, 22, LocalDate.of(2024,3,14), "TECH"),

            new Post("p6",  u.get(4), "Złota godzina – moje zdjęcia z Wrocławia",
                     Arrays.asList("foto","lifestyle","wroclaw"),
                     892, 67, LocalDate.of(2024,3,14), "LIFESTYLE"),

            new Post("p7",  u.get(5), "Czy AI zastąpi programistów?",
                     Arrays.asList("ai","tech","programowanie"),
                     134, 88, LocalDate.of(2024,3,15), "TECH"),

            new Post("p8",  u.get(2), "Breaking news – wybory samorządowe",
                     Arrays.asList("news","polityka"),
                     445, 120, LocalDate.of(2024,3,15), "NEWS"),

            new Post("p9",  u.get(1), "Top 5 kawiarni w Krakowie",
                     Arrays.asList("lifestyle","kraków","jedzenie"),
                     201, 33, LocalDate.of(2024,3,16), "LIFESTYLE"),

            new Post("p10", u.get(0), "Wydajność streamów równoległych w Javie",
                     Arrays.asList("java","programowanie","wydajność"),
                     178, 41, LocalDate.of(2024,3,17), "TECH")
        );
    }
}
