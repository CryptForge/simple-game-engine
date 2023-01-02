package me.cryptforge.game;

import me.cryptforge.engine.Application;
import me.cryptforge.engine.asset.AssetManager;
import me.cryptforge.engine.asset.Texture;
import me.cryptforge.engine.render.Color;
import me.cryptforge.engine.render.Renderer;
import me.cryptforge.engine.world.Scene;

public final class TestScene extends Scene {

    private final Application application;

    public TestScene(Application application) {
        this.application = application;
    }

    @Override
    public void init() {
        final Texture testTexture = AssetManager.getTexture("test");
        final Texture buttonTexture = AssetManager.getTexture("button");

        final float offsetX = 0;
        final float offsetY = 200;

        final int size = 30;
        for (int i = 0; i < 100; i++) {
            int x = i * size;
            for (int j = 0; j < 20; j++) {
                int y = j * size;
                addGameObject(new TestObject(testTexture,x,y,size));
                addGameObject(new TestObject(buttonTexture,x + offsetX,y + offsetY,size));
            }
        }
    }

    @Override
    public void render(Renderer renderer) {
        super.render(renderer);

        final float x = application.getWorldWidth() / 2f;
        final float y = application.getWorldHeight() - 80f;
        renderer.textBatch(AssetManager.getFont("font"), batch -> {
            batch.drawTextCentered("Objects: " + gameObjects().size(), x, y, Color.GREEN);
        });
    }

    @Override
    public void terminate() {

    }

    @Override
    public String toString() {
        return "Test Scene";
    }
}
