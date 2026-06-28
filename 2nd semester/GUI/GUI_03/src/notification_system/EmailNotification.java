package notification_system;

public class EmailNotification extends BaseNotification {

    public EmailNotification(String email) {
        super(email);
    }

    @Override
    public String getType() {
        return "EMAIL";
    }

    @Override
    protected String formatMessage(String message) {
        return "Subject: Notification | To: " + getRecipient() + " | Body: " + message;
    }

    @Override
    protected void doSend(String formatted) {
        System.out.println("[EMAIL] " + formatted);
    }
}