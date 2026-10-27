import java.awt.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public abstract class ScheduledBall extends Ball {

    public static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    protected ScheduledFuture<?> scheduledFuture;

    public ScheduledBall(int x, int y) {
        super(x, y);
        scheduledFuture = scheduler.scheduleAtFixedRate(this, 0, speed, TimeUnit.MILLISECONDS);
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.RED);
        super.draw(g);
    }

    @Override
    public void run() {
        if (collidingX()) {
            vx *= -1;
            x += vx;
            scheduledFuture.cancel(true);
        }

        if (collidingY()) {
            vy *= -1;
            y += vy;
            scheduledFuture.cancel(true);
        }

        x += vx;
        y += vy;
    }

    @Override
    public void wakeUp() {
        if (scheduledFuture.isCancelled()) {
            scheduledFuture = scheduler.scheduleAtFixedRate(this, 0, speed, TimeUnit.MILLISECONDS);
        }
    }
}
