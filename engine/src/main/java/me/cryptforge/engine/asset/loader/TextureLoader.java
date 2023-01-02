package me.cryptforge.engine.asset.loader;

import me.cryptforge.engine.asset.Asset;
import me.cryptforge.engine.asset.TextureSettings;
import me.cryptforge.engine.asset.type.Texture;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;

@ApiStatus.Internal
public final class TextureLoader extends AssetLoader<Texture, TextureSettings> {

    @Override
    public @NotNull Texture load(Asset asset, TextureSettings settings) throws FileNotFoundException {
        final int textureId = glGenTextures();

        final int desiredChannels = settings.hasAlpha() ? 4 : 3;
        final int internalFormat = settings.hasAlpha() ? GL_RGBA : GL_RGB;

        final int width;
        final int height;
        final int channelCount;
        final ByteBuffer data;
        try (final MemoryStack stack = MemoryStack.stackPush()) {
            final IntBuffer bWidth = stack.mallocInt(1);
            final IntBuffer bHeight = stack.mallocInt(1);
            final IntBuffer bChannelCount = stack.mallocInt(1);

            try (final InputStream stream = asset.openStream()) {
                final byte[] bytes = stream.readAllBytes();

                final ByteBuffer rawData = MemoryUtil.memAlloc(bytes.length).put(bytes);
                rawData.flip();
                data = stbi_load_from_memory(rawData, bWidth, bHeight, bChannelCount, desiredChannels);
                MemoryUtil.memFree(rawData);
            } catch (FileNotFoundException e) {
                throw e;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            if (data == null) {
                throw new RuntimeException("Something has gone wrong while loading a texture from memory");
            }

            width = bWidth.get(0);
            height = bHeight.get(0);
            channelCount = bChannelCount.get(0);
        }

        glBindTexture(GL_TEXTURE_2D, textureId);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, settings.downFilter().getGlCode());
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, settings.upFilter().getGlCode());

        glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, width, height, 0, internalFormat, GL_UNSIGNED_BYTE, data);
        if (settings.generateMipmap()) {
            glGenerateMipmap(GL_TEXTURE_2D);
        }

        stbi_image_free(data);

        return new Texture(textureId, width, height, channelCount);
    }
}
