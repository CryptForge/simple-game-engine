package me.cryptforge.engine.render.buffer;

import me.cryptforge.engine.render.Color;
import me.cryptforge.engine.render.Renderer;
import me.cryptforge.engine.render.VertexArrayObject;
import me.cryptforge.engine.render.VertexBufferObject;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

import static me.cryptforge.engine.util.GLUtils.initAttribute;
import static org.lwjgl.opengl.GL33.*;

public final class InstanceBuffer implements DrawBuffer {

    private final Renderer renderer;
    private final VertexArrayObject vao;
    private final VertexBufferObject vertexVbo;
    private final VertexBufferObject instanceVbo;
    private final FloatBuffer buffer;
    private final int capacity;
    private int count;

    public InstanceBuffer(Renderer renderer, int capacity) {
        this.renderer = renderer;
        this.capacity = capacity;
        vao = new VertexArrayObject();
        vertexVbo = new VertexBufferObject();
        instanceVbo = new VertexBufferObject();
        buffer = MemoryUtil.memAllocFloat(capacity * 20);
    }

    @Override
    public void init() {
        vao.bind();
        vertexVbo.bind(GL_ARRAY_BUFFER);
        // init vertex attributes
        initAttribute(0, 4, 4 * Float.BYTES, 0); // coordinates (vec4)

        // Upload vertices
        final float[] vertices = new float[]{
                0, 0, 0, 0,
                0, 1, 0, 1,
                1, 1, 1, 1,
                0, 0, 0, 0,
                1, 1, 1, 1,
                1, 0, 1, 0
        };
        vertexVbo.uploadData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);


        instanceVbo.bind(GL_ARRAY_BUFFER);
        // init instance attributes
        initAttribute(1, 4, 20 * Float.BYTES, 0); // color (vec4)

        // model matrix (vec4, vec4, vec4, vec4)
        initAttribute(2, 4, 20 * Float.BYTES, 4 * Float.BYTES);
        initAttribute(3, 4, 20 * Float.BYTES, 8 * Float.BYTES);
        initAttribute(4, 4, 20 * Float.BYTES, 12 * Float.BYTES);
        initAttribute(5, 4, 20 * Float.BYTES, 16 * Float.BYTES);

        glVertexAttribDivisor(1, 1);
        glVertexAttribDivisor(2, 1);
        glVertexAttribDivisor(3, 1);
        glVertexAttribDivisor(4, 1);
        glVertexAttribDivisor(5, 1);

        // set instance vbo buffer size
        final long size = (long) capacity * 20L * Float.BYTES;
        instanceVbo.uploadData(GL_ARRAY_BUFFER, size, GL_DYNAMIC_DRAW);
    }

    public InstanceBuffer putInstance(float r, float g, float b, float a, Matrix4f matrix) {
        if (count >= capacity) {
            renderer.flushBuffer();
        }
        buffer.put(r).put(g).put(b).put(a);

        matrix.get(buffer);
        buffer.position(buffer.position() + 16);
        count++;
        return this;
    }

    public InstanceBuffer putInstance(Color color, Matrix4f matrix) {
        return putInstance(color.r(), color.g(), color.b(), color.a(), matrix);
    }

    @Override
    public void flush() {
        if (count > 0) {
            buffer.flip();

            instanceVbo.bind(GL_ARRAY_BUFFER);
            instanceVbo.uploadSubData(GL_ARRAY_BUFFER, 0, buffer);

            vao.bind();
            glDrawArraysInstanced(GL_TRIANGLES, 0, 6, count);
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
    public int capacity() {
        return capacity;
    }

    @Override
    public int count() {
        return count;
    }
}
