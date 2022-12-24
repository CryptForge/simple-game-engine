package me.cryptforge.engine.asset;

import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.stb.STBTruetype.*;

public class Font {

    public static final boolean KERNING = true;

    private final STBTTFontinfo info;
    private final STBTTBakedChar.Buffer charData;
    private final Texture texture;
    private final Map<Integer, Glyph> glyphs = new HashMap<>();
    private final int size;
    private final int ascent;
    private final int descent;
    private final int lineGap;
    private final int bitmapWidth;
    private final int bitmapHeight;

    public Font(STBTTFontinfo info, STBTTBakedChar.Buffer charData, Texture texture, int size, int ascent, int descent, int lineGap, int bitmapWidth, int bitmapHeight) {
        this.info = info;
        this.charData = charData;
        this.texture = texture;
        this.size = size;
        this.ascent = ascent;
        this.descent = descent;
        this.lineGap = lineGap;
        this.bitmapWidth = bitmapWidth;
        this.bitmapHeight = bitmapHeight;

        try (final MemoryStack stack = MemoryStack.stackPush()) {
            final IntBuffer pAdvance = stack.mallocInt(1);
            final IntBuffer pLeftBearing = stack.mallocInt(1);

            for (int i = 0; i < 128; i++) {
                stbtt_GetCodepointHMetrics(info, i, pAdvance, pLeftBearing);
                glyphs.put(i, new Glyph(i, pAdvance.get(0), pLeftBearing.get(0)));
            }

        }
    }

    public int getBitmapWidth() {
        return bitmapWidth;
    }

    public int getBitmapHeight() {
        return bitmapHeight;
    }

    public STBTTFontinfo getInfo() {
        return info;
    }

    public STBTTBakedChar.Buffer getCharData() {
        return charData;
    }

    public Texture getTexture() {
        return texture;
    }

    public int getSize() {
        return size;
    }

    public int getAscent() {
        return ascent;
    }

    public int getDescent() {
        return descent;
    }

    public int getLineGap() {
        return lineGap;
    }

    public Glyph getGlyph(int codepoint) {
        return glyphs.get(codepoint);
    }

    public static int getCodePoint(String text, int to, int i, IntBuffer cpOut) {
        char c1 = text.charAt(i);
        if (Character.isHighSurrogate(c1) && i + 1 < to) {
            char c2 = text.charAt(i + 1);
            if (Character.isLowSurrogate(c2)) {
                cpOut.put(0, Character.toCodePoint(c1, c2));
                return 2;
            }
        }
        cpOut.put(0, c1);
        return 1;
    }

    public float getWidth(String text, int from, int to) {
        int width = 0;

        try (final MemoryStack stack = MemoryStack.stackPush()) {
            final IntBuffer pCodePoint = stack.mallocInt(1);
            final IntBuffer pAdvancedWidth = stack.mallocInt(1);
            final IntBuffer pLeftSideBearing = stack.mallocInt(1);

            int i = from;
            while (i < to) {
                i += getCodePoint(text, to, i, pCodePoint);
                final int codePoint = pCodePoint.get(0);

                stbtt_GetCodepointHMetrics(info, codePoint, pAdvancedWidth, pLeftSideBearing);
                width += pAdvancedWidth.get(0);

                if (Font.KERNING && i < to) {
                    getCodePoint(text, to, i, pCodePoint);
                    width += stbtt_GetCodepointKernAdvance(info, codePoint, pCodePoint.get(0));
                }

            }
        }
        return width * stbtt_ScaleForPixelHeight(info, size);
    }
//
//    public int getHeight(String text) {
//        int height = 0;
//        int lineHeight = 0;
//        for(int i = 0; i < text.length(); i++) {
//            final char c = text.charAt(i);
//            if (c == '\n') {
//                height += lineHeight;
//                lineHeight = 0;
//                continue;
//            }
//            if (c == '\r') {
//                continue;
//            }
//            final Glyph glyph = glyphs.get(c);
//            lineHeight = Math.max(lineHeight,glyph.getHeight());
//        }
//        height += lineHeight;
//        return height;
//    }
//
//    public int getFontHeight() {
//        return fontHeight;
//    }
//
//    public Glyph getGlyph(char c) {
//        return glyphs.get(c);
//    }
//
//    public Texture getBitmap() {
//        return bitmap;
//    }
}
