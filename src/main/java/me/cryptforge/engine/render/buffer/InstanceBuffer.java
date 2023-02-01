package me.cryptforge.engine.render.buffer;

import me.cryptforge.engine.Engine;
import me.cryptforge.engine.render.Renderer;
import me.cryptforge.engine.render.VertexArrayObject;
import me.cryptforge.engine.render.VertexBufferObject;
import org.joml.Matrix3x2f;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL46.*;

public final class InstanceBuffer implements DrawBuffer {

    private static final int INSTANCE_SIZE = 14;

    private final Renderer renderer;
    private final VertexArrayObject vao;
    private final VertexBufferObject vertexVbo;
    private final VertexBufferObject instanceVbo;
    private final FloatBuffer instanceBuffer;
    private final ByteBuffer indexBuffer;
    private final Matrix3x2f matrix;
    private final int capacity;
    private int count;

    public InstanceBuffer(Renderer renderer, int capacity) {
        this.renderer = renderer;
        this.capacity = capacity;
        vao = new VertexArrayObject();
        vertexVbo = new VertexBufferObject();
        instanceVbo = new VertexBufferObject();
        instanceBuffer = MemoryUtil.memAllocFloat(capacity * INSTANCE_SIZE);
        indexBuffer = MemoryUtil.memAlloc(6);
        matrix = new Matrix3x2f();
    }

    @Override
    public void init() {
        // init vertex attributes
        vao.defineAttribute(0, 4, 4 * Float.BYTES, 0, vertexVbo);

        // Upload vertices
        final float[] vertices = new float[]{
                0, 0, 0, 0,
                0, 1, 0, 1,
                1, 0, 1, 0,
                1, 1, 1, 1
        };
        vertexVbo.uploadData(vertices, GL_STATIC_DRAW);

        // init instance attributes
        vao.defineAttribute(1, 4, INSTANCE_SIZE * Float.BYTES, 0, instanceVbo); // color (vec4)
        vao.defineAttribute(2, 4, INSTANCE_SIZE * Float.BYTES, 4 * Float.BYTES, instanceVbo); // uvs (vec4)

        // model matrix (vec2 vec2 vec2)
        vao.defineAttribute(3, 2, INSTANCE_SIZE * Float.BYTES, 8 * Float.BYTES, instanceVbo);
        vao.defineAttribute(4, 2, INSTANCE_SIZE * Float.BYTES, 10 * Float.BYTES, instanceVbo);
        vao.defineAttribute(5, 2, INSTANCE_SIZE * Float.BYTES, 12 * Float.BYTES, instanceVbo);

        vao.defineDivisor(1, 1);
        vao.defineDivisor(2, 1);
        vao.defineDivisor(3, 1);
        vao.defineDivisor(4, 1);
        vao.defineDivisor(5, 1);

        // set instance vbo buffer size
        final long size = (long) capacity * INSTANCE_SIZE * Float.BYTES;
        instanceVbo.uploadData(size, GL_DYNAMIC_DRAW);

        // set indices
        indexBuffer.put(new byte[]{0, 1, 3, 0, 3, 2});
        indexBuffer.flip();

        vao.bind();
    }

    public InstanceBuffer putInstance(float r, float g, float b, float a, float texWidth, float texHeight, float texX, float texY, Matrix3x2f matrix) {
        if (!hasSpace(1)) {
            renderer.flushBuffer();
        }
        instanceBuffer.put(r).put(g).put(b).put(a).put(texWidth).put(texHeight).put(texX).put(texY);

        Engine.window().projectionMatrix().mul(matrix, this.matrix);
        this.matrix.get(instanceBuffer);
        instanceBuffer.position(instanceBuffer.position() + 6);
        count++;
        return this;
    }

    @Override
    public void flush() {
        if (count > 0) {
            instanceBuffer.flip();

            instanceVbo.uploadSubData(0, instanceBuffer);

            glDrawElementsInstanced(GL_TRIANGLES, indexBuffer, count);

            clear();
        }
    }

    @Override
    public void clear() {
        instanceBuffer.clear();
        count = 0;
    }

    @Override
    public void free() {
        MemoryUtil.memFree(instanceBuffer);
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
