package me.cryptforge.demo;

import me.cryptforge.demo.ui.InputField;
import me.cryptforge.engine.Game;
import me.cryptforge.engine.Engine;
import me.cryptforge.engine.asset.Asset;
import me.cryptforge.engine.asset.Assets;
import me.cryptforge.engine.asset.TextureFilter;
import me.cryptforge.engine.asset.TextureSettings;
import me.cryptforge.engine.input.InputButton;
import me.cryptforge.engine.input.InputListener;
import me.cryptforge.engine.input.InputModifiers;
import me.cryptforge.engine.input.InputState;
import me.cryptforge.engine.render.Color;
import me.cryptforge.engine.render.Renderer;

import java.util.HashSet;
import java.util.Set;

public class DemoGame implements Game, InputListener {

    private final Set<TestObject> objects = new HashSet<>();
    private final Color clearColor = new Color(0, 0, 120, 1f);
    private InputField inputField;
    private double time;

    @Override
    public void init() {
        Assets.load(loader -> {
            loader.texture("test", Asset.internal("textures/test.png"),
                    TextureSettings.builder()
                                   .mipmap(true)
                                   .filter(TextureFilter.LINEAR_MIPMAP_LINEAR)
                                   .build()
            );
            loader.texture("button", Asset.internal("textures/button.png"), TextureSettings.defaultSettings());
            loader.texture(
                    "debug",
                    Asset.internal("textures/debug_texture.png"),
                    TextureSettings.builder().filter(TextureFilter.NEAREST).build()
            );
            loader.font("font", Asset.internal("fonts/NotoSans-Regular.ttf"), 96);
        });

        final int size = 50;
        for (int x = 0; x < 50; x++) {
            for (int y = 0; y < 10; y++) {
                final TestObject object = new TestObject(Assets.texture("test"), x * size, y * size, size);
                objects.add(object);
            }
        }

        inputField = new InputField(Assets.font("font"), 100, 100, "", Color.WHITE, Color.BLACK, value -> {
            System.out.println(value);
            inputField.clear();
        });
        inputField.registerInput();

        registerInput();
    }

    @Override
    public void update() {
        time += 1f;
        objects.forEach(TestObject::update);
    }

    @Override
    public void handleInput(InputButton button, InputState state, InputModifiers modifiers) {
        if (button == InputButton.ESC) {
            System.out.println("Closing game");
            Engine.exit();
        }
    }

    @Override
    public void render(Renderer renderer) {
        renderer.clear(clearColor);

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
            batch.drawTextCentered("Time: " + time, Engine.window().worldWidth() / 2f, 80, Color.GREEN);
        });

        inputField.render(renderer);
    }

    @Override
    public void onClose() {

    }
}