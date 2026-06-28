package layout_managers_in_swing;

import javax.swing.*;
import java.awt.*;

public class L04CardLayoutPanel extends MyPanel {

    private JPanel     cardPanel;
    private CardLayout cardLayout;

    public L04CardLayoutPanel() {
        cardLayout = new CardLayout();
        cardPanel  = new JPanel(cardLayout);
        cardPanel.setBorder(BorderFactory.createTitledBorder("Cards"));

        // Create four named cards (loop 1 → 4)
        for (int i = 1; i <= 4; i++) {
            JPanel card = new JPanel(new BorderLayout());
            card.setBackground(colors[i]);

            JLabel label = new JLabel("Card " + i, JLabel.CENTER);
            label.setFont(new Font("Arial", Font.BOLD, 48));
            card.add(label, BorderLayout.CENTER);

            cardPanel.add(card, "Card" + i);
        }

        // Navigation panel
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton firstBtn = new JButton("\u00ab first");
        JButton prevBtn  = new JButton("< previous");
        JButton nextBtn  = new JButton("next >");
        JButton lastBtn  = new JButton("last \u00bb");

        firstBtn.addActionListener(e -> cardLayout.first(cardPanel));
        prevBtn .addActionListener(e -> cardLayout.previous(cardPanel));
        nextBtn .addActionListener(e -> cardLayout.next(cardPanel));
        lastBtn .addActionListener(e -> cardLayout.last(cardPanel));

        navPanel.add(firstBtn);
        navPanel.add(prevBtn);
        navPanel.add(nextBtn);
        navPanel.add(lastBtn);

        // Drop-down card selector
        JComboBox<String> cardSelector =
            new JComboBox<>(new String[]{"Card1", "Card2", "Card3", "Card4"});

        cardSelector.addActionListener(e -> {
            String selected = (String) cardSelector.getSelectedItem();
            cardLayout.show(cardPanel, selected);
        });

        navPanel.add(new JLabel(" Select Card: "));
        navPanel.add(cardSelector);

        add(cardPanel, BorderLayout.CENTER);
        add(navPanel,  BorderLayout.SOUTH);
    }
}
