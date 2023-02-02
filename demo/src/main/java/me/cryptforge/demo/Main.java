package me.cryptforge.demo;

import me.cryptforge.engine.Engine;
import me.cryptforge.engine.GameSettings;

public class Main {

    public static void main(String[] args) {
        final Engine engine = Engine.init(
                new DemoGame(),
                GameSettings.builder()
                            .title("simple-game-engine demo")
                            .worldSize(1920, 1080)
                            .windowSize(854, 480)
//                            .windowSize(1920, 1080)
                            .targetFps(60)
                            .updateRate(60)
                            .resizeable(true)
                            .build()
        );
        engine.run();
    }

}
