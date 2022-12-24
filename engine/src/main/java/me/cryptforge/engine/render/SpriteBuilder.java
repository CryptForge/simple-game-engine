package me.cryptforge.engine.render;

import me.cryptforge.engine.asset.Texture;

/**
 * Builder to simplify sprite drawing
 */
public class SpriteBuilder {

    private final Renderer renderer;
    private Texture texture;

    private float posX, posY;
    private float sizeX = 10f, sizeY = 10f;
    private float r = 1f, g = 1f, b = 1f, a = 1f;

    protected SpriteBuilder(Renderer renderer) {
        this.renderer = renderer;
    }

    public SpriteBuilder texture(Texture texture) {
        this.texture = texture;
        return this;
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

    public SpriteBuilder color(Color color) {
        this.r = color.r();
        this.g = color.g();
        this.b = color.b();
        this.a = color.a();
        return this;
    }

    public SpriteBuilder reset() {
        texture = null;
        posX = 0f;
        posY = 0f;
        sizeX = 10f;
        sizeY = 10f;
        r = 1f;
        g = 1f;
        b = 1f;

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
                r, g, b, a
        );
    }

}
