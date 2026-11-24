import java.awt.*;
import java.awt.event.*;
import java.io.*;

// Part 2, step 3 - saving / loading circles with java.io streams
public
    class MyFrameIO
    extends Frame {

    private static final String FILE_NAME = "circles_io.bin";

    public static void main(String[] args) {
        new MyFrameIO();
    }

    private int[] circles;
    private int circlesCount;

    public MyFrameIO() {
        super("Circles (java.io)");
        this.circles = new int[1000];
        this.circlesCount = 0;

        Panel drawPanel = new Panel(){
            public void paint(Graphics g){
                for(int i = 0; i < circles.length; i++){
                    int[] decoded = PositionAndColor.decode(
                        MyFrameIO.this.circles[i]
                    );
                    g.setColor(PositionAndColor.byteToColor(decoded[2]));
                    g.fillOval(decoded[0]-5, decoded[1]-5, 10, 10);
                }
            }
        };

        drawPanel.addMouseListener(
            new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    MyFrameIO.this.circles[circlesCount++] = PositionAndColor.encode(
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
                    try (FileInputStream fis = new FileInputStream(FILE_NAME)) {
                        MyFrameIO.this.circlesCount = MyFrameIO.this.loadInt(fis);
                        for (int i = 0; i < MyFrameIO.this.circlesCount; i++) {
                            MyFrameIO.this.circles[i] = MyFrameIO.this.loadInt(fis);
                        }
                    } catch (FileNotFoundException ex) {
                        System.err.println("File not found: " + ex.getMessage());
                    } catch (IOException ex) {
                        System.err.println("Error while reading: " + ex.getMessage());
                    }
                    drawPanel.repaint();
                }
            }
        );

        saveButton.addActionListener(
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try (FileOutputStream fos = new FileOutputStream(FILE_NAME)) {
                        MyFrameIO.this.saveInt(fos, MyFrameIO.this.circlesCount);
                        for (int circle : MyFrameIO.this.circles) {
                            MyFrameIO.this.saveInt(fos, circle);
                        }
                    } catch (FileNotFoundException ex) {
                        System.err.println("Cannot open file: " + ex.getMessage());
                    } catch (IOException ex) {
                        System.err.println("Error while writing: " + ex.getMessage());
                    }
                }
            }
        );
    }

    // writes the int as 4 bytes, from the least significant one (little-endian)
    // write(int) only takes the lowest 8 bits of its argument
    private void saveInt(OutputStream os, int value) throws IOException {
        os.write(value);
        os.write(value >> 8);
        os.write(value >> 16);
        os.write(value >> 24);
    }

    // reads 4 bytes and puts them back together in the same order
    private int loadInt(InputStream is) throws IOException {
        int value = is.read();
        value |= is.read() << 8;
        value |= is.read() << 16;
        value |= is.read() << 24;
        return value;
    }
}
