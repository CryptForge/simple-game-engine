package me.cryptforge.engine.render;

import me.cryptforge.engine.asset.Texture;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL33.*;

public final class VertexBuffer implements AutoCloseable {

    public static final int VERTEX_SIZE = 8 * Float.BYTES;

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

    public void flush() {
        if (count > 0) {
            final var vbo = renderer.vbo();

            buffer.flip();

            renderer.getActiveShader().use();

            final Texture activeTexture = renderer.getActiveTexture();
            if (activeTexture != null) {
                glActiveTexture(GL_TEXTURE0);
                activeTexture.bind();
            }
            vbo.bind(GL_ARRAY_BUFFER);
            vbo.uploadSubData(GL_ARRAY_BUFFER, 0, buffer);

            glDrawArrays(GL_TRIANGLES, 0, count);

            clear();
        }
    }

    public VertexBuffer vertex(float x, float y, float r, float g, float b, float a, float textureX, float textureY) {
        if (!hasSpace(1)) {
            flush();
        }
        buffer.put(x).put(y).put(r).put(g).put(b).put(a).put(textureX).put(textureY);
        count++;
        return this;
    }

    public VertexBuffer region(float bottomX, float bottomY, float topX, float topY, float bottomTextureX, float bottomTextureY, float topTextureX, float topTextureY, float r, float g, float b, float a) {
        if (!hasSpace(6)) {
            flush();
        }
        vertex(bottomX, bottomY, r, g, b, a, bottomTextureX, bottomTextureY);
        vertex(bottomX, topY, r, g, b, a, bottomTextureX, topTextureY);
        vertex(topX, topY, r, g, b, a, topTextureX, topTextureY);

        vertex(bottomX, bottomY, r, g, b, a, bottomTextureX, bottomTextureY);
        vertex(topX, topY, r, g, b, a, topTextureX, topTextureY);
        vertex(topX, bottomY, r, g, b, a, topTextureX, bottomTextureY);

        return this;
    }

    public VertexBuffer region(float bottomX, float bottomY, float topX, float topY, float bottomTextureX, float bottomTextureY, float topTextureX, float topTextureY, Color color) {
        return region(bottomX, bottomY, topX, topY, bottomTextureX, bottomTextureY, topTextureX, topTextureY, color.r(), color.g(), color.b(), color.a());
    }

    @Override
    public void close() {
        MemoryUtil.memFree(buffer);
    }

    public int getCapacity() {
        return buffer.capacity();
    }

    public boolean hasSpace(int count) {
        return (getCapacity() - this.count * VERTEX_SIZE) >= count * VERTEX_SIZE;
    }
}
