package swing_look_and_feel;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class LAFFrame extends JFrame {

    // ── Constants used to clamp random dialog placement ──────────────────────
    private static final int DIALOG_WIDTH  = 400;
    private static final int DIALOG_HEIGHT = 200;

    // ── Instance fields ───────────────────────────────────────────────────────
    private final Random random      = new Random();
    private       int    dialogCount = 0;

    // TODO 02 – Map from display name → fully-qualified class name
    private final Map<String, String> lafMap = new HashMap<>();

    // ── Entry point ───────────────────────────────────────────────────────────
    public static void main(String[] args) {
        // TODO 01 – Apply system LAF then launch the GUI on the EDT
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // If the system LAF is unavailable the default LAF is kept
        }

        SwingUtilities.invokeLater(() -> new LAFFrame().setVisible(true));
    }

    // ── Constructor ───────────────────────────────────────────────────────────
    public LAFFrame() {
        setTitle("JOptionPane & ComboBox L&F");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLayout(new BorderLayout(10, 10));

        // TODO 03 – Discover installed LAFs and build the name→className map
        UIManager.LookAndFeelInfo[] lafs = UIManager.getInstalledLookAndFeels();
        String[] lafNames = new String[lafs.length];
        for (int i = 0; i < lafs.length; i++) {
            lafNames[i] = lafs[i].getName();
            lafMap.put(lafs[i].getName(), lafs[i].getClassName());
        }

        // ── Main panel ────────────────────────────────────────────────────────
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JComboBox<String> lafComboBox = new JComboBox<>(lafNames);
        lafComboBox.setMaximumSize(new Dimension(300, 30));
        lafComboBox.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Pre-select the currently active LAF
        String currentLaFName = UIManager.getLookAndFeel().getName();
        lafComboBox.setSelectedItem(currentLaFName);

        // TODO 05 – React to the user picking a different LAF
        lafComboBox.addActionListener(e -> {
            String selectedName = (String) lafComboBox.getSelectedItem();
            String className    = lafMap.get(selectedName);
            applyLookAndFeel(className);
        });

        JButton showDialogButton = new JButton("Pokaż Dialog");
        showDialogButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // TODO 07 – Wire the button to the jumping-dialog method
        showDialogButton.addActionListener(e -> showJumpingDialog());

        mainPanel.add(new JLabel("Select Look & Feel:"));
        mainPanel.add(lafComboBox);
        mainPanel.add(showDialogButton);

        add(mainPanel, BorderLayout.CENTER);
        setLocationRelativeTo(null);
    }

    // TODO 04 – Switch the active LAF and refresh all components
    private void applyLookAndFeel(String className) {
        try {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not apply Look & Feel:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // TODO 06 – Show a dialog at a random screen position; offer to repeat
    private void showJumpingDialog() {
        dialogCount++;

        // Determine a random position that keeps the dialog on-screen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int maxX = (int) screenSize.getWidth()  - DIALOG_WIDTH;
        int maxY = (int) screenSize.getHeight() - DIALOG_HEIGHT;
        int x    = random.nextInt(Math.max(1, maxX));
        int y    = random.nextInt(Math.max(1, maxY));

        // Build the informational message
        String message = String.format(
                "Dialog number : %d%n" +
                "Position      : (%d, %d)%n" +
                "Look & Feel   : %s",
                dialogCount, x, y,
                UIManager.getLookAndFeel().getName()
        );

        // Button labels – the first one triggers another dialog
        String[] options = { "Show Another Dialog", "Close" };

        JOptionPane optionPane = new JOptionPane(
                message,
                JOptionPane.INFORMATION_MESSAGE,
                JOptionPane.DEFAULT_OPTION,
                null,       // no custom icon
                options,
                options[0]  // default button
        );

        JDialog dialog = optionPane.createDialog(this, "Dialog #" + dialogCount);
        dialog.setLocation(x, y);
        dialog.setVisible(true);  // blocks until the dialog is closed

        // If the user clicked "Show Another Dialog", recurse via invokeLater
        Object selectedValue = optionPane.getValue();
        if (options[0].equals(selectedValue)) {
            SwingUtilities.invokeLater(this::showJumpingDialog);
        }
    }
}
