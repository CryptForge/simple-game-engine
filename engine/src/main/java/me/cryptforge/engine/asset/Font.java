package me.cryptforge.engine.asset;

import java.util.HashMap;
import java.util.Map;

public class Font {

    private final Map<Character, Glyph> glyphs;
    private final Texture bitmap;
    private final int fontHeight;

    public Font(Texture bitmap, Map<Character, Glyph> glyphs, int fontHeight) {
        this.bitmap = bitmap;
        this.glyphs = glyphs;
        this.fontHeight = fontHeight;
    }

    public int getWidth(String text) {
        int width = 0;
        int lineWidth = 0;
        for(int i = 0; i < text.length(); i++) {
            final char c = text.charAt(i);
            if(c == '\n') {
                width = Math.max(width,lineWidth);
                lineWidth = 0;
                continue;
            }
            if(c == '\r') {
                continue;
            }
            final Glyph glyph = glyphs.get(c);
            lineWidth += glyph.getWidth();
        }
        width = Math.max(width,lineWidth);
        return width;
    }

    public int getHeight(String text) {
        int height = 0;
        int lineHeight = 0;
        for(int i = 0; i < text.length(); i++) {
            final char c = text.charAt(i);
            if (c == '\n') {
                height += lineHeight;
                lineHeight = 0;
                continue;
            }
            if (c == '\r') {
                continue;
            }
            final Glyph glyph = glyphs.get(c);
            lineHeight = Math.max(lineHeight,glyph.getHeight());
        }
        height += lineHeight;
        return height;
    }

    public int getFontHeight() {
        return fontHeight;
    }

    public Glyph getGlyph(char c) {
        return glyphs.get(c);
    }

    public Texture getBitmap() {
        return bitmap;
    }
}
