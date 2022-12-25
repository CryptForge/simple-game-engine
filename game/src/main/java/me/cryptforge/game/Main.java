package me.cryptforge.game;

import me.cryptforge.engine.Application;
import me.cryptforge.engine.asset.*;
import me.cryptforge.engine.input.InputAction;
import me.cryptforge.engine.input.KeyboardKey;
import me.cryptforge.engine.input.MouseButton;
import me.cryptforge.engine.render.Color;
import me.cryptforge.engine.render.Renderer;
import org.joml.Vector2f;

public class Main extends Application {


    private final Vector2f position;
    private final Color purple = new Color(154, 31, 242, 0.6f);
    private final float sizeX = 50f, sizeY = 50f;
    private Texture texture;
    private Font font;


    public Main() {
        super("game engine test", 800, 480, true, 60, true);
        position = new Vector2f(0, 0);
    }

    @Override
    public void init() {
        texture = AssetManager.loadTexture(
                "test",
                AssetPathType.FILE,
                "assets/textures/test.png",
                TextureSettings.builder()
                               .generateMipmap(true)
                               .downscaleFilter(TextureFilter.LINEAR_MIPMAP_LINEAR)
                               .build()
        );

        font = AssetManager.loadFont("font", AssetPathType.FILE, "assets/fonts/NotoSans-Regular.ttf", 48);
    }

    @Override
    public void update() {
        final Vector2f movement = new Vector2f(0, 0);
        if (isKeyPressed(KeyboardKey.A)) {
            movement.x -= 1;
        }
        if (isKeyPressed(KeyboardKey.D)) {
            movement.x += 1;
        }
        if (isKeyPressed(KeyboardKey.W)) {
            movement.y -= 1;
        }
        if (isKeyPressed(KeyboardKey.S)) {
            movement.y += 1;
        }

        if (movement.lengthSquared() == 0)
            return;

        movement.normalize();

        movement.mul(2.5f);

        if (position.x + movement.x < 0 || position.x + movement.x + sizeX > getWorldWidth()) {
            movement.x = 0;
        }
        if (position.y + movement.y < 0 || position.y + movement.y + sizeY > getWorldHeight()) {
            movement.y = 0;
        }

        position.add(movement);
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
    public void onMouse(MouseButton button, InputAction action, float x, float y) {
        if (action != InputAction.PRESSED)
            return;
        position.y = y;
        position.x = x;
    }

    @Override
    public void render(Renderer renderer) {
        renderer.clear(0, 0, 120, 1f);

        renderer.spriteBatch(texture, batch -> {
            final int size = 50;
            for (int i = 0; i < 12; i++) {
                int x = i * size;
                for (int j = 0; j < 8; j++) {
                    int y = j * size;
                    batch.drawSprite(x, y, size, size, Color.GREEN);
                }
            }
        });

        renderer.shapeBatch(batch -> {
            batch.drawRectangle(position.x, position.y, sizeX, sizeY, Color.RED);
            batch.drawRectangle(10, 420, font.getWidth("Hello World"), font.getAscent() * font.getScale(), purple);
        });

        renderer.textBatch(font, batch -> {
            batch.drawText("Hello World", 10, 420, Color.BLACK);
        });

    }

    public static void main(String[] args) {
        new Main().run();
    }
}