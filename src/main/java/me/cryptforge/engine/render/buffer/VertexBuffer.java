package me.cryptforge.engine.render.buffer;

import me.cryptforge.engine.render.Color;
import me.cryptforge.engine.render.Renderer;
import me.cryptforge.engine.render.VertexArrayObject;
import me.cryptforge.engine.render.VertexBufferObject;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL33.*;

@Deprecated
public final class VertexBuffer implements DrawBuffer {

    private static final int VERTEX_SIZE = 8;

    private final Renderer renderer;
    private final VertexArrayObject vao;
    private final VertexBufferObject vbo;
    private final FloatBuffer buffer;
    private final int capacity;
    private int count;

    public VertexBuffer(Renderer renderer, int capacity) {
        this.renderer = renderer;
        this.capacity = capacity;
        buffer = MemoryUtil.memAllocFloat(capacity * VERTEX_SIZE);
        vao = new VertexArrayObject();
        vbo = new VertexBufferObject();
    }

    @Override
    public void init() {
        vao.defineAttribute(0, 4, VERTEX_SIZE * Float.BYTES, 0, vbo); // coordinates (vec4)
        vao.defineAttribute(1, 4, VERTEX_SIZE * Float.BYTES, 4 * Float.BYTES, vbo); // color (vec4)

        vbo.uploadData((long) capacity * VERTEX_SIZE * Float.BYTES, GL_DYNAMIC_DRAW);
    }

    private VertexBuffer putVertex(float x, float y, float textureX, float textureY, float r, float g, float b, float a) {
        buffer.put(x).put(y).put(textureX).put(textureY).put(r).put(g).put(b).put(a);
        return this;
    }

    public VertexBuffer putRegion(float bottomX, float bottomY, float topX, float topY, float bottomTextureX, float bottomTextureY, float topTextureX, float topTextureY, float r, float g, float b, float a) {
        if (!hasSpace(1)) {
            renderer.flushBuffer();
        }
        putVertex(bottomX, bottomY, bottomTextureX, bottomTextureY, r, g, b, a);
        putVertex(bottomX, topY, bottomTextureX, topTextureY, r, g, b, a);
        putVertex(topX, topY, topTextureX, topTextureY, r, g, b, a);

        putVertex(bottomX, bottomY, bottomTextureX, bottomTextureY, r, g, b, a);
        putVertex(topX, topY, topTextureX, topTextureY, r, g, b, a);
        putVertex(topX, bottomY, topTextureX, bottomTextureY, r, g, b, a);
        count++;

        return this;
    }

    public VertexBuffer putRegion(float bottomX, float bottomY, float topX, float topY, float bottomTextureX, float bottomTextureY, float topTextureX, float topTextureY, Color color) {
        return putRegion(bottomX, bottomY, topX, topY, bottomTextureX, bottomTextureY, topTextureX, topTextureY, color.r(), color.g(), color.b(), color.a());
    }

    @Override
    public void flush() {
        if (count > 0) {
            buffer.flip();

            vbo.bind(GL_ARRAY_BUFFER);
            vbo.uploadSubData(0, buffer);

            vao.bind();
            glDrawArrays(GL_TRIANGLES, 0, count * 6);
            vao.unbind();

            clear();
        }
    }

    @Override
    public void clear() {
        buffer.clear();
        count = 0;
    }

    @Override
    public void free() {
        MemoryUtil.memFree(buffer);
    }

    @Override
    public long capacity() {
        return capacity;
    }

    @Override
    public int count() {
        return count;
    }
}
