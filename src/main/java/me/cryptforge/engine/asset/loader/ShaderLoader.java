package me.cryptforge.engine.asset.loader;

import me.cryptforge.engine.asset.Asset;
import me.cryptforge.engine.asset.type.Shader;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import static org.lwjgl.opengl.GL33.*;

@ApiStatus.Internal
public final class ShaderLoader extends AssetLoader<Shader, Asset> {

    @Override
    public @NotNull Shader load(Asset vertexFile, Asset fragmentFile) throws FileNotFoundException {
        final String vertexCode = readAsset(vertexFile);
        final String fragmentCode = readAsset(fragmentFile);

        final int vertexId = glCreateShader(GL_VERTEX_SHADER);
        {
            final String log = compileShader(vertexId, vertexCode);
            if (!log.isEmpty()) {
                System.err.println("Failed to compile vertex shader \"" + vertexFile.path() + ".vert\"");
                System.err.println(log);
            }
        }


        final int fragmentId = glCreateShader(GL_FRAGMENT_SHADER);
        {
            final String log = compileShader(fragmentId, fragmentCode);
            if (!log.isEmpty()) {
                System.err.println("Failed to compile fragment shader \"" + fragmentFile.path() + ".frag\"");
                System.err.println(log);
            }
        }

        final int shaderId = glCreateProgram();
        glAttachShader(shaderId, vertexId);
        glAttachShader(shaderId, fragmentId);
        glBindFragDataLocation(shaderId,0,"fragColor");
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

        return new Shader(shaderId);
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
            return glGetShaderInfoLog(id);
        }
        return "";
    }

    /**
     * Reads an asset into a string
     * @param asset The asset to read
     * @return Asset contents
     */
    private String readAsset(Asset asset) throws FileNotFoundException {
        try (final InputStream inputStream = asset.openStream()) {
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
