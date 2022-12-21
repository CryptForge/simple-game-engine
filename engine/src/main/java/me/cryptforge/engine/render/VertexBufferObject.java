package me.cryptforge.engine.render;

import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL33.*;

public class VertexBufferObject {

    private final int id;

    public VertexBufferObject() {
        this.id = glGenBuffers();
    }

    public void bind(int target) {
        glBindBuffer(target, id);
    }

    public void uploadData(int target, long size, int usage) {
        glBufferData(target, size, usage);
    }

    public void uploadData(int target, float[] data,int usage) {
        glBufferData(target,data,usage);
    }

    public void uploadData(int target, FloatBuffer data, int usage) {
        glBufferData(target, data, usage);
    }

    public void uploadSubData(int target, long offset, FloatBuffer data) {
        glBufferSubData(target, offset, data);
    }

    public void delete() {
        glDeleteBuffers(id);
    }

    public int getId() {
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
