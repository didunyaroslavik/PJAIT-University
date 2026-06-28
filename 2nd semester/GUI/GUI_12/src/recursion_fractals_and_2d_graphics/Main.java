package recursion_fractals_and_2d_graphics;

import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }

    public Main() {
        CardLayout cardLayout = new CardLayout();
        JPanel center = new JPanel(cardLayout);

        center.add(new TreePanel(),       "TreePanel");
        center.add(new SierpinskiPanel(), "SierpinskiPanel");
        center.add(new KochPanel(),       "KochPanel");

        this.add(center, BorderLayout.CENTER);

        JComboBox<String> comboBox = new JComboBox<>(
            new String[]{"TreePanel", "SierpinskiPanel", "KochPanel"}
        );
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
