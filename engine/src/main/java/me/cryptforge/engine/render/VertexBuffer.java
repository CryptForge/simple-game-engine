package me.cryptforge.engine.render;

import me.cryptforge.engine.asset.Shader;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL33.*;

public final class VertexBuffer implements AutoCloseable {

    private final Renderer renderer;
    private final FloatBuffer buffer;
    private int count;

    public VertexBuffer(Renderer renderer) {
        this.renderer = renderer;
        buffer = MemoryUtil.memAllocFloat(4096);
        count = 0;

        final long size = (long) buffer.capacity() * Float.BYTES;
        renderer.vbo().uploadData(GL_ARRAY_BUFFER, size, GL_DYNAMIC_DRAW);
    }

    public void clear() {
        buffer.clear();
        count = 0;
    }

    public void flush(Shader shader) {
        if (count > 0) {
            final var vao = renderer.vao();
            final var vbo = renderer.vbo();

            buffer.flip();

            if (vao != null) {
                vao.bind();
            } else {
                vbo.bind(GL_ARRAY_BUFFER);
                renderer.initVertexAttributes();
            }
            shader.use();

            vbo.bind(GL_ARRAY_BUFFER);
            vbo.uploadSubData(GL_ARRAY_BUFFER, 0, buffer);

            glDrawArrays(GL_TRIANGLES, 0, count);

            clear();
        }
    }

    public VertexBuffer add(float x, float y, float r, float g, float b, float a, float textureX, float textureY) {
        buffer.put(x).put(y).put(r).put(g).put(b).put(a).put(textureX).put(textureY);
        count++;
        return this;
    }

    public VertexBuffer add(float x, float y, Color color, float textureX, float textureY) {
        buffer.put(x).put(y).put(color.r()).put(color.g()).put(color.b()).put(color.a()).put(textureX).put(textureY);
        count++;
        return this;
    }

    @Override
    public void close() {
        MemoryUtil.memFree(buffer);
    }

    public int getCapacity() {
        return buffer.capacity();
    }
}
