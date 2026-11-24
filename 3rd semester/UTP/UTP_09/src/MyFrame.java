import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

// Part 2, step 4 - saving / loading circles with java.nio (FileChannel + ByteBuffer)
// The java.io version (step 3) is in MyFrameIO.java
public
    class MyFrame
    extends Frame {

    private static final Path FILE_PATH = Path.of("circles_nio.bin");

    public static void main(String[] args) {
        new MyFrame();
    }

    private int[] circles;
    private int circlesCount;

    public MyFrame() {
        super("Circles (java.nio)");
        this.circles = new int[1000];
        this.circlesCount = 0;

        Panel drawPanel = new Panel(){
            public void paint(Graphics g){
                for(int i = 0; i < circles.length; i++){
                    int[] decoded = PositionAndColor.decode(
                        MyFrame.this.circles[i]
                    );
                    g.setColor(PositionAndColor.byteToColor(decoded[2]));
                    g.fillOval(decoded[0]-5, decoded[1]-5, 10, 10);
                }
            }
        };

        drawPanel.addMouseListener(
            new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    // after Load the array has exactly circlesCount elements,
                    // so it has to grow before a new circle can be added
                    if (circlesCount == circles.length) {
                        circles = Arrays.copyOf(circles, Math.max(1, circles.length * 2));
                    }
                    MyFrame.this.circles[circlesCount++] = PositionAndColor.encode(
                        e.getX(),
                        e.getY(),
                        new Color(
                            (int)(Math.random()*255),
                            (int)(Math.random()*255),
                            (int)(Math.random()*255)
                        )
                    );
                    drawPanel.repaint();
                }
            }
        );

        Panel actionPanel = new Panel();

        Button loadButton = new Button("Load");
        Button saveButton = new Button("Save");

        actionPanel.setLayout(
            new FlowLayout()
        );

        actionPanel.add(loadButton);
        actionPanel.add(saveButton);

        this.setLayout(new BorderLayout());
        this.add(actionPanel, BorderLayout.SOUTH);
        this.add(drawPanel, BorderLayout.CENTER);

        this.setSize(500, 500);
        this.setVisible(true);

        this.addWindowListener(
            new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                }
            }
        );

        loadButton.addActionListener(
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try (FileChannel channel = FileChannel.open(FILE_PATH, StandardOpenOption.READ)) {
                        long size = channel.size();
                        ByteBuffer buffer = ByteBuffer.allocate((int) size);

                        channel.read(buffer);
                        buffer.flip();

                        MyFrame.this.circlesCount = buffer.getInt();
                        MyFrame.this.circles = new int[MyFrame.this.circlesCount];
                        for (int i = 0; i < MyFrame.this.circlesCount; i++) {
                            MyFrame.this.circles[i] = buffer.getInt();
                        }
                    } catch (IOException ex) {
                        System.err.println("Error while reading: " + ex);
                    }
                    drawPanel.repaint();
                }
            }
        );

        saveButton.addActionListener(
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try (FileChannel channel = FileChannel.open(
                            FILE_PATH,
                            StandardOpenOption.CREATE,
                            StandardOpenOption.WRITE,
                            StandardOpenOption.TRUNCATE_EXISTING)) {

                        int bufferSize = Integer.BYTES * (1 + MyFrame.this.circles.length);
                        ByteBuffer buffer = ByteBuffer.allocate(bufferSize);

                        buffer.putInt(MyFrame.this.circlesCount);
                        for (int circle : MyFrame.this.circles) {
                            buffer.putInt(circle);
                        }

                        buffer.flip();
                        channel.write(buffer);
                    } catch (IOException ex) {
                        System.err.println("Error while writing: " + ex);
                    }
                }
            }
        );
    }
}
