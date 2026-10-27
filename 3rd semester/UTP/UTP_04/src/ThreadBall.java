import java.awt.*;

public abstract class ThreadBall extends Ball {

    public static final Object MONITOR = new Object();

    protected Thread thread;

    public ThreadBall(int x, int y) {
        super(x, y);
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.BLACK);
        super.draw(g);
    }

    @Override
    public void wakeUp() {
        synchronized (MONITOR) {
            MONITOR.notifyAll();
        }
    }

    @Override
    public void run() {
        while (true) {
            x += vx;
            y += vy;

            if (collidingX()) {
                vx = -vx;
                x += vx;
                synchronized (MONITOR) {
                    try {
                        MONITOR.wait();
                    } catch (InterruptedException e) {
                    }
                }
            }

            if (collidingY()) {
                vy = -vy;
                y += vy;
                synchronized (MONITOR) {
                    try {
                        MONITOR.wait();
                    } catch (InterruptedException e) {
                    }
                }
            }

            try {
                Thread.sleep(speed);
            } catch (InterruptedException e) {
            }
        }
    }
}
