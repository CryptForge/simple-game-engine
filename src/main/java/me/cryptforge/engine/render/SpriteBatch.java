package me.cryptforge.engine.render;

import me.cryptforge.engine.asset.Assets;
import me.cryptforge.engine.render.buffer.InstanceBuffer;
import org.joml.Matrix3x2f;

public final class SpriteBatch extends RenderBatch<InstanceBuffer> {

    private final Matrix3x2f matrix;

    public SpriteBatch(InstanceBuffer buffer) {
        super(buffer, Assets.shader("sprite"));
        this.matrix = new Matrix3x2f();
    }

    @Override
    void init() {
        if (getTexture() == null) {
            throw new IllegalStateException("Texture is null for sprite batch");
        }
        buffer().clear();
    }

    @Override
    void cleanup() {

    }

    public void drawSprite(Matrix3x2f matrix, Color color) {
        drawSprite(
                matrix,
                0,
                0,
                getTexture().getWidth(),
                getTexture().getHeight(),
                color
        );
    }

    public void drawSprite(float x, float y, float width, float height, float rotation, Color color) {
        matrix.translation(x, y)
              .scale(width, height)
              .rotateAbout((float) Math.toRadians(rotation), 0.5f, 0.5f);
        drawSprite(matrix, color);
    }

    public void drawSprite(Matrix3x2f matrix, float minTexX, float minTexY, float maxTexX, float maxTexY, Color color) {
        final float texX = minTexX / getTexture().getWidth();
        final float texY = minTexY / getTexture().getHeight();
        final float texSizeX = (maxTexX - minTexX) / getTexture().getWidth();
        final float texSizeY = (maxTexY - minTexY) / getTexture().getHeight();

        buffer().putInstance(
                color.r(),
                color.g(),
                color.b(),
                color.a(),
                texSizeX,
                texSizeY,
                texX,
                texY,
                matrix
        );
    }

    public void drawSpriteRegion(
            float x,
            float y,
            float width,
            float height,
            float minTexX,
            float minTexY,
            float maxTexX,
            float maxTexY,
            float rotation,
            Color color
    ) {
        matrix.translation(x, y)
              .scale(width, height)
              .rotateAbout((float) Math.toRadians(rotation), 0.5f, 0.5f);

        drawSprite(
                matrix,
                minTexX,
                minTexY,
                maxTexX,
                maxTexY,
                color
        );
    }
}
