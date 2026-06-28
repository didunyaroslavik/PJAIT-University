package layout_managers_in_swing;

import javax.swing.*;
import java.awt.*;

public class L00NullLayoutPanel extends JPanel {

    public L00NullLayoutPanel() {
        setLayout(null);
        setPreferredSize(new Dimension(500, 300));
        setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.RED, 2),
            "Null Layout - absolute positioning (NOT RECOMMENDED!)"
        ));
        setBackground(new Color(255, 245, 245));

        JButton btn1 = new JButton("Button 1");
        btn1.setBounds(20, 30, 100, 40);

        JButton btn2 = new JButton("Button 2");
        btn2.setBounds(150, 80, 120, 35);

        JButton btn3 = new JButton("Button 3");
        btn3.setBounds(50, 150, 80, 80);

        JLabel label = new JLabel("Label at position (300, 50)");
        label.setBounds(300, 50, 180, 25);
        label.setOpaque(true);
        label.setBackground(Color.RED);

        JTextField field = new JTextField("Text field");
        field.setBounds(280, 150, 150, 30);

        JLabel warning = new JLabel("bad idea");
        warning.setBounds(250, 200, 230, 60);
        warning.setOpaque(true);
        warning.setBackground(new Color(255, 200, 200));
        warning.setBorder(BorderFactory.createLineBorder(Color.RED));

        add(btn1);
        add(btn2);
        add(btn3);
        add(label);
        add(field);
        add(warning);
    }
}
