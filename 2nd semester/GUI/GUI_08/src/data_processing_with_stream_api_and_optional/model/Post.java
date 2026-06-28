package data_processing_with_stream_api_and_optional.model;

import java.time.LocalDate;
import java.util.List;

public class Post {

    private String       id;
    private User         author;
    private String       content;
    private List<String> tags;
    private int          likes;
    private int          comments;
    private LocalDate    publishedAt;
    private String       category;   // "NEWS" | "HUMOR" | "TECH" | "LIFESTYLE"

    public Post(String id, User author, String content,
                List<String> tags, int likes, int comments,
                LocalDate publishedAt, String category) {
        this.id          = id;
        this.author      = author;
        this.content     = content;
        this.tags        = tags;
        this.likes       = likes;
        this.comments    = comments;
        this.publishedAt = publishedAt;
        this.category    = category;
    }

    public String       getId()          { return id; }
    public User         getAuthor()      { return author; }
    public String       getContent()     { return content; }
    public List<String> getTags()        { return tags; }
    public int          getLikes()       { return likes; }
    public int          getComments()    { return comments; }
    public LocalDate    getPublishedAt() { return publishedAt; }
    public String       getCategory()   { return category; }

    @Override
    public String toString() {
        return String.format("Post{id='%s', author='%s', likes=%d, comments=%d, category='%s', date=%s}",
                id, author.getUsername(), likes, comments, category, publishedAt);
    }
}
