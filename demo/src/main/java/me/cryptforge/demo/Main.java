package me.cryptforge.demo;

import me.cryptforge.engine.Application;
import me.cryptforge.engine.asset.Asset;
import me.cryptforge.engine.asset.Assets;
import me.cryptforge.engine.asset.TextureFilter;
import me.cryptforge.engine.asset.TextureSettings;
import me.cryptforge.engine.input.InputAction;
import me.cryptforge.engine.input.KeyboardKey;
import me.cryptforge.engine.render.Color;
import me.cryptforge.engine.render.Renderer;

import java.util.HashSet;
import java.util.Set;

public class Main extends Application {

    private final Set<TestObject> objects = new HashSet<>();
    private int counter;

    public Main() {
        super("game engine test", 1920, 1080, true, true, 60, 60);
    }

    @Override
    public void init() {
        setWindowSize(854, 480);

        Assets.load(loader -> {
            loader.texture("test", Asset.internal("textures/test.png"), TextureSettings.builder().generateMipmap(true).downscaleFilter(TextureFilter.LINEAR_MIPMAP_LINEAR).build());
            loader.texture("button", Asset.internal("textures/button.png"), TextureSettings.defaultSettings());
            loader.font("font", Asset.internal("fonts/NotoSans-Regular.ttf"), 96);
        });

        setClearColor(new Color(0, 0, 120, 1f));

        final int size = 20;
        for(int x = 0; x < 100; x++) {
            for(int y = 0; y < 50; y++) {
                objects.add(new TestObject(Assets.texture("test"),x * size, y * size, size));
            }
        }
    }

    @Override
    public void update() {
        objects.forEach(TestObject::update);

        counter++;
    }

    @Override
    public void onKey(KeyboardKey key, InputAction action) {
        if (action != InputAction.PRESSED)
            return;

        if (key == KeyboardKey.ESC) {
            exit();
        }
    }

    @Override
    public void render(Renderer renderer) {
        renderer.drawAll(objects);

        renderer.textBatch(Assets.font("font"), batch -> {
            batch.drawTextCentered("Counter: " + counter, getWorldWidth() / 2f, 80, Color.GREEN);
        });
    }

    public static void main(String[] args) {
        new Main().run();
    }
}