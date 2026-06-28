package notification_system;

public class SmsNotification extends BaseNotification {

    public SmsNotification(String phoneNumber) {
        super(phoneNumber);
    }

    @Override
    public String getType() {
        return "SMS";
    }

    @Override
    protected String formatMessage(String message) {
        String text = message;
        if (message.length() > 160) {
            text = message.substring(0, 157) + "...";
        }
        return "SMS to " + getRecipient() + ": " + text;
    }

    @Override
    protected void doSend(String formatted) {
        System.out.println("[SMS] " + formatted);
    }
}