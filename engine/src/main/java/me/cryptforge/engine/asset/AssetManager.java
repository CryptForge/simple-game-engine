package me.cryptforge.engine.asset;

import me.cryptforge.engine.exception.AssetMissingException;
import me.cryptforge.engine.exception.UnknownAssetException;
import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;

public final class AssetManager {

    private static final TextureLoader textureLoader = new TextureLoader();
    private static final ShaderLoader shaderLoader = new ShaderLoader();
    private static final FontLoader fontLoader = new FontLoader();

    private AssetManager() {
    }

    /**
     * Loads and registers a texture with specified settings
     *
     * @param id       Texture id
     * @param pathType Path type
     * @param path     Path to load texture from
     * @param settings Texture settings
     * @return The loaded texture
     */
    public static @NotNull Texture loadTexture(String id, AssetPathType pathType, String path, TextureSettings settings) {
        final Texture texture;
        try {
            texture = textureLoader.load(id, pathType, path, settings);
        } catch (FileNotFoundException e) {
            throw new AssetMissingException(pathType, path);
        }

        return texture;
    }

    /**
     * Loads and registers a texture with default settings
     *
     * @param id       Texture id
     * @param pathType Path type
     * @param path     Path to load texture from
     * @return The loaded texture
     */
    public static @NotNull Texture loadTexture(String id, AssetPathType pathType, String path) {
        return loadTexture(id, pathType, path, TextureSettings.defaultSettings());
    }

    /**
     * Loads and registers a shader
     *
     * @param id       Shader id
     * @param pathType Path type
     * @param path     Path to load shader from
     * @return The loaded shader
     */
    public static @NotNull Shader loadShader(String id, AssetPathType pathType, String path) {
        final Shader shader;
        try {
            shader = shaderLoader.load(id, pathType, path, null);
        } catch (FileNotFoundException e) {
            throw new AssetMissingException(pathType, path);
        }

        return shader;
    }

    public static @NotNull Font loadFont(String id, AssetPathType pathType, String path, int fontSize) {
        final Font font;
        try {
            font = fontLoader.load(id, pathType, path, fontSize);
        } catch (FileNotFoundException e) {
            throw new AssetMissingException(pathType,path);
        }

        return font;
    }

    /**
     * Gets a loaded texture with id
     *
     * @param id Texture id
     * @return Loaded texture
     * @throws UnknownAssetException When the texture with specified id hasn't been loaded
     */
    public static @NotNull Texture getTexture(String id) {
        final Texture texture = textureLoader.get(id);
        if (texture == null) {
            throw new UnknownAssetException("Texture", id);
        }

        return texture;
    }

    /**
     * Gets a loaded shader with id
     *
     * @param id Shader id
     * @return Loaded shader
     * @throws UnknownAssetException When the shader with specified id hasn't been loaded
     */
    public static @NotNull Shader getShader(String id) {
        final Shader shader = shaderLoader.get(id);
        if (shader == null) {
            throw new UnknownAssetException("Shader", id);
        }

        return shader;
    }

    public static @NotNull Font getFont(String id) {
        final Font font = fontLoader.get(id);
        if (font == null) {
            throw new UnknownAssetException("Font", id);
        }

        return font;
    }
}
