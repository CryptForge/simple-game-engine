package me.cryptforge.engine.asset;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public final class ExternalAsset implements Asset {

    private final String path;

    ExternalAsset(String path) {
        if(path.startsWith("/")) {
            this.path = path.substring(1);
        } else {
            this.path = path;
        }
    }

    @Override
    public @NotNull InputStream openStream() throws FileNotFoundException {
        final File file = new File(path);
        if (!file.exists() || !file.isFile()) {
            throw new FileNotFoundException();
        }

        return new FileInputStream(file);
    }

    @Override
    public @NotNull String path() {
        return path;
    }

    @Override
    public @NotNull String type() {
        return "external";
    }
}
