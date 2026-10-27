import java.awt.*;
import java.util.concurrent.Semaphore;

public abstract class VsThreadBall extends VBall {

    private final Semaphore semaphore = new Semaphore(1);

    public VsThreadBall(int x, int y) {
        super(x, y);
        vExecutor.submit(this);
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.GREEN);
        super.draw(g);
    }

    @Override
    public void wakeUp() {
        semaphore.release();
    }

    @Override
    public void run() {
        while (true) {
            try {
                semaphore.acquire();
                semaphore.release();

                if (collidingX()) {
                    vx = -vx;
                    semaphore.acquire();
                }

                if (collidingY()) {
                    vy = -vy;
                    semaphore.acquire();
                }

                x += vx;
                y += vy;

                Thread.sleep(speed);
            } catch (InterruptedException e) {
            }
        }
    }
}
