package data_processing_with_stream_api_and_optional.tasks;

import data_processing_with_stream_api_and_optional.data.TestData;
import data_processing_with_stream_api_and_optional.model.Post;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class P3_SortLimitSkip {

    public static void go() {

        printHeader("Problem P3 – sorted(), limit(), skip()");

        List<Post> posts = TestData.getPosts();

        System.out.println("--- [1] TOP 3 posts by likes ---");
        // TODO 11 – sort by likes descending, take top 3, format and print
        posts.stream()
             .sorted(Comparator.comparingInt(Post::getLikes).reversed())
             .limit(3)
             .map(p -> String.format("%-12s -> %d", p.getAuthor().getUsername(), p.getLikes()))
             .forEach(System.out::println);

        System.out.println("\n--- [2] Posts from newest ---");
        // TODO 12 – sort by publication date descending, format "date | content(35)" and print
        posts.stream()
             .sorted(Comparator.comparing(Post::getPublishedAt).reversed())
             .map(p -> String.format("%s | %s",
                     p.getPublishedAt(),
                     p.getContent().substring(0, Math.min(35, p.getContent().length()))))
             .forEach(System.out::println);

        System.out.println("\n--- [3] Posts starting from the 4th (skip 3) ---");
        // TODO 13 – skip the first 3 posts, format "[id] content(35)" and print the rest
        posts.stream()
             .skip(3)
             .map(p -> String.format("[%s] %s",
                     p.getId(),
                     p.getContent().substring(0, Math.min(35, p.getContent().length()))))
             .forEach(System.out::println);

        System.out.println("\n--- [4] Pagination – page 2, size 4 ---");
        int page     = 2;
        int pageSize = 4;
        // TODO 14 – skip elements from previous pages and take one page worth, then print
        List<Post> pageResult = posts.stream()
                .skip((long) (page - 1) * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());

        pageResult.forEach(p -> System.out.println(
                String.format("[%s] %s (%d)",
                        p.getId(),
                        p.getContent().substring(0, Math.min(35, p.getContent().length())),
                        p.getLikes())));

        System.out.println("\n--- [BONUS] Sorting: category → likes descending ---");
        // TODO 15 – primary sort: category ascending; secondary sort: likes descending
        posts.stream()
             .sorted(Comparator.comparing(Post::getCategory)
                     .thenComparing(Comparator.comparingInt(Post::getLikes).reversed()))
             .map(p -> String.format("%-10s | %-12s | %d",
                     p.getCategory(),
                     p.getAuthor().getUsername(),
                     p.getLikes()))
             .forEach(System.out::println);
    }

    private static void printHeader(String title) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println(" " + title);
        System.out.println("=".repeat(60));
    }
}
