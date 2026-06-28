package data_processing_with_stream_api_and_optional.tasks;

import data_processing_with_stream_api_and_optional.data.TestData;
import data_processing_with_stream_api_and_optional.model.Post;
import data_processing_with_stream_api_and_optional.model.User;

import java.util.List;
import java.util.Optional;

public class P4_TerminalOperations {

    public static void go() {

        printHeader("Problem P4 – Terminal Operations");

        List<Post> posts = TestData.getPosts();
        List<User> users = TestData.getUsers();

        System.out.println("--- [1] count() – number of NEWS posts ---");
        // TODO 16 – filter NEWS posts and count how many there are
        long newsCount = posts.stream()
                .filter(p -> p.getCategory().equals("NEWS"))
                .count();
        System.out.println("Number of NEWS posts: " + newsCount);

        System.out.println("\n--- [2] anyMatch() – is there a post with tag 'docker'? ---");
        // TODO 17 – check if at least one post contains the tag "docker"
        boolean hasDocker = posts.stream()
                .anyMatch(p -> p.getTags().contains("docker"));
        System.out.println("Is there a post with 'docker': " + hasDocker);

        System.out.println("\n--- [3] allMatch() – do all have at least 1 comment? ---");
        // TODO 18 – check if every post has more than 0 comments
        boolean allHaveComments = posts.stream()
                .allMatch(p -> p.getComments() > 0);
        System.out.println("Do all have comments: " + allHaveComments);

        System.out.println("\n--- [4] noneMatch() – do none of the posts have negative likes? ---");
        // TODO 19 – check that no post has a negative like count
        boolean noNegativeLikes = posts.stream()
                .noneMatch(p -> p.getLikes() < 0);
        System.out.println("Do none have negative likes: " + noNegativeLikes);

        System.out.println("\n--- [5] findFirst() – first LIFESTYLE post ---");
        // TODO 20 – find the first LIFESTYLE post and handle the result with Optional
        Optional<Post> firstLifestyle = posts.stream()
                .filter(p -> p.getCategory().equals("LIFESTYLE"))
                .findFirst();

        // ifPresentOrElse: print the content if found, otherwise print a "not found" message
        firstLifestyle.ifPresentOrElse(
                p -> System.out.println("Found: \"" + p.getContent() + "\""),
                ()  -> System.out.println("No posts in LIFESTYLE category"));

        // map Optional<Post> to Optional<String> (content), then provide a default value
        String content = firstLifestyle
                .map(Post::getContent)
                .orElse("Brak treści");
        System.out.println("Content (orElse): " + content);

        System.out.println("\n--- [6] findFirst() – unverified user from Kraków ---");
        // TODO 21 – find first unverified user from Kraków, handle with ifPresentOrElse
        Optional<User> unverifiedKrakow = users.stream()
                .filter(u -> !u.isVerified() && u.getCity().equals("Kraków"))
                .findFirst();

        unverifiedKrakow.ifPresentOrElse(
                u -> System.out.println("Unverified user from Kraków: " + u.getUsername()),
                ()  -> System.out.println("No matching user found"));

        System.out.println("\n--- [7] Optional – method demonstration ---");
        // TODO 22 – search for a post with > 1000 likes (none exist) and demo Optional methods
        Optional<Post> maybePost = posts.stream()
                .filter(p -> p.getLikes() > 1000)
                .findFirst();

        // isPresent() returns false because no post has > 1000 likes
        System.out.println("isPresent(): " + maybePost.isPresent());

        // orElse(null) returns null when the Optional is empty
        System.out.println("orElse(): " + maybePost.orElse(null));

        // orElseGet() runs a supplier when empty; we create a placeholder Post with content "None"
        String orElseGetContent = maybePost
                .orElseGet(() -> new Post(null, null, "None", null, 0, 0, null, null))
                .getContent();
        System.out.println("orElseGet(): " + orElseGetContent);

        // orElseThrow() throws a RuntimeException when the Optional is empty
        try {
            maybePost.orElseThrow(() -> new RuntimeException("Post does not exist!"));
        } catch (RuntimeException e) {
            System.out.println("orElseThrow() caught: " + e.getMessage());
        }
    }

    private static void printHeader(String title) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println(" " + title);
        System.out.println("=".repeat(60));
    }
}
