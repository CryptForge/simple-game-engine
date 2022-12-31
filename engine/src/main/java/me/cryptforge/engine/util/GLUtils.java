package me.cryptforge.engine.util;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

public final class GLUtils {

    private GLUtils() {}

    public static void initAttribute(int index, int size, int stride, int offset) {
        glVertexAttribPointer(index, size, GL_FLOAT, false, stride, offset);
        glEnableVertexAttribArray(index);
    }

}
