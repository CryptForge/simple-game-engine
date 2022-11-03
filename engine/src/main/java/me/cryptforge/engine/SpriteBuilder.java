package me.cryptforge.engine;

import org.jetbrains.annotations.ApiStatus;
import org.joml.Vector2f;
import org.joml.Vector3d;
import org.joml.Vector3f;

/**
 * Builder to simplify sprite drawing
 */
@ApiStatus.AvailableSince("1.0")
public class SpriteBuilder {

    private final Renderer renderer;
    private final Texture texture;

    private float posX, posY;
    private float sizeX = 10f, sizeY = 10f;
    private float r = 1f, g = 1f, b = 1f;
    private float rotation;

    protected SpriteBuilder(Renderer renderer, Texture texture) {
        this.renderer = renderer;
        this.texture = texture;
    }

    public SpriteBuilder position(float x, float y) {
        posX = x;
        posY = y;
        return this;
    }

    public SpriteBuilder size(float x, float y) {
        sizeX = x;
        sizeY = y;
        return this;
    }

    public SpriteBuilder rotation(float rotation) {
        this.rotation = rotation;
        return this;
    }

    public SpriteBuilder color(float r, float g, float b) {
        this.r = r;
        this.g = g;
        this.b = b;
        return this;
    }

    public SpriteBuilder color(double r, double g, double b) {
        this.r = (float) r;
        this.g = (float) g;
        this.b = (float) b;
        return this;
    }

    /**
     * Draws the constructed sprite
     */
    public void draw() {
        renderer.drawSprite(
                texture,
                posX, posY,
                sizeX, sizeY,
                rotation,
                r, g, b
        );
    }

}
