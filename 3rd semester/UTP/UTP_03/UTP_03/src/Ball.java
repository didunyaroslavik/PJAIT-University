import java.awt.*;

public abstract class Ball implements Runnable {

    public static final Object MONITOR = new Object();

    protected int x;
    protected int y;
    protected int vx;
    protected int vy;
    protected int speed;

    protected Thread thread;

    public Ball(int x, int y) {
        this.x = x;
        this.y = y;
        vx = (int)(Math.random() * 7) - 3;
        vy = (int)(Math.random() * 7) - 3;
        speed = 20 + (int)(Math.random() * 11);

        thread = new Thread(this);
        thread.start();
    }

    protected abstract boolean collidingX();
    protected abstract boolean collidingY();

    public void draw(Graphics g) {
        g.fillOval(x - 5, y - 5, 10, 10);
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