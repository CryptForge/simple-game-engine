package me.cryptforge.engine.asset.type;

import me.cryptforge.engine.Freeable;
import org.jetbrains.annotations.ApiStatus;

import java.util.Objects;

import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL33.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL33.glBindTexture;


@ApiStatus.Internal
public record Texture(int id, int width, int height, int channelCount) implements Freeable {

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Texture texture = (Texture) o;
        return id == texture.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public void free() {
        glDeleteTextures(id);
    }
}
