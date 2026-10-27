import java.awt.*;
import java.awt.event.*;

public class MyFrame extends Frame {

    private BallPanel ballPanel;
    private Panel buttonPanel;
    private Button addBall;
    private Button shakePanel;

    public MyFrame() {
        ballPanel = new BallPanel();
        addBall = new Button("Add Ball");
        shakePanel = new Button("Shake Balls");
        shakePanel.addActionListener(e -> ballPanel.shake());
        addBall.addActionListener(ballPanel);
        buttonPanel = new Panel();

        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(addBall);
        buttonPanel.add(shakePanel);

        ballPanel.setBackground(Color.YELLOW);
        buttonPanel.setBackground(Color.GREEN);

        setLayout(new BorderLayout());
        add(ballPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setSize(500, 500);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        new MyFrame();
    }

}