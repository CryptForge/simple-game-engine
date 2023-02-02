package me.cryptforge.engine.render;

public record Color(float r, float g, float b, float a) {

    public static final Color WHITE = new Color(1f, 1f, 1f, 1);
    public static final Color BLACK = new Color(0f, 0f, 0f, 1);

    public static final Color RED = new Color(1f, 0f, 0f, 1);
    public static final Color GREEN = new Color(0f, 1f, 0f, 1);
    public static final Color BLUE = new Color(0f, 0f, 1f, 1);

    public Color withAlpha(float alpha) {
        return new Color(r, g, b, alpha);
    }

    public Color withRed(float red) {
        return new Color(red, g, b, a);
    }

    public Color withGreen(float green) {
        return new Color(r, green, b, a);
    }

    public Color withBlue(float blue) {
        return new Color(r, g, blue, a);
    }

    public static Color rgba(int r, int g, int b, float a) {
        return new Color(r / 255f, g / 255f, b / 255f, a);
    }

    public static Color rgb(int r, int g, int b) {
        return rgba(r, g, b, 1f);
    }

    public static Color hex(int hex, float alpha) {
        return new Color(
                (hex & 0xFF0000) >> 16,
                (hex & 0xFF00) >> 8,
                (hex & 0xFF),
                alpha
        );
    }

}
