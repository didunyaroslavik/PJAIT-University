import java.awt.*;

public abstract class Ball implements Runnable {

    protected int x;
    protected int y;
    protected int vx;
    protected int vy;
    protected int speed;

    public Ball(int x, int y) {
        this.x = x;
        this.y = y;
        vx = (int)(Math.random() * 7) - 3;
        vy = (int)(Math.random() * 7) - 3;
        speed = 20 + (int)(Math.random() * 11);
    }

    protected abstract boolean collidingX();
    protected abstract boolean collidingY();

    public abstract void wakeUp();

    public void draw(Graphics g) {
        g.fillOval(x - 5, y - 5, 10, 10);
    }
}
