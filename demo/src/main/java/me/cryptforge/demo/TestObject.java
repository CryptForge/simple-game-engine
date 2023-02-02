package me.cryptforge.demo;

import me.cryptforge.engine.Drawable;
import me.cryptforge.engine.Engine;
import me.cryptforge.engine.asset.type.Texture;
import me.cryptforge.engine.input.InputButton;
import me.cryptforge.engine.render.Color;
import org.joml.Matrix3x2f;
import org.joml.Vector2f;

public final class TestObject implements Drawable {

    private final Matrix3x2f transform;
    private final Texture texture;
    private final Color color;
    private final Vector2f velocity;

    public TestObject(Texture texture, float x, float y, float scale) {
        this.texture = texture;
        this.color = Color.WHITE;
        this.transform = new Matrix3x2f().translation(x, y).scale(scale, scale);
        this.velocity = new Vector2f(0,0);
    }

    public void update() {
        if(Engine.input().isPressed(InputButton.SPACE)) {
            transform()
                    .rotateAbout(0.01f, 0.5f, 0.5f);
        }
    }

    public void setVelocity(float x, float y) {
        this.velocity.x = x;
        this.velocity.y = y;
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
