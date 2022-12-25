package me.cryptforge.engine.render;

import me.cryptforge.engine.asset.AssetManager;

public final class SpriteBatch extends RenderBatch {

    public SpriteBatch(VertexBuffer vertexBuffer) {
        super(vertexBuffer, AssetManager.getShader("sprite"));
    }

    @Override
    void init() {
        if(getTexture() == null) {
            throw new IllegalStateException("Texture is null for sprite batch");
        }
    }

    @Override
    void cleanup() {

    }

    public void drawSprite(float posX, float posY, float sizeX, float sizeY, Color color) {
        vertexBuffer().region(posX, posY, posX + sizeX, posY + sizeY, 0, 0, 1, 1, color);
    }

    public void drawSprite(float posX, float posY, float sizeX, float sizeY) {
        drawSprite(posX, posY, sizeX, sizeY, Color.WHITE);
    }
}
