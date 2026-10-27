import java.awt.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class VThreadBall extends VBall {

    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private volatile boolean paused = false;

    public VThreadBall(int x, int y) {
        super(x, y);
        vExecutor.submit(this);
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.BLUE);
        super.draw(g);
    }

    public void checkPaused() {
        lock.lock();
        try {
            while (paused) {
                condition.await();
            }
        } catch (InterruptedException e) {
        } finally {
            lock.unlock();
        }
    }

    public void pause() {
        lock.lock();
        try {
            paused = true;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void wakeUp() {
        lock.lock();
        try {
            paused = false;
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void run() {
        while (true) {
            checkPaused();

            if (collidingX()) {
                vx = -vx;
                pause();
            }

            if (collidingY()) {
                vy = -vy;
                pause();
            }

            x += vx;
            y += vy;

            try {
                Thread.sleep(speed);
            } catch (InterruptedException e) {
            }
        }
    }
}
