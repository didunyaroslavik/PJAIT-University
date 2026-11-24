import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public
    class PositionAndColorTest {

    private void testEncodeDecode(int x, int y, Color c) {
        int encoded = PositionAndColor.encode(x, y, c);
        int[] decoded = PositionAndColor.decode(encoded);

        assertEquals(x & 0xFFF, decoded[0], "X mismatch");
        assertEquals(y & 0xFFF, decoded[1], "Y mismatch");
        assertEquals(PositionAndColor.colorToByte(c), decoded[2], "Color byte mismatch");
    }

    private void testColorRoundtrip(Color c) {
        int b = PositionAndColor.colorToByte(c);
        Color c2 = PositionAndColor.byteToColor(b);

        assertTrue(Math.abs(c.getRed() - c2.getRed()) <= 36);
        assertTrue(Math.abs(c.getGreen() - c2.getGreen()) <= 36);
        assertTrue(Math.abs(c.getBlue() - c2.getBlue()) <= 85);
    }

    @Test
    public void testColorToByteBasic() {
        assertEquals(0, PositionAndColor.colorToByte(new Color(0,0,0)));
        assertEquals(255, PositionAndColor.colorToByte(new Color(255,255,255)));
    }

    @Test
    public void testColorRoundtripKnownColors() {
        testColorRoundtrip(Color.BLACK);
        testColorRoundtrip(Color.WHITE);
        testColorRoundtrip(Color.RED);
        testColorRoundtrip(Color.GREEN);
        testColorRoundtrip(Color.BLUE);
        testColorRoundtrip(new Color(128, 200, 50));
        testColorRoundtrip(new Color(15, 180, 240));
    }

    @Test
    public void testByteToColorExactCases() {
        assertEquals(new Color(0,0,0), PositionAndColor.byteToColor(0));
        assertEquals(new Color(255,255,255), PositionAndColor.byteToColor(255));
    }

    @Test
    public void testEncodeDecodeZero() {
        testEncodeDecode(0, 0, Color.BLACK);
    }

    @Test
    public void testEncodeDecodeMax() {
        testEncodeDecode(4095, 4095, Color.WHITE);
    }

    @Test
    public void testEncodeDecodeMixedValues() {
        testEncodeDecode(123, 2048, Color.RED);
        testEncodeDecode(999, 1, Color.GREEN);
        testEncodeDecode(4000, 3000, Color.BLUE);
    }

    @Test
    public void testEncodeDecodeRandomValues() {
        Random rand = new Random();

        for (int i = 0; i < 1000; i++) {
            int x = rand.nextInt(4096);
            int y = rand.nextInt(4096);
            Color c = new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));

            testEncodeDecode(x, y, c);
        }
    }

    @Test
    public void testXMasking() {
        int[] d = PositionAndColor.decode(PositionAndColor.encode(99999, 0, Color.BLACK));
        assertEquals(99999 & 0xFFF, d[0]);
    }

    @Test
    public void testYMasking() {
        int[] d = PositionAndColor.decode(PositionAndColor.encode(0, 99999, Color.BLACK));
        assertEquals(99999 & 0xFFF, d[1]);
    }

    @Test
    public void testColorMasking() {
        Color c = new Color(120, 130, 140);
        int encoded = PositionAndColor.encode(0, 0, c);
        int[] decoded = PositionAndColor.decode(encoded);

        assertEquals(PositionAndColor.colorToByte(c), decoded[2]);
    }
}