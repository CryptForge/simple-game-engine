package me.cryptforge.engine.render;

import me.cryptforge.engine.asset.type.Shader;
import me.cryptforge.engine.asset.type.Texture;
import me.cryptforge.engine.render.buffer.DrawBuffer;

public abstract class RenderBatch<T extends DrawBuffer> {

    private final T buffer;
    private final Shader shader;
    private Texture texture;

    public RenderBatch(T buffer, Shader shader) {
        this.buffer = buffer;
        this.shader = shader;
    }

    abstract void init();

    abstract void cleanup();

    void clear() {
        texture = null;

        cleanup();
    }

    protected void setTexture(Texture texture) {
        if(this.texture != null && this.texture.equals(texture)) {
            throw new RuntimeException("Cannot change texture twice during draw batch");
        }
        this.texture = texture;
    }

    protected T buffer() {
        return buffer;
    }

    public Texture getTexture() {
        return texture;
    }

    public Shader getShader() {
        return shader;
    }
}
