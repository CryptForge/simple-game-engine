package me.cryptforge.engine.render;

import me.cryptforge.engine.asset.Assets;
import me.cryptforge.engine.render.buffer.InstanceBuffer;
import org.joml.Matrix3x2f;

public class ShapeBatch extends RenderBatch<InstanceBuffer> {

    private final Matrix3x2f matrix;

    public ShapeBatch(InstanceBuffer buffer) {
        super(buffer, Assets.shader("shape"));
        this.matrix = new Matrix3x2f();
    }

    @Override
    void init() {
        buffer().clear();
    }

    @Override
    void cleanup() {

    }

    public void drawSquare(float x, float y, float size, Color color) {
        drawRectangle(x, y, size, size, color);
    }

    public void drawRectangle(float x, float y, float sizeX, float sizeY, Color color) {
        matrix.translation(x, y)
              .scale(sizeX, sizeY);

        buffer().putInstance(
                color.r(),
                color.g(),
                color.b(),
                color.a(),
                0, 0, 0, 0,
                matrix
        );
    }
}
