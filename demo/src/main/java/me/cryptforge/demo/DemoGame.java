package me.cryptforge.demo;

import me.cryptforge.engine.Engine;
import me.cryptforge.engine.Game;
import me.cryptforge.engine.asset.Asset;
import me.cryptforge.engine.asset.Assets;
import me.cryptforge.engine.asset.TextureFilter;
import me.cryptforge.engine.asset.TextureSettings;
import me.cryptforge.engine.asset.type.Font;
import me.cryptforge.engine.input.InputButton;
import me.cryptforge.engine.input.InputListener;
import me.cryptforge.engine.input.InputModifiers;
import me.cryptforge.engine.input.InputState;
import me.cryptforge.engine.render.Color;
import me.cryptforge.engine.render.Renderer;
import me.cryptforge.engine.ui.FlexBundle;
import me.cryptforge.engine.ui.FlexDirection;
import me.cryptforge.engine.ui.component.Component;

import java.util.HashSet;
import java.util.Set;

public class DemoGame implements Game, InputListener {

    private final Set<TestObject> objects = new HashSet<>();
    private final Color clearColor = new Color(0, 0, 120, 1f);
    private FlexBundle ui;
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
            loader.texture("button", Asset.internal("textures/button.png"), TextureSettings.pixelSettings());
            loader.texture(
                    "debug",
                    Asset.internal("textures/debug_texture.png"),
                    TextureSettings.builder().filter(TextureFilter.NEAREST).build()
            );
            loader.font("font_96", Asset.internal("fonts/NotoSans-Regular.ttf"), 96);
            loader.font("font_48", Asset.internal("fonts/NotoSans-Regular.ttf"), 48);
        });

        final Font fontBig = Assets.font("font_96");
        final Font fontSmall = Assets.font("font_48");

        ui = new FlexBundle();
        ui.setDirection(FlexDirection.COLUMN);

        final var inputField = Component.inputField(fontBig, System.out::println);
        ui.add(FlexBundle.of(Component.label(fontBig, "Enter value: "), inputField));
        ui.add(Component.button(Assets.texture("button"), fontBig, "Press Me!", 384, () -> System.out.println("Pressed!")));
        ui.add(Component.label(fontSmall, "Bottom Text"));

        ui.registerInput();
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

        renderer.textBatch(Assets.font("font_96"), batch -> {
            batch.drawTextCentered("Time: " + time, Engine.window().worldWidth() / 2f, 80, Color.GREEN);
        });

        ui.render(renderer, 10, 10);
    }

    @Override
    public void onClose() {
        ui.unregisterInput();
    }
}