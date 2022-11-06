package me.cryptforge.engine.asset;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public enum AssetPathType {
    RESOURCE("Resource") {
        @Override
        public @NotNull InputStream openStream(String path) throws FileNotFoundException {
            final InputStream inputStream = AssetPathType.class.getClassLoader().getResourceAsStream(path);
            if(inputStream == null) {
                throw new FileNotFoundException();
            }

            return inputStream;
        }
    },
    FILE("File") {
        @Override
        public @NotNull InputStream openStream(String path) throws FileNotFoundException {
            final File file = new File(path);
            if(!file.exists() || !file.isFile()) {
                throw new FileNotFoundException();
            }

            return new FileInputStream(file);
        }
    };

    private final String name;

    AssetPathType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public abstract @NotNull InputStream openStream(String path) throws FileNotFoundException;
}
