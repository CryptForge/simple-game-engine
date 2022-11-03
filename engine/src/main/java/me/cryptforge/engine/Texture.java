package me.cryptforge.engine;

import org.jetbrains.annotations.ApiStatus;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load;

@ApiStatus.AvailableSince("1.0")
public class Texture {

    private final int id;

    /**
     * Creates and loads a texture from a file
     * @param file Relative path to image file
     */
    public Texture(String file) {
        id = glGenTextures();

        final int width;
        final int height;
        final ByteBuffer data;
        try (final MemoryStack stack = MemoryStack.stackPush()) {
            final IntBuffer bWidth = stack.mallocInt(1);
            final IntBuffer bHeight = stack.mallocInt(1);
            final IntBuffer bChannelCount = stack.mallocInt(1);

            data = stbi_load(file, bWidth, bHeight, bChannelCount, 3);
            if (data == null) {
                throw new IllegalArgumentException("File \"" + file + "\" not found");
            }

            width = bWidth.get(0);
            height = bHeight.get(0);
        }

        glBindTexture(GL_TEXTURE_2D, id);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, data);
        glGenerateMipmap(GL_TEXTURE_2D);

        stbi_image_free(data);
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, id);
    }

}
