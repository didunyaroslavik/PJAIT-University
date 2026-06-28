package layout_managers_in_swing;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public abstract class MyPanel extends JPanel {

    protected Color[] colors = {
        new Color(255, 107, 107),
        new Color(78,  205, 196),
        new Color(255, 230, 109),
        new Color(170, 166, 157),
        new Color(69,  183, 209),
        new Color(255, 165,   0),
        new Color(147, 112, 219),
        new Color(60,  179, 113),
        new Color(255, 182, 193),
        new Color(135, 206, 235)
    };

    public MyPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    protected JButton createStyledButton(String text, int colorIndex) {
        JButton button = new JButton(text);
        button.setBackground(colors[colorIndex % colors.length]);
        button.setForeground(Color.BLACK);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.setOpaque(true);
        return button;
    }

    protected JPanel createDemoPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY, 2),
                "Demo",
                TitledBorder.CENTER,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14)
            ),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        return panel;
    }

    protected JLabel createDescriptionLabel(String text) {
        JLabel label = new JLabel("<html><div style='width:400px;'>" + text + "</div></html>");
        label.setFont(new Font("Arial", Font.PLAIN, 12));
        label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return label;
    }

    protected JPanel createInfoPanel(String title, String description, String code) {
        JPanel infoPanel = new JPanel(new BorderLayout(10, 10));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(51, 51, 51));

        JTextArea descArea = new JTextArea(description);
        descArea.setEditable(false);
        descArea.setWrapStyleWord(true);
        descArea.setLineWrap(true);
        descArea.setFont(new Font("Arial", Font.PLAIN, 12));
        descArea.setBackground(new Color(245, 245, 245));
        descArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextArea codeArea = new JTextArea(code);
        codeArea.setEditable(false);
        codeArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        codeArea.setBackground(new Color(40, 44, 52));
        codeArea.setForeground(new Color(171, 178, 191));
        codeArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane codeScroll = new JScrollPane(codeArea);
        codeScroll.setPreferredSize(new Dimension(300, 150));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(descArea, BorderLayout.CENTER);

        infoPanel.add(topPanel, BorderLayout.NORTH);
        infoPanel.add(codeScroll, BorderLayout.SOUTH);

        return infoPanel;
    }
}
