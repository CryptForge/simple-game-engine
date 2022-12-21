package me.cryptforge.engine.asset;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL33.GL_ALPHA;
import static org.lwjgl.stb.STBTruetype.*;

public class FontLoader extends AssetLoader<Font, Integer> {

    private final Map<String, Font> fonts = new HashMap<>();

    @Override
    protected @Nullable Font get(String id) {
        return fonts.get(id);
    }

    @Override
    protected @NotNull Font load(String id, AssetPathType pathType, String path, Integer fontSize) throws FileNotFoundException {
        final ByteBuffer ttf;

        try (final InputStream stream = pathType.openStream(path)) {
            final byte[] bytes = stream.readAllBytes();
            ttf = MemoryUtil.memAlloc(bytes.length).put(bytes);
            ttf.flip();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        final STBTTFontinfo info = STBTTFontinfo.create();

        if (!stbtt_InitFont(info, ttf)) {
            throw new IllegalStateException("Failed to init font");
        }
        final int ascent, descent, lineGap;

        try (final MemoryStack stack = MemoryStack.stackPush()) {
            final IntBuffer bAscent = stack.mallocInt(1);
            final IntBuffer bDescent = stack.mallocInt(1);
            final IntBuffer bLineGap = stack.mallocInt(1);

            stbtt_GetFontVMetrics(info, bAscent, bDescent, bLineGap);

            ascent = bAscent.get();
            descent = bDescent.get();
            lineGap = bLineGap.get();
        }

        final int bitmapWidth = fontSize * 16;
        final int bitmapHeight = fontSize * 16;

        final STBTTBakedChar.Buffer charData = STBTTBakedChar.malloc(215);
        final ByteBuffer bitmap = MemoryUtil.memAlloc(bitmapWidth * bitmapHeight);
        stbtt_BakeFontBitmap(ttf, fontSize, bitmap, bitmapWidth, bitmapHeight, 32, charData);

        final Texture texture = TextureLoader.loadTexture(bitmap, bitmapWidth, bitmapHeight, GL_ALPHA);

        MemoryUtil.memFree(bitmap);

        return new Font(info, charData, ttf, texture, fontSize, ascent, descent, lineGap, bitmapWidth, bitmapHeight);
    }
}
