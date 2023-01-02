package me.cryptforge.engine.asset;

import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * An interface used to load assets on a file system.
 * <br>
 * <br>
 * Use {@link Asset#external(String)} to load an asset outside the jar.
 * <br>
 * Use {@link Asset#internal(String)} to load an asset bundled with the jar.
 */
public sealed interface Asset permits InternalAsset, ExternalAsset {

    @NotNull InputStream openStream() throws FileNotFoundException;

    @NotNull String path();

    @NotNull String type();

    static Asset internal(String path) {
        return new InternalAsset(path);
    }

    static Asset external(String path) {
        return new ExternalAsset(path);
    }

}
