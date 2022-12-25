package me.cryptforge.engine.render;

import me.cryptforge.engine.asset.Shader;
import me.cryptforge.engine.asset.Texture;

public abstract class RenderBatch {

    private final VertexBuffer vertexBuffer;
    private final Shader shader;
    private Texture texture;

    public RenderBatch(VertexBuffer vertexBuffer,Shader shader) {
        this.vertexBuffer = vertexBuffer;
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

    protected VertexBuffer vertexBuffer() {
        return vertexBuffer;
    }

    public Texture getTexture() {
        return texture;
    }

    public Shader getShader() {
        return shader;
    }
}
