package me.cryptforge.engine.render;

import me.cryptforge.engine.asset.AssetManager;
import me.cryptforge.engine.render.buffer.InstanceBuffer;
import org.joml.Matrix4f;

public final class SpriteBatch extends RenderBatch<InstanceBuffer> {


    public SpriteBatch(InstanceBuffer buffer) {
        super(buffer, AssetManager.getShader("sprite"));
    }

    @Override
    void init() {
        if(getTexture() == null) {
            throw new IllegalStateException("Texture is null for sprite batch");
        }
        buffer().clear();
    }

    @Override
    void cleanup() {

    }

    public void drawSprite(Matrix4f modelMatrix, Color color) {
        buffer().putInstance(color,modelMatrix);
    }
}
