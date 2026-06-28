package notification_system;

import java.time.LocalDateTime;

public class NotificationService {

    @FunctionalInterface
    public interface ResultFilter {
        boolean accept(Result result);
    }

    @FunctionalInterface
    public interface ResultComparator {
        int compare(Result a, Result b);
    }

    public static class Result {
        private final boolean success;
        private final String channel;
        private final String message;
        private final String failReason;
        private final LocalDateTime timestamp;

        public Result(boolean success, String channel, String message, String failReason) {
            this.success = success;
            this.channel = channel;
            this.message = message;
            this.failReason = failReason;
            this.timestamp = LocalDateTime.now();
        }

        public boolean isSuccess() { return success; }
        public String getChannel() { return channel; }
        public String getMessage() { return message; }
        public String getFailReason() { return failReason; }
        public LocalDateTime getTimestamp() { return timestamp; }

        @Override
        public String toString() {
            String status = success ? "OK" : "FAIL";
            String suffix = success ? "" : " (reason: " + failReason + ")";
            return "  [" + status + "] [" + channel + "] " + message + suffix;
        }
    }

    private class History {

        private Result[] results = new Result[10];
        private int count = 0;

        void add(Result result) {
            if (count == results.length) {
                Result[] bigger = new Result[results.length * 2];
                for (int i = 0; i < results.length; i++) {
                    bigger[i] = results[i];
                }
                results = bigger;
            }
            results[count++] = result;
        }

        Result[] getAll() {
            Result[] copy = new Result[count];
            for (int i = 0; i < count; i++) {
                copy[i] = results[i];
            }
            return copy;
        }

        Result[] getFiltered(ResultFilter filter) {
            int matchCount = 0;
            for (int i = 0; i < count; i++) {
                if (filter.accept(results[i])) {
                    matchCount++;
                }
            }

            Result[] filtered = new Result[matchCount];
            int idx = 0;
            for (int i = 0; i < count; i++) {
                if (filter.accept(results[i])) {
                    filtered[idx++] = results[i];
                }
            }
            return filtered;
        }

        void printSummary() {
            int okCount = 0;
            for (int i = 0; i < count; i++) {
                if (results[i].isSuccess()) {
                    okCount++;
                }
            }

            System.out.println("\n=== History: " + serviceName + " ===");
            System.out.println("Total: " + count + " | OK: " + okCount + " | Failed: " + (count - okCount));

            for (int i = 0; i < count; i++) {
                System.out.println(results[i]);
            }
        }
    }

    private final String serviceName;
    private final History history = new History();

    private Notification[] channels = new Notification[10];
    private int channelCount = 0;

    private NotificationListener[] listeners = new NotificationListener[10];
    private int listenerCount = 0;

    private NotificationFilter[] filters = new NotificationFilter[10];
    private int filterCount = 0;

    public NotificationService(String serviceName) {
        this.serviceName = serviceName;
    }

    public void addChannel(Notification channel) {
        if (channelCount == channels.length) {
            Notification[] bigger = new Notification[channels.length * 2];
            for (int i = 0; i < channels.length; i++) {
                bigger[i] = channels[i];
            }
            channels = bigger;
        }
        channels[channelCount++] = channel;
    }

    public void addListener(NotificationListener listener) {
        if (listenerCount == listeners.length) {
            NotificationListener[] bigger = new NotificationListener[listeners.length * 2];
            for (int i = 0; i < listeners.length; i++) {
                bigger[i] = listeners[i];
            }
            listeners = bigger;
        }
        listeners[listenerCount++] = listener;
    }

    public void addFilter(NotificationFilter filter) {
        if (filterCount == filters.length) {
            NotificationFilter[] bigger = new NotificationFilter[filters.length * 2];
            for (int i = 0; i < filters.length; i++) {
                bigger[i] = filters[i];
            }
            filters = bigger;
        }
        filters[filterCount++] = filter;
    }

    public void sendAll(String message) {
        System.out.println("\nSending: \"" + message + "\"");

        // Message filtering validation
        for (int f = 0; f < filterCount; f++) {
            if (!filters[f].accept(message)) {
                String reason = "Message rejected by filter";
                System.out.println("BLOCKED: " + reason);

                for (int c = 0; c < channelCount; c++) {
                    history.add(new Result(false, channels[c].getType(), message, reason));

                    for (int l = 0; l < listenerCount; l++) {
                        listeners[l].onFailure(channels[c].getType(), message, reason);
                    }
                }
                return; // Stop further processing if rejected
            }
        }

        // Send notifications
        for (int c = 0; c < channelCount; c++) {
            channels[c].send(message);
            history.add(new Result(true, channels[c].getType(), message, null));

            for (int l = 0; l < listenerCount; l++) {
                listeners[l].onSuccess(channels[c].getType(), message);
            }
        }
    }

    public void printHistory() {
        history.printSummary();
    }

    public Result[] getSuccessful() {
        return history.getFiltered(result -> result.isSuccess());
    }

    public Result[] getByChannel(String channel) {
        return history.getFiltered(result -> result.getChannel().equals(channel));
    }

    public static Result[] sort(Result[] results, ResultComparator comparator) {
        Result[] sorted = new Result[results.length];
        for (int i = 0; i < results.length; i++) {
            sorted[i] = results[i];
        }

        for (int i = 0; i < sorted.length; i++) {
            for (int j = 0; j < sorted.length - 1 - i; j++) {
                if (comparator.compare(sorted[j], sorted[j + 1]) > 0) {
                    Result temp = sorted[j];
                    sorted[j] = sorted[j + 1];
                    sorted[j + 1] = temp;
                }
            }
        }
        return sorted;
    }
}