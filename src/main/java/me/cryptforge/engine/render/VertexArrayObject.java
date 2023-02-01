package me.cryptforge.engine.render;

import static org.lwjgl.opengl.GL46.*;

public class VertexArrayObject {

    private final int id;

    public VertexArrayObject() {
        this.id = glCreateVertexArrays();
    }

    public void bind() {
        glBindVertexArray(id);
    }

    public void unbind() {
        glBindVertexArray(0);
    }

    public void delete() {
        glDeleteVertexArrays(id);
    }

    public void defineAttribute(int index, int size, int stride, int offset, VertexBufferObject source) {
        glEnableVertexArrayAttrib(id, index);
        glVertexArrayVertexBuffer(id, index, source.id(), offset, stride);
        glVertexArrayAttribFormat(id, index, size, GL_FLOAT, false, 0);
        glVertexArrayAttribBinding(id, index, index);
    }

    public void defineDivisor(int index, int divisor) {
        glVertexArrayBindingDivisor(id,index,divisor);
    }

    public int id() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VertexArrayObject that = (VertexArrayObject) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
