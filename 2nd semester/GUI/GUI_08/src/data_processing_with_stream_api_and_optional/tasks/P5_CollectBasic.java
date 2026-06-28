package data_processing_with_stream_api_and_optional.tasks;

import data_processing_with_stream_api_and_optional.data.TestData;
import data_processing_with_stream_api_and_optional.model.Post;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class P5_CollectBasic {

    public static void go() {

        printHeader("Problem P5 – collect() basics");

        List<Post> posts = TestData.getPosts();

        // TODO 23 – filter TECH posts and collect into a List
        List<Post> techPosts = posts.stream()
                .filter(p -> p.getCategory().equals("TECH"))
                .collect(Collectors.toList());
        System.out.println("Number of TECH posts: " + techPosts.size());

        // TODO 24 – iterate the techPosts list and print each element with 2-space indent
        techPosts.forEach(p -> System.out.println("  " + p.toString()));

        System.out.println("\n--- [2] toSet() – unique categories ---");
        // TODO 25 – extract categories and collect to a Set (automatically removes duplicates)
        Set<String> categories = posts.stream()
                .map(Post::getCategory)
                .collect(Collectors.toSet());
        System.out.println("Unique categories: " + categories);

        System.out.println("\n--- [2b] Comparison List vs Set with duplicates ---");
        // TODO 26 – collect author names to List (keeps duplicates) and to Set (removes them)
        List<String> authorsList = posts.stream()
                .map(p -> p.getAuthor().getUsername())
                .collect(Collectors.toList());

        Set<String> authorsSet = posts.stream()
                .map(p -> p.getAuthor().getUsername())
                .collect(Collectors.toSet());

        System.out.println("List (with duplicates): " + authorsList.size() + " elements -> " + authorsList);
        System.out.println("Set  (without duplicates): " + authorsSet.size() + " elements -> " + authorsSet);

        System.out.println("\n--- [3] toMap() – post id -> number of likes ---");
        // TODO 27 – build a Map where each post id maps to its like count, then print every entry
        Map<String, Integer> likesById = posts.stream()
                .collect(Collectors.toMap(Post::getId, Post::getLikes));

        likesById.forEach((id, likes) -> System.out.printf(" %-5s -> %d %n", id, likes));

        System.out.println("\n--- [4] List of authors (with duplicates) ---");
        // TODO 28 – collect author names to a list (preserving duplicates) and print its size and contents
        List<String> authorsWithDuplicates = posts.stream()
                .map(p -> p.getAuthor().getUsername())
                .collect(Collectors.toList());

        System.out.println("Size of authors list (with duplicates): " + authorsWithDuplicates.size());
        System.out.println("List: " + authorsWithDuplicates);

        System.out.println("\n--- [BONUS] toUnmodifiableList() ---");
        // TODO 29 – collect distinct categories into an unmodifiable list and prove it can't be changed
        List<String> immutable = posts.stream()
                .map(Post::getCategory)
                .distinct()
                .collect(Collectors.toUnmodifiableList());

        System.out.println("Unmodifiable list of categories: " + immutable);

        // Attempting to modify the unmodifiable list throws UnsupportedOperationException
        try {
            immutable.add("SPORT");
        } catch (UnsupportedOperationException e) {
            System.out.println("Attempt to modify unmodifiable list: " + e.getClass().getSimpleName());
        }
    }

    private static void printHeader(String title) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println(" " + title);
        System.out.println("=".repeat(60));
    }
}
