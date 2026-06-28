package notification_system;

public class Main {

    public static void main(String[] args) {

        NotificationService service = new NotificationService("OnlineStore");

        service.addChannel(new EmailNotification("jan@pj.edu"));
        service.addChannel(new SmsNotification("+48 22 58 44 500"));

        // Add anonymous Notification channel
        service.addChannel(new Notification() {
            @Override
            public void send(String message) {
                System.out.println("[PUSH] " + message);
            }

            @Override
            public String getType() {
                return "PUSH";
            }
        });

        // Add NotificationFilters
        service.addFilter(msg -> !msg.isBlank());
        service.addFilter(msg -> msg.length() <= 200);
        service.addFilter(msg -> !msg.toLowerCase().contains("spam"));

        // Add anonymous NotificationListener
        service.addListener(new NotificationListener() {
            private int successCount = 0;
            private int failCount = 0;

            @Override
            public void onSuccess(String type, String message) {
                successCount++;
                System.out.println("[AUDIT] OK #" + successCount + " via " + type);
            }

            @Override
            public void onFailure(String type, String message, String reason) {
                failCount++;
                System.out.println("[AUDIT] FAIL #" + failCount + " via " + type + "\n" + reason);
            }
        });

        service.sendAll("Your order #1234 has been shipped!");
        service.sendAll("");
        service.sendAll("This is SPAM content");
        service.sendAll("Welcome to our store!");

        service.printHistory();

        // Display EMAIL channel only
        System.out.println("\nEMAIL only");
        NotificationService.Result[] emailResults = service.getByChannel("EMAIL");
        for (int i = 0; i < emailResults.length; i++) {
            System.out.println(emailResults[i]);
        }

        // Display results sorted by timestamp (newest first)
        System.out.println("\nSorted by timestamp (newest first)");
        NotificationService.Result[] sorted = NotificationService.sort(
                service.getSuccessful(),
                (a, b) -> b.getTimestamp().compareTo(a.getTimestamp())
        );
        for (int i = 0; i < sorted.length; i++) {
            System.out.println(sorted[i]);
        }
    }
}