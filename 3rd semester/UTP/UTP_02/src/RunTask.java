public class RunTask implements Runnable {

    public long objCreationTime;
    public long firstInstruction;

    public RunTask() {
        objCreationTime = System.nanoTime();
    }

    @Override
    public void run() {
        firstInstruction = System.nanoTime();
    }

    public long getTime() {
        return firstInstruction - objCreationTime;
    }
}
