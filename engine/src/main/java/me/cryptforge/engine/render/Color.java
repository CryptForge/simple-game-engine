package me.cryptforge.engine.render;

public record Color(float r, float g, float b, float a) {

    public static final Color WHITE = new Color(1, 1, 1, 1);
    public static final Color BLACK = new Color(0, 0, 0, 1);

    public static final Color RED = new Color(1, 0, 0, 1);
    public static final Color GREEN = new Color(0, 1, 0, 1);
    public static final Color BLUE = new Color(0, 0, 1, 1);

    public Color withAlpha(float alpha) {
        return new Color(r,g,b,alpha);
    }

    public Color withRed(float red) {
        return new Color(red,g,b,a);
    }

    public Color withGreen(float green) {
        return new Color(r,green,b,a);
    }

    public Color withBlue(float blue) {
        return new Color(r,g,blue,a);
    }

}
