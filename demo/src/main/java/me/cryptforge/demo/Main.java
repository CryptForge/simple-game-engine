package me.cryptforge.demo;

import me.cryptforge.engine.Engine;
import me.cryptforge.engine.GameSettings;
import org.joml.Vector2i;

public class Main {

    public static void main(String[] args) {
        final Engine engine = Engine.init(
                new DemoGame(),
                GameSettings.builder()
                            .title("simple-game-engine demo")
                            .worldSize(new Vector2i(1920, 1080))
                            .windowSize(new Vector2i(854, 480))
                            .targetFps(60)
                            .updateRate(60)
                            .resizeable(true)
                            .build()
        );
        engine.run();
    }

}
