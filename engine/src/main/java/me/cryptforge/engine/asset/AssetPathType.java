package me.cryptforge.engine.asset;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.system.MemoryUtil;

import java.io.*;
import java.nio.ByteBuffer;

public enum AssetPathType {
    RESOURCE("Resource") {
        @Override
        public @NotNull InputStream openStream(String path) throws FileNotFoundException {
            final InputStream inputStream = AssetPathType.class.getClassLoader().getResourceAsStream(path);
            if (inputStream == null) {
                throw new FileNotFoundException();
            }

            return inputStream;
        }
    },
    FILE("File") {
        @Override
        public @NotNull InputStream openStream(String path) throws FileNotFoundException {
            final File file = new File(path);
            if (!file.exists() || !file.isFile()) {
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

    public ByteBuffer readToByteBuffer(String path, int bufferLimit) throws FileNotFoundException {
        final ByteBuffer buffer = MemoryUtil.memAlloc(bufferLimit);
        try (final InputStream stream = openStream(path)) {
            byte b;
            while (-1 != (b = (byte) stream.read())) {
                buffer.put(b);
            }
        } catch (FileNotFoundException e) {
            throw e;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return buffer;
    }
}
