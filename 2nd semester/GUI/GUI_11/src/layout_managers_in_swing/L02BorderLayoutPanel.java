package layout_managers_in_swing;

import javax.swing.*;
import java.awt.*;

public class L02BorderLayoutPanel extends MyPanel {

    public L02BorderLayoutPanel() {
        JPanel demo = createDemoPanel();
        demo.setLayout(new BorderLayout(10, 10));

        JButton north = createStyledButton("NORTH - head", 0);
        north.setPreferredSize(new Dimension(0, 50));

        JButton south = createStyledButton("SOUTH - bottom", 1);
        south.setPreferredSize(new Dimension(0, 50));

        // Vertical text for EAST and WEST via HTML line breaks
        JButton east = createStyledButton("<html>E<br>A<br>S<br>T</html>", 2);
        east.setPreferredSize(new Dimension(80, 0));

        JButton west = createStyledButton("<html>W<br>E<br>S<br>T</html>", 3);
        west.setPreferredSize(new Dimension(80, 0));

        JButton center = createStyledButton("<html>CENTER</html>", 4);

        demo.add(north,  BorderLayout.NORTH);
        demo.add(south,  BorderLayout.SOUTH);
        demo.add(east,   BorderLayout.EAST);
        demo.add(west,   BorderLayout.WEST);
        demo.add(center, BorderLayout.CENTER);

        add(demo, BorderLayout.CENTER);
    }
}
