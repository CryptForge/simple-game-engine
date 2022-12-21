package me.cryptforge.game;

import me.cryptforge.engine.Application;
import me.cryptforge.engine.render.Renderer;
import me.cryptforge.engine.asset.*;
import me.cryptforge.engine.input.InputAction;
import me.cryptforge.engine.input.KeyboardKey;
import me.cryptforge.engine.input.MouseButton;
import org.joml.Vector2f;

public class Main extends Application {


    private final Vector2f position;
    private final float sizeX = 50f, sizeY = 50f;
    private Texture texture;
    private Texture buttonTexture;
    private Texture buttonPressedTexture;
    private Font font;
    private Button button;
    double time = 0;
    double timeSpeed = 0.05;


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


        final TextureSettings buttonSettings = TextureSettings.builder()
                                                              .upscaleFilter(TextureFilter.NEAREST)
                                                              .downscaleFilter(TextureFilter.NEAREST)
                                                              .build();
        buttonTexture = AssetManager.loadTexture("button", AssetPathType.FILE, "assets/textures/button.png", buttonSettings);
        buttonPressedTexture = AssetManager.loadTexture("button_pressed", AssetPathType.FILE, "assets/textures/button_pressed.png", buttonSettings);
        font = AssetManager.loadFont("font", AssetPathType.FILE, "assets/fonts/NotoSans-Regular.ttf", 24);
        button = new Button(
                buttonTexture,
                200, 40,
                10, 10,
                () -> {
                    System.out.println("Button pressed");
                }
        );
    }

    @Override
    public void update() {
        time += timeSpeed;

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

        if (key == KeyboardKey.UP) {
            timeSpeed += 0.01;
        }
        if (key == KeyboardKey.DOWN) {
            timeSpeed -= 0.01;
        }
    }

    @Override
    public void onMouse(MouseButton button, InputAction action, float x, float y) {
        if (action != InputAction.PRESSED)
            return;
        if (this.button.isInBounds((int) x, (int) y)) {
            this.button.getCallback().run();
            return;
        }
        position.y = y - sizeY / 2;
        position.x = x - sizeX / 2;

    }

    @Override
    public void render(Renderer renderer) {
        renderer.clear(0, 0, 120, 1f);

//        final Vector2f mousePos = getMousePosition();
//        if (button.isInBounds((int) mousePos.x, (int) mousePos.y)) {
//            button.setTexture(buttonPressedTexture);
//        } else {
//            button.setTexture(buttonTexture);
//        }
//
//        final int size = 75;
//        for (int i = 0; i < 12; i++) {
//            int x = i * size;
//            for (int j = 0; j < 8; j++) {
//                int y = j * size;
//                renderer.sprite(texture)
//                        .position(x, y)
//                        .size(size, size)
//                        .color(Math.sin(time + x + y) * 0.75f, -Math.sin(time + x + y) * 0.75f, 0)
//                        .draw();
//            }
//        }
//
//        renderer.sprite(texture)
//                .position(position.x, position.y)
//                .size(sizeX, sizeY)
//                .draw();
//
//        button.draw(renderer);
//
//        renderer.sprite(font.getBitmap())
//                .position(0, 50)
//                .size(50, 10)
//                .draw();
        renderer.drawText(font,"hello world",20, 20);

    }

    public static void main(String[] args) {
        new Main().run();
    }
}