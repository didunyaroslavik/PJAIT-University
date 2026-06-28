package notification_system;

@FunctionalInterface
public interface NotificationFilter {
    boolean accept(String message);
}