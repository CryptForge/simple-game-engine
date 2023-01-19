package me.cryptforge.engine.render.buffer;

import me.cryptforge.engine.render.Renderer;
import me.cryptforge.engine.render.VertexArrayObject;
import me.cryptforge.engine.render.VertexBufferObject;
import org.joml.Matrix3x2f;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static me.cryptforge.engine.util.GLUtils.initAttribute;
import static org.lwjgl.opengl.GL33.*;

public final class InstanceBuffer implements DrawBuffer {

    private static final int INSTANCE_SIZE = 14;

    private final Renderer renderer;
    private final VertexArrayObject vao;
    private final VertexBufferObject vertexVbo;
    private final VertexBufferObject instanceVbo;
    private final FloatBuffer instanceBuffer;
    private final ByteBuffer indexBuffer;
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
                1, 0, 1, 0,
                1, 1, 1, 1
        };
        vertexVbo.uploadData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        instanceVbo.bind(GL_ARRAY_BUFFER);
        // init instance attributes
        initAttribute(1, 4, INSTANCE_SIZE * Float.BYTES, 0); // color (vec4)
        initAttribute(2, 4, INSTANCE_SIZE * Float.BYTES, 4 * Float.BYTES); // uvs (vec4)

        // model matrix (vec2 vec2 vec2)
        initAttribute(3, 2, INSTANCE_SIZE * Float.BYTES, 8 * Float.BYTES);
        initAttribute(4, 2, INSTANCE_SIZE * Float.BYTES, 10 * Float.BYTES);
        initAttribute(5, 2, INSTANCE_SIZE * Float.BYTES, 12 * Float.BYTES);

        glVertexAttribDivisor(1, 1);
        glVertexAttribDivisor(2, 1);
        glVertexAttribDivisor(3, 1);
        glVertexAttribDivisor(4, 1);
        glVertexAttribDivisor(5, 1);

        // set instance vbo buffer size
        final long size = (long) capacity * INSTANCE_SIZE * Float.BYTES;
        instanceVbo.uploadData(GL_ARRAY_BUFFER, size, GL_DYNAMIC_DRAW);

        // set indices
        indexBuffer.put(new byte[]{0, 1, 3, 0, 3, 2});
        indexBuffer.flip();
    }

    public InstanceBuffer putInstance(float r, float g, float b, float a, float texWidth, float texHeight, float texX, float texY, Matrix3x2f matrix) {
        if (!hasSpace(1)) {
            renderer.flushBuffer();
        }
        instanceBuffer.put(r).put(g).put(b).put(a).put(texWidth).put(texHeight).put(texX).put(texY);

        renderer.getProjectionMatrix().mul(matrix,matrix);
        matrix.get(instanceBuffer);
        instanceBuffer.position(instanceBuffer.position() + 6);
        count++;
        return this;
    }

    @Override
    public void flush() {
        if (count > 0) {
            instanceBuffer.flip();

            instanceVbo.bind(GL_ARRAY_BUFFER);
            instanceVbo.uploadSubData(GL_ARRAY_BUFFER, 0, instanceBuffer);

            vao.bind();
            glDrawElementsInstanced(GL_TRIANGLES, indexBuffer, count);
            vao.unbind();

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
