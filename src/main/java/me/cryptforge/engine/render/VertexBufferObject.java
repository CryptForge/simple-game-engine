package me.cryptforge.engine.render;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL46.*;

public class VertexBufferObject {

    private final int id;

    public VertexBufferObject() {
        this.id = glCreateBuffers();
    }

    public void bind(int target) {
        glBindBuffer(target, id);
    }

    public void uploadData(long size, int usage) {
        glNamedBufferData(id, size, usage);
    }

    public void uploadData(float[] data, int usage) {
        glNamedBufferData(id, data, usage);
    }

    public void uploadSubData(long offset, FloatBuffer data) {
        glNamedBufferSubData(id, offset, data);
    }

    public void bufferStorage(long size, int flags) {
        glNamedBufferStorage(id, size, flags);
    }

    public ByteBuffer mapRange(long offset, long length, int flags) {
        return glMapNamedBufferRange(id, offset, length, flags);
    }

    public ByteBuffer map(int access) {
        return glMapNamedBuffer(id, access);
    }

    public boolean unmap() {
        return glUnmapNamedBuffer(id);
    }

    public void delete() {
        glDeleteBuffers(id);
    }

    public int id() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VertexBufferObject that = (VertexBufferObject) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
