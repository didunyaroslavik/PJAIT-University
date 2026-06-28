package layout_managers_in_swing;

import javax.swing.*;
import java.awt.*;

public class L05GridBagLayoutPanel extends MyPanel {

    public L05GridBagLayoutPanel() {
        JPanel demo = createDemoPanel();
        demo.setLayout(new GridBagLayout());
        demo.setBorder(BorderFactory.createTitledBorder("GridBagLayout form"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // ── Row 0: form title spanning all three columns ──────────────────────
        gbc.gridx     = 0;
        gbc.gridy     = 0;
        gbc.gridwidth = 3;
        gbc.fill      = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Registration form:", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setOpaque(true);
        title.setBackground(colors[0]);
        title.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        demo.add(title, gbc);

        // ── Row 1: name label + text field ───────────────────────────────────
        gbc.gridwidth = 1;
        gbc.gridy     = 1;
        gbc.gridx     = 0;
        gbc.fill      = GridBagConstraints.NONE;
        gbc.anchor    = GridBagConstraints.EAST;
        demo.add(new JLabel("Name:"), gbc);

        gbc.gridx     = 1;
        gbc.gridwidth = 2;
        gbc.fill      = GridBagConstraints.HORIZONTAL;
        gbc.weightx   = 1;
        demo.add(new JTextField(20), gbc);

        // ── Row 2: email label + text field ──────────────────────────────────
        gbc.gridwidth = 1;
        gbc.weightx   = 0;
        gbc.gridy     = 2;
        gbc.gridx     = 0;
        gbc.fill      = GridBagConstraints.NONE;
        demo.add(new JLabel("Email:"), gbc);

        gbc.gridx     = 1;
        gbc.gridwidth = 2;
        gbc.fill      = GridBagConstraints.HORIZONTAL;
        gbc.weightx   = 1;
        demo.add(new JTextField(20), gbc);

        // ── Row 3: message label + scrollable text area ───────────────────────
        gbc.gridwidth = 1;
        gbc.weightx   = 0;
        gbc.gridy     = 3;
        gbc.gridx     = 0;
        gbc.fill      = GridBagConstraints.NONE;
        gbc.anchor    = GridBagConstraints.NORTHEAST;
        demo.add(new JLabel("Message"), gbc);

        gbc.gridx     = 1;
        gbc.gridwidth = 2;
        gbc.fill      = GridBagConstraints.BOTH;
        gbc.weightx   = 1;
        gbc.weighty   = 1;
        JTextArea messageArea = new JTextArea(5, 20);
        demo.add(new JScrollPane(messageArea), gbc);

        // ── Row 4: Send / Cancel buttons ─────────────────────────────────────
        gbc.weighty   = 0;
        gbc.gridy     = 4;
        gbc.gridx     = 1;
        gbc.gridwidth = 1;
        gbc.fill      = GridBagConstraints.NONE;
        demo.add(createStyledButton("Send", 4), gbc);

        gbc.gridx = 2;
        demo.add(createStyledButton("Cancel", 1), gbc);

        add(demo, BorderLayout.CENTER);
    }
}
