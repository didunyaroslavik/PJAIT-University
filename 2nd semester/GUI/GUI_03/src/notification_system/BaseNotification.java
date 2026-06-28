package notification_system;

public abstract class BaseNotification implements Notification {
    private final String recipient;

    public BaseNotification(String recipient) {
        this.recipient = recipient;
    }

    public String getRecipient() {
        return recipient;
    }

    @Override
    public void send(String message) {
        String formatted = formatMessage(message);
        doSend(formatted);
    }

    protected abstract String formatMessage(String message);

    protected abstract void doSend(String formatted);
}