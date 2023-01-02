package me.cryptforge.game;

import me.cryptforge.engine.asset.Texture;
import me.cryptforge.engine.world.GameObject;

public final class TestObject extends GameObject {

    public TestObject(Texture texture, float x, float y, float scale) {
        transform().setPosition(x,y).setScale(scale,scale);
        setTexture(texture);
    }

    @Override
    public void update() {
        transform().add((float) Math.random() - 0.5f, (float) Math.random() - 0.5f);
    }
}
