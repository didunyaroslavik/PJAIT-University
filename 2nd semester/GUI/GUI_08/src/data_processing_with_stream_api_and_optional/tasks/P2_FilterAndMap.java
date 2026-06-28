package data_processing_with_stream_api_and_optional.tasks;

import data_processing_with_stream_api_and_optional.data.TestData;
import data_processing_with_stream_api_and_optional.model.Post;

import java.util.List;
import java.util.stream.Collectors;

public class P2_FilterAndMap {

    public static void go() {

        printHeader("Problem P2 – filter(), map(), distinct()");

        List<Post> posts = TestData.getPosts();

        System.out.println("--- [1] Posts in TECH category (first 40 characters) ---");
        // TODO 07 – keep only TECH posts and print the first 40 chars of each content
        posts.stream()
             .filter(p -> p.getCategory().equals("TECH"))
             .map(p -> p.getContent().substring(0, Math.min(40, p.getContent().length())))
             .forEach(System.out::println);

        System.out.println("\n--- [2] Unique post authors (distinct) ---");
        // TODO 08 – extract author usernames, remove duplicates with distinct(), then print
        posts.stream()
             .map(p -> p.getAuthor().getUsername())
             .distinct()
             .forEach(System.out::println);

        System.out.println("\n--- [3] Posts with more than 200 likes ---");
        // TODO 09 – filter posts with > 200 likes and format as "author -> likes"
        posts.stream()
             .filter(p -> p.getLikes() > 200)
             .map(p -> String.format("%-12s -> %d", p.getAuthor().getUsername(), p.getLikes()))
             .forEach(System.out::println);

        System.out.println("\n--- [4] Post contents in UPPERCASE ---");
        // TODO 10 – extract content, convert to uppercase, collect to list, then print
        List<String> upperContents = posts.stream()
                .map(Post::getContent)
                .map(String::toUpperCase)
                .collect(Collectors.toList());

        upperContents.forEach(System.out::println);
    }

    private static void printHeader(String title) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println(" " + title);
        System.out.println("=".repeat(60));
    }
}
