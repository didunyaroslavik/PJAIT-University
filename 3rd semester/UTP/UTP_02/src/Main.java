import java.util.Arrays;

public class Main{

    public static void main(String[] args) throws InterruptedException {
        final int WARMUP = 5_000;
        final int N = 25_000;

        for (int i = 0; i < WARMUP; i++) {
            measureExt();
            measureRun();
        }

        long[] extTimes = new long[N];
        long[] runTimes = new long[N];

        for (int i = 0; i < N; i++) {
            extTimes[i] = measureExt();
            runTimes[i] = measureRun();
        }

        System.out.println("ExtThread,Runnable");
        for (int i = 0; i < N; i++) {
            System.out.println(extTimes[i] + "," + runTimes[i]);
        }

        Arrays.sort(extTimes);
        Arrays.sort(runTimes);

        System.err.println("N = " + N);
        System.err.println("ExtThread: avg=" + average(extTimes)
                + " p50=" + percentile(extTimes, 50)
                + " p90=" + percentile(extTimes, 90)
                + " p99=" + percentile(extTimes, 99));
        System.err.println("Runnable:  avg=" + average(runTimes)
                + " p50=" + percentile(runTimes, 50)
                + " p90=" + percentile(runTimes, 90)
                + " p99=" + percentile(runTimes, 99));
    }

    static long measureExt() throws InterruptedException {
        ExtThread t = new ExtThread();
        t.start();
        t.join();
        return t.getTime();
    }


    static long measureRun() throws InterruptedException {
        RunTask r = new RunTask();
        Thread thread = new Thread(r);
        thread.start();
        thread.join();
        return r.getTime();
    }

    static double average(long[] a) {
        long sum = 0;
        for (int i = 0; i < a.length; i++) {
            sum += a[i];
        }
        return (double) sum / a.length;
    }

    static long percentile(long[] sorted, double p) {
        int index = (int) Math.ceil(p / 100.0 * sorted.length) - 1;
        return sorted[index];
    }

}
