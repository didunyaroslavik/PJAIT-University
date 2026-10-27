import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class VBall extends Ball {

    protected static ExecutorService vExecutor = Executors.newVirtualThreadPerTaskExecutor();

    public VBall(int x, int y) {
        super(x, y);
    }
}
