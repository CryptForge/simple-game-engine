package me.cryptforge.game;

import me.cryptforge.engine.Application;
import me.cryptforge.engine.Renderer;
import me.cryptforge.engine.Texture;
import me.cryptforge.engine.input.InputAction;
import me.cryptforge.engine.input.KeyboardKey;
import me.cryptforge.engine.input.MouseButton;
import org.joml.Vector2f;

public class Main extends Application {

    private final Vector2f position;
    private final float sizeX = 50f ,sizeY = 50f;
    private Texture texture;
    double time = 0;


    public Main() {
        super("game engine test", 800, 480, false,60,true);
        position = new Vector2f(0,0);
    }

    @Override
    public void init() {
        texture = new Texture("assets/textures/test.png");
    }

    @Override
    public void update() {
        time += 0.05;
        final Vector2f movement = new Vector2f(0,0);
        if(isKeyPressed(KeyboardKey.A)) {
            movement.x -= 1;
        }
        if(isKeyPressed(KeyboardKey.D)) {
            movement.x += 1;
        }
        if(isKeyPressed(KeyboardKey.W)) {
            movement.y -= 1;
        }
        if(isKeyPressed(KeyboardKey.S)) {
            movement.y += 1;
        }

        if(movement.lengthSquared() == 0)
            return;

        movement.normalize();

        movement.mul(2.5f);

        if(position.x + movement.x < 0 || position.x + movement.x + sizeX > getWorldWidth()) {
            movement.x = 0;
        }
        if(position.y + movement.y < 0 || position.y + movement.y + sizeY > getWorldHeight()) {
            movement.y = 0;
        }

        position.add(movement);
    }

    @Override
    public void onKey(KeyboardKey key, InputAction action) {
        if(key == KeyboardKey.ESC && action == InputAction.PRESSED) {
            exit();
        }
    }

    @Override
    public void onMouse(MouseButton button, InputAction action, float x, float y) {
        if(action != InputAction.PRESSED)
            return;
        position.x = x - sizeX / 2;
        position.y = y - sizeY / 2;
    }

    @Override
    public void render(Renderer renderer) {
        renderer.clear(0, 0, 120, 1f);

        for (int i = 0; i < 11; i++) {
            int x = i * 75;
            for (int j = 0; j < 8; j++) {
                int y = j * 75;
                renderer.sprite(texture)
                        .position(x, y)
                        .size(75, 75)
                        .color(Math.sin(time + x + y) * 0.75,-Math.sin(time + x + y) * 0.75,0)
                        .draw();
            }
        }

        renderer.sprite(texture)
                .position(position.x,position.y)
                .size(sizeX,sizeY)
                .draw();
    }

    public static void main(String[] args) {
        new Main().run();
    }
}