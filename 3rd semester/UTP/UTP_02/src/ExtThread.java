public class ExtThread extends Thread{

    public long objCreationTime;
    public long firstInstruction;

    public ExtThread() {
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
