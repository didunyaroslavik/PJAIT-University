package layout_managers_in_swing;

import javax.swing.*;
import java.awt.*;

public class L07GroupLayoutPanel extends MyPanel {

    public L07GroupLayoutPanel() {
        JPanel demo = createDemoPanel();
        demo.setBorder(BorderFactory.createTitledBorder("GroupLayout form"));

        GroupLayout layout = new GroupLayout(demo);
        demo.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        // ── Labels ────────────────────────────────────────────────────────────
        JLabel l1 = new JLabel("Name:");
        JLabel l2 = new JLabel("Surname:");
        JLabel l3 = new JLabel("Email:");
        JLabel l4 = new JLabel("Phone:");

        // ── Text fields ───────────────────────────────────────────────────────
        JTextField f1 = new JTextField(20);
        JTextField f2 = new JTextField(20);
        JTextField f3 = new JTextField(20);
        JTextField f4 = new JTextField(20);

        // ── Buttons ───────────────────────────────────────────────────────────
        JButton submit = createStyledButton("Save", 4);
        JButton clear  = createStyledButton("Load", 1);

        // ── Horizontal layout: label column | field + button column ───────────
        layout.setHorizontalGroup(
            layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                    .addComponent(l1)
                    .addComponent(l2)
                    .addComponent(l3)
                    .addComponent(l4))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(f1)
                    .addComponent(f2)
                    .addComponent(f3)
                    .addComponent(f4)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(submit)
                        .addComponent(clear)))
        );

        // ── Vertical layout: one baseline-aligned row per field + button row ──
        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(l1).addComponent(f1))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(l2).addComponent(f2))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(l3).addComponent(f3))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(l4).addComponent(f4))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(submit).addComponent(clear))
        );

        // Wrap so the form takes only its natural size
        JPanel wrapper = new JPanel(new FlowLayout());
        wrapper.add(demo);

        add(wrapper, BorderLayout.CENTER);
    }
}
