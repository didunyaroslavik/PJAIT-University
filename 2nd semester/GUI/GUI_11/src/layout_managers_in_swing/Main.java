package layout_managers_in_swing;

import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }

    public Main() {
        CardLayout cardLayout = new CardLayout();
        JPanel center = new JPanel(cardLayout);

        center.add(new L00NullLayoutPanel(),    "Null Layout");
        center.add(new L01FlowLayoutPanel(),    "FlowLayout");
        center.add(new L02BorderLayoutPanel(),  "BorderLayout");
        center.add(new L03GridLayoutPanel(),    "GridLayout");
        center.add(new L04CardLayoutPanel(),    "CardLayout");
        center.add(new L05GridBagLayoutPanel(), "GridBagLayout");
        center.add(new L06SpringLayoutPanel(),  "SpringLayout");
        center.add(new L07GroupLayoutPanel(),   "GroupLayout");

        this.add(center, BorderLayout.CENTER);

        JComboBox<String> comboBox = new JComboBox<>(new String[]{
            "Null Layout",
            "FlowLayout",
            "BorderLayout",
            "GridLayout",
            "CardLayout",
            "GridBagLayout",
            "SpringLayout",
            "GroupLayout"
        });

        comboBox.addActionListener(
            e -> cardLayout.show(center, (String) comboBox.getSelectedItem())
        );

        JPanel north = new JPanel();
        north.add(comboBox);

        this.add(north, BorderLayout.PAGE_START);

        this.setSize(600, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }
}
