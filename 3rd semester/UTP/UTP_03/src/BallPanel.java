import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class BallPanel extends Panel implements Runnable, ActionListener {

    public List<Ball> ballList;

    public BallPanel() {
        ballList = new ArrayList<>();
        new Thread(this).start();
    }

    @Override
    public void paint(Graphics g) {
        for (Ball b : ballList) {
            b.draw(g);
        }
    }

    @Override
    public void run() {
        while (true) {
            repaint();
            try {
                Thread.sleep(33);
            } catch (InterruptedException e) {
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Ball ball = new Ball(getWidth() / 2, getHeight() / 2) {
            @Override
            protected boolean collidingX() {
                return x - 5 < 0 || x + 5 > getWidth();
            }

            @Override
            protected boolean collidingY() {
                return y - 5 < 0 || y + 5 > getHeight();
            }
        };
        ballList.add(ball);
    }

    public void shake() {
        synchronized (Ball.MONITOR) {
            Ball.MONITOR.notifyAll();
        }
    }
}