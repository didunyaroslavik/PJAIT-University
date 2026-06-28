package recursion_fractals_and_2d_graphics;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class TreePanel extends JPanel {

    // TODO 11 — random field for natural branch variation
    private final Random random = new Random();

    // TODO 05 / 07 / 08 / 09 / 10 / 11 — recursive draw method
    private void draw(Graphics2D g2d, int xFrom, int yFrom, double radius, double angle, int depth) {

        // TODO 08 — base case
        if (depth == 0) return;

        // TODO 09 — branch thickness proportional to depth (thicker near root)
        g2d.setStroke(new BasicStroke(depth));

        // TODO 11 — slight random variation of the branching angle
        double deltaAngle = 0.25 + random.nextDouble() * 0.1;

        // TODO 10 — brown trunk/main branches, green leaves
        if (depth > 3) {
            g2d.setColor(new Color(101, 67, 33));
        } else {
            g2d.setColor(new Color(34, 139, 34));
        }

        // TODO 05 — left branch endpoint and line
        double leftEndX = xFrom + Math.sin(angle + deltaAngle) * radius;
        double leftEndY = yFrom + Math.cos(angle + deltaAngle) * radius;
        g2d.drawLine(xFrom, yFrom, (int) leftEndX, (int) leftEndY);

        // TODO 07 — right branch endpoint and line
        double rightEndX = xFrom + Math.sin(angle - deltaAngle) * radius;
        double rightEndY = yFrom + Math.cos(angle - deltaAngle) * radius;
        g2d.drawLine(xFrom, yFrom, (int) rightEndX, (int) rightEndY);

        // TODO 11 — random length multiplier for each sub-branch
        double multLeft  = 0.6 + random.nextDouble() * 0.2;
        double multRight = 0.6 + random.nextDouble() * 0.2;

        // TODO 08 — recursive calls for left and right sub-branches
        draw(g2d, (int) leftEndX,  (int) leftEndY,  radius * multLeft,  angle + deltaAngle, depth - 1);
        draw(g2d, (int) rightEndX, (int) rightEndY, radius * multRight, angle - deltaAngle, depth - 1);
    }

    // TODO 06 / 09 / 12 — paintComponent initialises parameters and triggers drawing
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // TODO 09 — upgrade Graphics to Graphics2D for stroke control
        Graphics2D g2d = (Graphics2D) g;

        // TODO 12 — dark background (bonsai aesthetic)
        g2d.setColor(new Color(20, 20, 20));
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // TODO 06 — starting parameters
        int    xFrom  = getWidth()  / 2;
        int    yFrom  = getHeight() - 20;
        int    radius = 100;
        double angle  = Math.PI;        // points upward in Swing coordinates

        draw(g2d, xFrom, yFrom, radius, angle, 10);
    }
}
