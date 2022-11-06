package me.cryptforge.engine.asset;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL20.*;

final class ShaderLoader extends AssetLoader<Shader, Void> {

    private final Map<String,Shader> shaders = new HashMap<>();

    @Override
    protected @Nullable Shader get(String id) {
        return shaders.get(id);
    }

    @Override
    protected @NotNull Shader load(String id, AssetPathType pathType, String path, Void data) throws FileNotFoundException {
        final String vertexCode = readAsset(pathType,path + ".vert");
        final String fragmentCode = readAsset(pathType,path + ".frag");

        final int vertexId = glCreateShader(GL_VERTEX_SHADER);
        {
            final String log = compileShader(vertexId, vertexCode);
            if (!log.isEmpty()) {
                System.err.println("Failed to compile vertex shader \"" + path + ".vert\"");
                System.err.println(log);
            }
        }


        final int fragmentId = glCreateShader(GL_FRAGMENT_SHADER);
        {
            final String log = compileShader(fragmentId, fragmentCode);
            if (!log.isEmpty()) {
                System.err.println("Failed to compile fragment shader \"" + path + ".frag\"");
                System.err.println(log);
            }
        }

        final int shaderId = glCreateProgram();
        glAttachShader(shaderId, vertexId);
        glAttachShader(shaderId, fragmentId);
        glLinkProgram(shaderId);

        final int[] buffer = new int[1];
        glGetProgramiv(shaderId, GL_LINK_STATUS, buffer);
        final boolean success = buffer[0] == GL_TRUE;
        if (!success) {
            System.err.println("Failed to link shader");
            System.err.println(glGetProgramInfoLog(shaderId, 512));
        }

        glDeleteShader(vertexId);
        glDeleteShader(fragmentId);

        final Shader shader = new Shader(shaderId);
        shaders.put(id,shader);
        return shader;
    }

    /**
     * Compiles a shader and returns the error log
     * @param id Shader handle
     * @param source Source code
     * @return The error log, empty if successful
     */
    private String compileShader(int id, String source) {
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
     * Reads an asset into a string
     * @param pathType Type of path
     * @param path Path to asset
     * @return Asset contents
     */
    private String readAsset(AssetPathType pathType,String path) throws FileNotFoundException {
        try (final InputStream inputStream = pathType.openStream(path)) {
            final InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            final BufferedReader bufferedReader = new BufferedReader(reader);

            return bufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (FileNotFoundException e) {
            throw e;
        } catch (IOException e) {
            System.err.println("Failed to read asset");
            throw new RuntimeException(e);
        }
    }
}
