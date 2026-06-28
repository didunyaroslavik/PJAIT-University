package recursion_fractals_and_2d_graphics;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;

public class KochPanel extends JPanel {

    // Recursion depth of the fractal
    private int depth = 5;

    // ── Inner helper class ────────────────────────────────────────────────────

    private static class Point {
        double x, y;

        Point(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    // ── Koch segment generation ───────────────────────────────────────────────

    /**
     * Recursively generates all outline points for a single Koch-curve segment
     * between p1 and p2 at the given recursion depth.
     */
    private List<Point> kochSegment(Point p1, Point p2, int depth) {
        List<Point> points = new ArrayList<>();

        // Base case: just the two endpoints
        if (depth == 0) {
            points.add(p1);
            points.add(p2);
            return points;
        }

        // a — one third along p1→p2
        Point a = new Point(
            p1.x + (p2.x - p1.x) / 3.0,
            p1.y + (p2.y - p1.y) / 3.0
        );

        // b — two thirds along p1→p2
        Point b = new Point(
            p1.x + 2.0 * (p2.x - p1.x) / 3.0,
            p1.y + 2.0 * (p2.y - p1.y) / 3.0
        );

        // Rotate vector a→b by -60° (clockwise in standard coords) to get
        // the outward-pointing peak of the equilateral triangle bump.
        double angle = Math.PI / 3.0;
        double dx    = b.x - a.x;
        double dy    = b.y - a.y;

        Point peak = new Point(
            a.x + dx * Math.cos(angle) + dy * Math.sin(angle),
            a.y - dx * Math.sin(angle) + dy * Math.cos(angle)
        );

        // Recurse into the four sub-segments
        List<Point> seg1 = kochSegment(p1,   a,    depth - 1);
        List<Point> seg2 = kochSegment(a,    peak, depth - 1);
        List<Point> seg3 = kochSegment(peak, b,    depth - 1);
        List<Point> seg4 = kochSegment(b,    p2,   depth - 1);

        // Concatenate, skipping the duplicate first point of each subsequent segment
        points.addAll(seg1);
        for (int i = 1; i < seg2.size(); i++) points.add(seg2.get(i));
        for (int i = 1; i < seg3.size(); i++) points.add(seg3.get(i));
        for (int i = 1; i < seg4.size(); i++) points.add(seg4.get(i));

        return points;
    }

    // ── Snowflake drawing ─────────────────────────────────────────────────────

    private void drawKochSnowflake(Graphics2D g2d) {
        double centerX     = getWidth()  / 2.0;
        double centerY     = getHeight() / 2.0;
        double r           = Math.min(getWidth(), getHeight()) / 2.0 * 0.8;
        double angleOffset = -Math.PI / 2;          // first vertex points upward
        double step        = 2 * Math.PI / 3;

        // Three vertices of the base equilateral triangle
        Point[] vertices = new Point[3];
        for (int i = 0; i < 3; i++) {
            vertices[i] = new Point(
                centerX + Math.cos(step * i + angleOffset) * r,
                centerY + Math.sin(step * i + angleOffset) * r
            );
        }

        // Generate Koch curve for each of the three sides
        List<Point> side1 = kochSegment(vertices[0], vertices[1], depth);
        List<Point> side2 = kochSegment(vertices[1], vertices[2], depth);
        List<Point> side3 = kochSegment(vertices[2], vertices[0], depth);

        // Merge all points into one outline, skipping duplicate joins
        List<Point> allPoints = new ArrayList<>(side1);
        for (int i = 1; i < side2.size(); i++) allPoints.add(side2.get(i));
        for (int i = 1; i < side3.size(); i++) allPoints.add(side3.get(i));

        // Build and render the closed path
        Path2D.Double path = new Path2D.Double();
        path.moveTo(allPoints.get(0).x, allPoints.get(0).y);
        for (int i = 1; i < allPoints.size(); i++) {
            path.lineTo(allPoints.get(i).x, allPoints.get(i).y);
        }
        path.closePath();

        // Fill
        g2d.setColor(new Color(100, 180, 255));
        g2d.fill(path);

        // Outline
        g2d.setColor(new Color(0, 80, 160));
        g2d.setStroke(new BasicStroke(1.0f));
        g2d.draw(path);
    }

    // ── paintComponent ────────────────────────────────────────────────────────

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        drawKochSnowflake(g2d);
    }
}
