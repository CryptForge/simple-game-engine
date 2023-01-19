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
    private double time;

    public Main() {
        super("game engine test", 1920, 1080, true, true, 60, 60);
    }

    @Override
    public void init() {
        setWindowSize(854, 480);

        Assets.load(loader -> {
            loader.texture("test", Asset.internal("textures/test.png"),
                    TextureSettings.builder()
                                   .mipmap(true)
                                   .filter(TextureFilter.LINEAR_MIPMAP_LINEAR)
                                   .build());
            loader.texture("button", Asset.internal("textures/button.png"), TextureSettings.defaultSettings());
            loader.texture(
                    "debug",
                    Asset.internal("textures/debug_texture.png"),
                    TextureSettings.pixelSettings()
            );
            loader.font("font", Asset.internal("fonts/NotoSans-Regular.ttf"), 96);
        });

        setClearColor(new Color(0, 0, 120, 1f));

        final int size = 40;
        for (int x = 0; x < 100; x++) {
            for (int y = 0; y < 50; y++) {
                final TestObject object = new TestObject(Assets.texture("test"), x * size, y * size, size);
                objects.add(object);
            }
        }

        center();
    }

    @Override
    public void update() {
        time += 1f;
        objects.forEach(TestObject::update);
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

        renderer.spriteBatch(Assets.texture("debug"), batch -> {
            batch.drawSpriteRegion(
                    800,
                    400,
                    500,
                    500,
                    16,
                    16,
                    32,
                    32,
                    0,
                    Color.WHITE
            );
        });

        renderer.textBatch(Assets.font("font"), batch -> {
            batch.drawTextCentered("Time: " + time, getWorldWidth() / 2f, 80, Color.GREEN);
        });

        renderer.shapeBatch(batch -> {
            batch.drawSquare(100, 100, 100, Color.RED);
        });
    }

    public static void main(String[] args) {
        new Main().run();
    }
}