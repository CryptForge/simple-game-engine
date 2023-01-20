package me.cryptforge.engine;

import me.cryptforge.engine.system.InputSystem;
import me.cryptforge.engine.system.WindowSystem;
import org.jetbrains.annotations.NotNull;

public final class Engine {

    private static GameProcess process;

    public void run() {
        process.run();
    }

    public static @NotNull WindowSystem window() {
        return process.windowSystem();
    }

    public static @NotNull InputSystem input() {
        return process.inputSystem();
    }

    public static void exit() {
        if (process == null) {
            throw new IllegalStateException();
        }
        process.exit();
    }

    public static Engine init(Game game, GameSettings settings) {
        if (process != null) {
            throw new IllegalStateException("Cannot init engine more than once!");
        }
        process = new GameProcess(game, settings);
        return new Engine();
    }

}
