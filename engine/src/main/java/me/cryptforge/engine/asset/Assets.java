package me.cryptforge.engine.asset;

import me.cryptforge.engine.asset.loader.FontLoader;
import me.cryptforge.engine.asset.loader.ShaderLoader;
import me.cryptforge.engine.asset.loader.TextureLoader;
import me.cryptforge.engine.asset.type.Font;
import me.cryptforge.engine.asset.type.Shader;
import me.cryptforge.engine.asset.type.Texture;
import me.cryptforge.engine.exception.AssetMissingException;
import me.cryptforge.engine.exception.UnknownAssetException;
import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public final class Assets {

    private static final Map<String, Texture> textures = new HashMap<>();
    private static final Map<String, Shader> shaders = new HashMap<>();
    private static final Map<String, Font> fonts = new HashMap<>();

    private Assets() {
    }

    public static final class Loader {

        private final TextureLoader textureLoader = new TextureLoader();
        private final ShaderLoader shaderLoader = new ShaderLoader();
        private final FontLoader fontLoader = new FontLoader();

        private Loader() {
        }

        /**
         * Loads a texture
         * @param id ID to assign to the loaded texture
         * @param asset Asset containing the texture
         * @param settings Texture settings
         */
        public void texture(String id, Asset asset, TextureSettings settings) {
            try {
                final Texture texture = textureLoader.load(asset, settings);
                textures.put(id, texture);
            } catch (FileNotFoundException e) {
                throw new AssetMissingException(asset);
            }
        }

        /**
         * Loads a shader
         * @param id ID to assign to the loaded shader
         * @param vertex Asset containing the vertex shader
         * @param fragment Asset containing the fragment shader
         */
        public void shader(String id, Asset vertex, Asset fragment) {
            try {
                final Shader shader = shaderLoader.load(vertex, fragment);
                shaders.put(id, shader);
            } catch (FileNotFoundException e) {
                throw new AssetMissingException(vertex);
            }
        }

        /**
         * Loads a font
         * @param id ID to assign to the loaded font
         * @param asset Asset containing the font
         * @param size Font pixel size
         */
        public void font(String id, Asset asset, int size) {
            try {
                final Font font = fontLoader.load(asset, size);
                fonts.put(id, font);
            } catch (FileNotFoundException e) {
                throw new AssetMissingException(asset);
            }
        }
    }

    public static void load(Consumer<Loader> loader) {
        loader.accept(new Loader());
    }

    /**
     * Gets a loaded texture with id
     *
     * @param id Texture id
     * @return Loaded texture
     * @throws UnknownAssetException When the texture with specified id hasn't been loaded
     */
    public static @NotNull Texture texture(String id) {
        final Texture texture = textures.get(id);
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
    public static @NotNull Shader shader(String id) {
        final Shader shader = shaders.get(id);
        if (shader == null) {
            throw new UnknownAssetException("Shader", id);
        }
        return shader;
    }

    /**
     * Gets a loaded font with id
     *
     * @param id Font id
     * @return Loaded font
     * @throws UnknownAssetException When the font with specified id hasn't been loaded
     */
    public static @NotNull Font font(String id) {
        final Font font = fonts.get(id);
        if (font == null) {
            throw new UnknownAssetException("Font", id);
        }
        return font;
    }
}
