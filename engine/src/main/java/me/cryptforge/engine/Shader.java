package me.cryptforge.engine;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2f;
import org.joml.Matrix4fc;
import org.joml.Vector3fc;
import org.lwjgl.system.MemoryStack;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.FloatBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.stream.Collectors;

import static org.lwjgl.opengl.GL33.*;

@ApiStatus.Internal
class Shader {

    private final int id;

    /**
     * Creates a shader
     * @param vertexPath Resource path of vertex file
     * @param fragmentPath Resource path of fragment file
     */
    public Shader(@NotNull String vertexPath, @NotNull String fragmentPath) {
        final String vertexCode = readResource(vertexPath);
        final String fragmentCode = readResource(fragmentPath);

        final int vertexId = glCreateShader(GL_VERTEX_SHADER);
        {
            final String log = compileShader(vertexId, vertexCode);
            if (!log.isEmpty()) {
                System.err.println("Failed to compile vertex shader \"" + vertexPath + "\"");
                System.err.println(log);
            }
        }


        final int fragmentId = glCreateShader(GL_FRAGMENT_SHADER);
        {
            final String log = compileShader(fragmentId, fragmentCode);
            if (!log.isEmpty()) {
                System.err.println("Failed to compile fragment shader \"" + vertexPath + "\"");
                System.err.println(log);
            }
        }

        id = glCreateProgram();
        glAttachShader(id, vertexId);
        glAttachShader(id, fragmentId);
        glLinkProgram(id);

        final int[] buffer = new int[1];
        glGetProgramiv(id, GL_LINK_STATUS, buffer);
        final boolean success = buffer[0] == GL_TRUE;
        if (!success) {
            System.err.println("Failed to link shader");
            System.err.println(glGetProgramInfoLog(id, 512));
        }

        glDeleteShader(vertexId);
        glDeleteShader(fragmentId);
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

    /**
     * Compiles a shader and returns the error log
     * @param id Shader handle
     * @param source Source code
     * @return The error log, empty if successful
     */
    private static String compileShader(int id, String source) {
        glShaderSource(id, source);
        glCompileShader(id);

        final int[] buffer = new int[1];
        glGetShaderiv(id, GL_COMPILE_STATUS, buffer);
        final boolean success = buffer[0] == GL_TRUE;

        if (!success) {
            return glGetProgramInfoLog(id);
        }
        return "";
    }

    /**
     * Reads a resource into a string
     * @param resource Path to resource
     * @return Resource contents
     */
    private static String readResource(String resource) {
        try (final InputStream inputStream = Shader.class.getResourceAsStream(resource)) {
            if (inputStream == null)
                throw new IllegalArgumentException("Resource \"" + resource + "\" not found!");

            final InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            final BufferedReader bufferedReader = new BufferedReader(reader);

            return bufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            System.err.println("Failed to read resource");
            throw new RuntimeException(e);
        }
    }
}
