package data_processing_with_stream_api_and_optional.model;

public class User {

    private String username;
    private String city;
    private int followersCount;
    private boolean isVerified;

    public User(String username, String city, int followersCount, boolean isVerified) {
        this.username       = username;
        this.city           = city;
        this.followersCount = followersCount;
        this.isVerified     = isVerified;
    }

    public String  getUsername()       { return username; }
    public String  getCity()           { return city; }
    public int     getFollowersCount() { return followersCount; }
    public boolean isVerified()        { return isVerified; }

    @Override
    public String toString() {
        return String.format("User{username='%s', city='%s', followers=%d, verified=%b}",
                username, city, followersCount, isVerified);
    }
}
