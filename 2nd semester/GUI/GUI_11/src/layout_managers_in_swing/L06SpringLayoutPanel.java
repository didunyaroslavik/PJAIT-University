package layout_managers_in_swing;

import javax.swing.*;
import java.awt.*;

public class L06SpringLayoutPanel extends MyPanel {

    public L06SpringLayoutPanel() {
        JPanel demo = createDemoPanel();

        SpringLayout springLayout = new SpringLayout();
        demo.setLayout(springLayout);
        demo.setPreferredSize(new Dimension(400, 200));
        demo.setBorder(BorderFactory.createTitledBorder("SpringLayout form"));

        // ── Create components ─────────────────────────────────────────────────
        JLabel      nameLabel = new JLabel("User name:");
        JTextField  nameField = new JTextField(15);
        JLabel      passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField(15);
        JButton     loginBtn  = createStyledButton("Login",  4);
        JButton     cancelBtn = createStyledButton("Cancel", 0);

        demo.add(nameLabel);
        demo.add(nameField);
        demo.add(passLabel);
        demo.add(passField);
        demo.add(loginBtn);
        demo.add(cancelBtn);

        // ── nameLabel: anchored to top-left of demo ───────────────────────────
        springLayout.putConstraint(SpringLayout.WEST,  nameLabel, 10, SpringLayout.WEST,  demo);
        springLayout.putConstraint(SpringLayout.NORTH, nameLabel, 10, SpringLayout.NORTH, demo);

        // ── nameField: right of nameLabel, same top, stretches to right edge ──
        springLayout.putConstraint(SpringLayout.WEST,  nameField, 10, SpringLayout.EAST,  nameLabel);
        springLayout.putConstraint(SpringLayout.NORTH, nameField, 10, SpringLayout.NORTH, demo);
        springLayout.putConstraint(SpringLayout.EAST,  nameField, -10, SpringLayout.EAST, demo);

        // ── passLabel: left-aligned, below nameLabel ──────────────────────────
        springLayout.putConstraint(SpringLayout.WEST,  passLabel, 10,  SpringLayout.WEST,  demo);
        springLayout.putConstraint(SpringLayout.NORTH, passLabel, 15,  SpringLayout.SOUTH, nameLabel);

        // ── passField: same left as nameField, below nameField, same right ────
        springLayout.putConstraint(SpringLayout.WEST,  passField, 0,   SpringLayout.WEST,  nameField);
        springLayout.putConstraint(SpringLayout.NORTH, passField, 10,  SpringLayout.SOUTH, nameField);
        springLayout.putConstraint(SpringLayout.EAST,  passField, -10, SpringLayout.EAST,  demo);

        // ── cancelBtn: pinned to bottom-right ─────────────────────────────────
        springLayout.putConstraint(SpringLayout.EAST,  cancelBtn, -10, SpringLayout.EAST,  demo);
        springLayout.putConstraint(SpringLayout.SOUTH, cancelBtn, -10, SpringLayout.SOUTH, demo);

        // ── loginBtn: left of cancelBtn, same bottom ──────────────────────────
        springLayout.putConstraint(SpringLayout.EAST,  loginBtn, -10, SpringLayout.WEST,  cancelBtn);
        springLayout.putConstraint(SpringLayout.SOUTH, loginBtn, -10, SpringLayout.SOUTH, demo);

        // Wrap in a FlowLayout panel so the demo keeps its preferred size
        JPanel wrapper = new JPanel(new FlowLayout());
        wrapper.add(demo);

        add(wrapper, BorderLayout.CENTER);
    }
}
