package me.cryptforge.demo;

import me.cryptforge.engine.Drawable;
import me.cryptforge.engine.asset.type.Texture;
import me.cryptforge.engine.render.Color;
import org.joml.Matrix3x2f;

public final class TestObject implements Drawable {

    private final Matrix3x2f transform;
    private final Texture texture;
    private final Color color;

    public TestObject(Texture texture, float x, float y, float scale) {
        this.texture = texture;
        this.color = Color.WHITE;
        this.transform = new Matrix3x2f().translation(x, y).scale(scale, scale);
    }

    public void update() {
        transform()
                .translate(((float) Math.random() - 0.5f) / 10f, ((float) Math.random() - 0.5f) / 10f)
                .rotateAbout((float) (Math.random() / 2f),0.5f, 0.5f);
    }

    @Override
    public Matrix3x2f transform() {
        return transform;
    }

    @Override
    public Color color() {
        return color;
    }

    @Override
    public Texture texture() {
        return texture;
    }
}
