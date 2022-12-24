package me.cryptforge.game;

import me.cryptforge.engine.render.RenderType;
import me.cryptforge.engine.render.Renderer;
import me.cryptforge.engine.asset.Texture;

public class Button {

    private Texture texture;
    private final int width,height;
    private final Runnable callback;
    private int x,y;

    public Button(Texture texture, int width, int height, int x, int y, Runnable callback) {
        this.texture = texture;
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        this.callback = callback;
    }

    public void draw(Renderer renderer) {
        renderer.begin(RenderType.SPRITE);
        renderer.sprite(texture)
                .position(x,y)
                .size(width,height)
                .draw();
        renderer.end();
    }

    public boolean isInBounds(int x, int y) {
        return x > this.x && x < this.x + width && y > this.y && y < this.y + height;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public Runnable getCallback() {
        return callback;
    }
}
