package me.cryptforge.engine.render;

import me.cryptforge.engine.asset.AssetManager;

public class ShapeBatch extends RenderBatch {

    public ShapeBatch(VertexBuffer vertexBuffer) {
        super(vertexBuffer, AssetManager.getShader("shape"));
    }

    @Override
    void init() {

    }

    @Override
    void cleanup() {

    }

    public void drawSquare(float x, float y, float size, Color color) {
        drawRectangle(x, y, x + size, y + size, color);
    }

    public void drawRectangle(float x, float y, float sizeX, float sizeY, Color color) {
        vertexBuffer().region(x, y, x + sizeX, y + sizeY, 0, 0, 0, 0, color);
    }
}
