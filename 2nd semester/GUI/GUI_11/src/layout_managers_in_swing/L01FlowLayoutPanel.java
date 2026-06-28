package layout_managers_in_swing;

import javax.swing.*;
import java.awt.*;

public class L01FlowLayoutPanel extends MyPanel {

    public L01FlowLayoutPanel() {
        JPanel demoContainer = new JPanel(new GridLayout(3, 1, 5, 5));

        // Left-aligned flow
        JPanel leftFlow = createDemoPanel();
        leftFlow.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        leftFlow.setBorder(BorderFactory.createTitledBorder("FlowLayout.LEFT"));
        for (int i = 1; i <= 6; i++) {
            leftFlow.add(createStyledButton("Button " + i, i));
        }

        // Center-aligned flow
        JPanel centerFlow = createDemoPanel();
        centerFlow.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        centerFlow.setBorder(BorderFactory.createTitledBorder("FlowLayout.CENTER"));
        for (int i = 1; i <= 6; i++) {
            centerFlow.add(createStyledButton("Btn " + i, i + 2));
        }

        // Right-aligned flow
        JPanel rightFlow = createDemoPanel();
        rightFlow.setLayout(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        rightFlow.setBorder(BorderFactory.createTitledBorder("FlowLayout.RIGHT"));
        for (int i = 1; i <= 6; i++) {
            rightFlow.add(createStyledButton("B" + i, i + 4));
        }

        demoContainer.add(leftFlow);
        demoContainer.add(centerFlow);
        demoContainer.add(rightFlow);

        add(demoContainer, BorderLayout.CENTER);
    }
}
