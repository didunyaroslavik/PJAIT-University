package notification_system;

public interface Notification {
    void send(String message);
    String getType();
}