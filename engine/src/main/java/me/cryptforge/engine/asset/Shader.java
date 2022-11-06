package me.cryptforge.engine.asset;

import org.joml.Matrix3x2f;
import org.joml.Matrix4fc;
import org.joml.Vector3fc;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL33.*;

public class Shader {

    private final int id;

    protected Shader(int id) {
        this.id = id;
    }

    public void use() {
        glUseProgram(id);
    }

    public void delete() {
        glDeleteProgram(id);
    }

    public void setFloat(String name, float value) {
        glUniform1f(glGetUniformLocation(id, name), value);
    }

    public void setInt(String name, int value) {
        glUniform1i(glGetUniformLocation(id, name), value);
    }

    public void setVector3f(String name, Vector3fc vector) {
        glUniform3f(glGetUniformLocation(id, name), vector.x(), vector.y(), vector.z());
    }

    public void setVector3f(String name, float x, float y, float z) {
        glUniform3f(glGetUniformLocation(id, name), x, y, z);
    }

    public void setMatrix4f(String name, Matrix4fc matrix) {
        try (final MemoryStack stack = MemoryStack.stackPush()) {
            final FloatBuffer floatBuffer = matrix.get(stack.mallocFloat(16));
            glUniformMatrix4fv(glGetUniformLocation(id, name), false, floatBuffer);
        }
    }

    public void setProjectionMatrix(String name, Matrix3x2f matrix) {
        try (final MemoryStack stack = MemoryStack.stackPush()) {
            final FloatBuffer floatBuffer = matrix.get4x4(stack.mallocFloat(16));
            glUniformMatrix4fv(glGetUniformLocation(id, name), false, floatBuffer);
        }
    }


}
