package me.cryptforge.engine.asset.loader;

import me.cryptforge.engine.asset.Asset;
import me.cryptforge.engine.asset.type.Font;
import me.cryptforge.engine.asset.type.Texture;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.stb.STBTruetype.*;

@ApiStatus.Internal
public class FontLoader extends AssetLoader<Font, Integer> {

    @Override
    public @NotNull Font load(Asset asset, Integer fontSize) throws FileNotFoundException {
        final ByteBuffer data;

        try (final InputStream stream = asset.openStream()) {
            final byte[] bytes = stream.readAllBytes();
            data = MemoryUtil.memAlloc(bytes.length).put(bytes);
            data.flip();
        } catch (FileNotFoundException e) {
            throw e;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        final STBTTFontinfo info = STBTTFontinfo.create();

        if (!stbtt_InitFont(info, data)) {
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
        stbtt_BakeFontBitmap(data, fontSize, bitmap, bitmapWidth, bitmapHeight, 32, charData);

        final Texture texture = createBitmapTexture(bitmap, bitmapWidth, bitmapHeight);

        MemoryUtil.memFree(bitmap);

        return new Font(info, charData, texture,data, fontSize, ascent, descent, lineGap, bitmapWidth, bitmapHeight);
    }

    private static Texture createBitmapTexture(ByteBuffer buffer, int width, int height) {
        final int textureId = glGenTextures();

        glBindTexture(GL_TEXTURE_2D, textureId);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RED, width, height, 0, GL_RED, GL_UNSIGNED_BYTE, buffer);

        glBindTexture(GL_TEXTURE_2D,0);

        return new Texture(textureId, width, height, 1);
    }
}
