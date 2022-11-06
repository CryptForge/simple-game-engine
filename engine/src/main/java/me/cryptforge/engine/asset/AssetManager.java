package me.cryptforge.engine.asset;

import me.cryptforge.engine.exception.AssetMissingException;
import me.cryptforge.engine.exception.UnknownAssetException;
import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;

public final class AssetManager {

    private static final TextureLoader textureLoader = new TextureLoader();
    private static final ShaderLoader shaderLoader = new ShaderLoader();

    private AssetManager() {
    }

    public static @NotNull Texture loadTexture(String id, AssetPathType pathType, String path, TextureSettings settings) {
        final Texture texture;
        try {
            texture = textureLoader.load(id,pathType,path,settings);
        } catch (FileNotFoundException e) {
            throw new AssetMissingException(pathType,path);
        }

        return texture;
    }

    public static @NotNull Shader loadShader(String id, AssetPathType pathType, String path) {
        final Shader shader;
        try {
            shader = shaderLoader.load(id,pathType,path,null);
        } catch (FileNotFoundException e) {
            throw new AssetMissingException(pathType,path);
        }

        return shader;
    }

    public static @NotNull Texture getTexture(String id) {
        final Texture texture = textureLoader.get(id);
        if(texture == null) {
            throw new UnknownAssetException("Texture",id);
        }

        return texture;
    }

    public static @NotNull Shader getShader(String id) {
        final Shader shader = shaderLoader.get(id);
        if(shader == null) {
            throw new UnknownAssetException("Shader",id);
        }

        return shader;
    }
}
