package notification_system;

public interface NotificationListener {
    void onSuccess(String type, String message);
    void onFailure(String type, String message, String reason);
}