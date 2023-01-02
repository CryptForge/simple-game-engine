package me.cryptforge.game;

import me.cryptforge.engine.Application;
import me.cryptforge.engine.asset.*;
import me.cryptforge.engine.input.InputAction;
import me.cryptforge.engine.input.KeyboardKey;
import me.cryptforge.engine.render.Color;
import me.cryptforge.engine.render.Renderer;

public class Main extends Application {

    private Font font;


    public Main() {
        super("game engine test", 1920, 1080, true, true, 60, 60);
    }

    @Override
    public void init() {
        setWindowSize(854,480);

        AssetManager.loadTexture(
                "test",
                AssetPathType.FILE,
                "assets/textures/test.png",
                TextureSettings.builder()
                               .generateMipmap(true)
                               .downscaleFilter(TextureFilter.LINEAR_MIPMAP_LINEAR)
                               .build()
        );

        AssetManager.loadTexture("button", AssetPathType.FILE, "assets/textures/button.png", TextureSettings.defaultSettings());

        font = AssetManager.loadFont("font", AssetPathType.FILE, "assets/fonts/NotoSans-Regular.ttf", 96);

        setScene(new TestScene(this));
        setClearColor(new Color(0,0,120,1f));
    }

    @Override
    public void update() {

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
        renderer.textBatch(font, batch -> {
            batch.drawTextCentered("Current Scene: " + getScene().toString(), getWorldWidth() / 2f, 80, Color.BLACK);
        });
    }

    public static void main(String[] args) {
        new Main().run();
    }
}