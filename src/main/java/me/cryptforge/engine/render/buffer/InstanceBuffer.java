package me.cryptforge.engine.render.buffer;

import me.cryptforge.engine.Engine;
import me.cryptforge.engine.render.GLFence;
import me.cryptforge.engine.render.Renderer;
import me.cryptforge.engine.render.VertexArrayObject;
import me.cryptforge.engine.render.VertexBufferObject;
import org.joml.Matrix3x2f;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL46.*;

public final class InstanceBuffer implements DrawBuffer {

    private static final int INSTANCE_SIZE = 14;

    private final Renderer renderer;
    private final VertexArrayObject vao;
    private final VertexBufferObject vertexVbo;
    private final VertexBufferObject instanceVbo;
    private final ByteBuffer instanceBuffer;
    private final ByteBuffer indexBuffer;
    private final Matrix3x2f matrix;
    private final long capacity;
    private GLFence[] fences;
    private int count;
    private int bufferOffset = 0;

    public InstanceBuffer(Renderer renderer, long capacity) {
        this.renderer = renderer;
        this.capacity = capacity;
        vao = new VertexArrayObject();
        vertexVbo = new VertexBufferObject();
        instanceVbo = new VertexBufferObject();
        fences = new GLFence[]{
                new GLFence(),
                new GLFence(),
                new GLFence()
        };

        final int flags = GL_MAP_WRITE_BIT | GL_MAP_PERSISTENT_BIT | GL_MAP_COHERENT_BIT;
        instanceVbo.bufferStorage(totalBufferSize(), flags);
        instanceBuffer = instanceVbo.mapRange(0, totalBufferSize(), flags);

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

        // set indices
        indexBuffer.put(new byte[]{0, 1, 3, 0, 3, 2});
        indexBuffer.flip();

        vao.bind();
    }

    public InstanceBuffer putInstance(float r, float g, float b, float a, float texWidth, float texHeight, float texX, float texY, Matrix3x2f matrix) {
        if (!hasSpace(1)) {
            renderer.flushBuffer();
        }
        final int index = (int) getBufferIndex();
        instanceBuffer.putFloat(index, r)
                      .putFloat(index + floatBytes(1), g)
                      .putFloat(index + floatBytes(2), b)
                      .putFloat(index + floatBytes(3), a)
                      .putFloat(index + floatBytes(4), texWidth)
                      .putFloat(index + floatBytes(5), texHeight)
                      .putFloat(index + floatBytes(6), texX)
                      .putFloat(index + floatBytes(7), texY);

        Engine.window().projectionMatrix().mul(matrix, this.matrix);
        this.matrix.get(index + floatBytes(8), instanceBuffer);
        count++;
        return this;
    }

    @Override
    public void flush() {
        if (count > 0) {
            fences[bufferOffset].lockBuffer();

            final int baseInstance = (int) ((bufferOffset * bufferSize()) / (INSTANCE_SIZE * Float.BYTES));
            glDrawElementsInstancedBaseInstance(GL_TRIANGLES, indexBuffer, count, baseInstance);

            bufferOffset = (bufferOffset + 1) % 3;

            fences[bufferOffset].waitBuffer();

            clear();
        }
    }

    @Override
    public void clear() {
        count = 0;
    }

    @Override
    public void free() {
        instanceVbo.unmap();
        instanceVbo.delete();
        vertexVbo.delete();
    }

    @Override
    public long capacity() {
        return capacity;
    }

    @Override
    public int count() {
        return count;
    }

    private long getBufferIndex() {
        final long base = bufferOffset * bufferSize();
        return base + ((long) count * INSTANCE_SIZE * Float.BYTES);
    }

    private long bufferSize() {
        return capacity * INSTANCE_SIZE * Float.BYTES;
    }

    private long totalBufferSize() {
        return bufferSize() * 3;
    }

    private int floatBytes(int value) {
        return value * Float.BYTES;
    }
}
