package me.cryptforge.engine.asset;

import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.InputStream;

public final class InternalAsset implements Asset {

    private final String path;

    InternalAsset(String path) {
        if(path.startsWith("/")) {
            this.path = path.substring(1);
        } else {
            this.path = path;
        }
    }

    @Override
    public @NotNull InputStream openStream() throws FileNotFoundException {
        final InputStream stream = Asset.class.getClassLoader().getResourceAsStream(path);
        if(stream == null) {
            throw new FileNotFoundException();
        }
        return stream;
    }

    @Override
    public @NotNull String path() {
        return path;
    }

    @Override
    public @NotNull String type() {
        return "internal";
    }
}
