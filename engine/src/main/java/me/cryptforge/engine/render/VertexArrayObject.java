package me.cryptforge.engine.render;

import static org.lwjgl.opengl.GL33.*;

public class VertexArrayObject {

    private final int id;

    public VertexArrayObject() {
        this.id = glGenVertexArrays();
    }

    public void bind() {
        glBindVertexArray(id);
    }

    public void delete() {
        glDeleteVertexArrays(id);
    }

    public int getId() {
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
