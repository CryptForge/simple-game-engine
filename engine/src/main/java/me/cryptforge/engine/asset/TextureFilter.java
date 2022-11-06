package me.cryptforge.engine.asset;

import static org.lwjgl.opengl.GL33.*;

public enum TextureFilter {
    NEAREST(GL_NEAREST),
    LINEAR(GL_LINEAR),
    LINEAR_MIPMAP_LINEAR(GL_LINEAR_MIPMAP_LINEAR),
    LINEAR_MIPMAP_NEAREST(GL_LINEAR_MIPMAP_NEAREST),
    NEAREST_MIPMAP_LINEAR(GL_NEAREST_MIPMAP_LINEAR),
    NEAREST_MIPMAP_NEAREST(GL_NEAREST_MIPMAP_NEAREST);

    private final int glCode;

    TextureFilter(int glCode) {
        this.glCode = glCode;
    }

    public int getGlCode() {
        return glCode;
    }
}
