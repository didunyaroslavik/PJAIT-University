import java.awt.*;

public
    class PositionAndColor {

    // bits 0-11: x, bits 12-23: y, bits 24-31: color byte
    public static int encode(int x, int y, Color color){
        int result = x & 0xFFF;
        result |= (y & 0xFFF) << 12;
        result |= (colorToByte(color) & 0xFF) << 24;
        return result;
    }

    public static int[] decode(int value) {
        int x = value & 0xFFF;
        int y = (value >> 12) & 0xFFF;
        int colorByte = (value >> 24) & 0xFF;
        return new int[]{x, y, colorByte};
    }

    // RRRGGGBB - 3 bits red, 3 bits green, 2 bits blue
    public static int colorToByte(Color color){
        int r = color.getRed() >> 5;
        int g = color.getGreen() >> 5;
        int b = color.getBlue() >> 6;
        return (r << 5) | (g << 2) | b;
    }

    public static Color byteToColor(int value) {
        int r = (value >> 5) & 0b111;
        int g = (value >> 2) & 0b111;
        int b = value & 0b11;

        int red = r * 255 / 7;
        int green = g * 255 / 7;
        int blue = b * 255 / 3;

        return new Color(red, green, blue);
    }
}
