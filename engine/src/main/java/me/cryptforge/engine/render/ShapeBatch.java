package me.cryptforge.engine.render;

import me.cryptforge.engine.asset.AssetManager;
import me.cryptforge.engine.render.buffer.VertexBuffer;

public class ShapeBatch extends RenderBatch<VertexBuffer> {

    public ShapeBatch(VertexBuffer buffer) {
        super(buffer, AssetManager.getShader("shape"));
    }

    @Override
    void init() {
        buffer().clear();
    }

    @Override
    void cleanup() {

    }

    public void drawSquare(float x, float y, float size, Color color) {
        drawRectangle(x, y, x + size, y + size, color);
    }

    public void drawRectangle(float x, float y, float sizeX, float sizeY, Color color) {
        buffer().putRegion(x, y, x + sizeX, y + sizeY, 0, 0, 0, 0, color);
    }
}
