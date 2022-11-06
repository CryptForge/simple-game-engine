package me.cryptforge.engine.asset;

import static org.lwjgl.opengl.GL33.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL33.glBindTexture;


public class Texture {

    private final int id;
    private final int width;
    private final int height;
    private final int channelCount;

    protected Texture(int id, int width, int height, int channelCount) {
        this.id = id;
        this.width = width;
        this.height = height;
        this.channelCount = channelCount;
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, id);
    }

    public int getId() {
        return id;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getChannelCount() {
        return channelCount;
    }
}
