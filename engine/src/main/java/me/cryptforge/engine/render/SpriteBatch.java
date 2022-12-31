package me.cryptforge.engine.render;

import me.cryptforge.engine.asset.AssetManager;
import me.cryptforge.engine.render.buffer.InstanceBuffer;
import org.joml.Matrix4f;

public final class SpriteBatch extends RenderBatch<InstanceBuffer> {

    private final Matrix4f model;

    public SpriteBatch(InstanceBuffer buffer) {
        super(buffer, AssetManager.getShader("sprite"));
        model = new Matrix4f();
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

    public void drawSprite(float posX, float posY, float sizeX, float sizeY, Color color) {
        buffer().putInstance(color,model.identity().translation(posX,posY,0).scale(sizeX,sizeY,0));
    }

    public void drawSprite(float posX, float posY, float sizeX, float sizeY) {
        drawSprite(posX, posY, sizeX, sizeY, Color.WHITE);
    }
}
