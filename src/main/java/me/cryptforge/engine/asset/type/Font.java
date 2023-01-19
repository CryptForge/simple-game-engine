package me.cryptforge.engine.asset.type;

import me.cryptforge.engine.Freeable;
import me.cryptforge.engine.asset.Glyph;
import org.jetbrains.annotations.ApiStatus;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.stb.STBTruetype.*;

public class Font implements Freeable {

    private final STBTTFontinfo info;
    private final STBTTBakedChar.Buffer charData;
    private final Texture texture;
    private final ByteBuffer data;
    private final Map<Integer, Glyph> glyphs = new HashMap<>();
    private final int size;
    private final int ascent;
    private final int descent;
    private final int lineGap;
    private final int bitmapWidth;
    private final int bitmapHeight;

    @ApiStatus.Internal
    public Font(STBTTFontinfo info, STBTTBakedChar.Buffer charData, Texture texture, ByteBuffer data, int size, int ascent, int descent, int lineGap, int bitmapWidth, int bitmapHeight) {
        this.info = info;
        this.charData = charData;
        this.texture = texture;
        this.data = data;
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

    public float getTextWidth(String text) {
        int width = 0;

        for (int i = 0; i < text.length(); i++) {
            final int codepoint = Character.codePointAt(text, i);
            final Glyph glyph = getGlyph(codepoint);

            width += glyph.advance();
        }

        return width * getScale();
    }

    public float getTextHeight() {
        return getAscent() * getScale();
    }

    public float getScale() {
        return stbtt_ScaleForPixelHeight(info, size);
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

    @Override
    public void free() {
        MemoryUtil.memFree(data);
        charData.free();
    }

}
