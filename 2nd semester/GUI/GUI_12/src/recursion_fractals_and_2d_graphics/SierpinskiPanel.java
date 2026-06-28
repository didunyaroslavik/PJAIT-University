package recursion_fractals_and_2d_graphics;

import javax.swing.*;
import java.awt.*;

public class SierpinskiPanel extends JPanel {

    // TODO 13 — recursive Sierpiński triangle
    private void drawSierpinski(Graphics g, int[] x, int[] y, int depth) {

        // Base case: fill the smallest triangle
        if (depth == 0) {
            g.fillPolygon(x, y, 3);
            return;
        }

        // Midpoints of each side
        int mx1 = (x[0] + x[1]) / 2,  my1 = (y[0] + y[1]) / 2;
        int mx2 = (x[1] + x[2]) / 2,  my2 = (y[1] + y[2]) / 2;
        int mx3 = (x[0] + x[2]) / 2,  my3 = (y[0] + y[2]) / 2;

        // Recurse into the three corner sub-triangles (middle one is left empty)
        drawSierpinski(g, new int[]{x[0], mx1, mx3}, new int[]{y[0], my1, my3}, depth - 1);
        drawSierpinski(g, new int[]{mx1, x[1], mx2}, new int[]{my1, y[1], my2}, depth - 1);
        drawSierpinski(g, new int[]{mx3, mx2, x[2]}, new int[]{my3, my2, y[2]}, depth - 1);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        int[]  x    = new int[3];
        int[]  y    = new int[3];
        double step = 2 * Math.PI / 3;
        double r    = Math.min(getWidth(), getHeight()) / 2.0 * 0.9;

        for (int i = 0; i < x.length; i++) {
            x[i] = (int) (Math.sin(step * i) * r) + getWidth()  / 2;
            y[i] = (int) (Math.cos(step * i) * r) + getHeight() / 2;
        }

        drawSierpinski(g, x, y, 6);
    }
}
