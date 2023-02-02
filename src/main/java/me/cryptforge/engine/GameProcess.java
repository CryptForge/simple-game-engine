package me.cryptforge.engine;

import me.cryptforge.engine.asset.Assets;
import me.cryptforge.engine.render.Renderer;
import me.cryptforge.engine.system.InputSystem;
import me.cryptforge.engine.system.WindowSystem;
import me.cryptforge.engine.system.impl.InputSystemImpl;
import me.cryptforge.engine.system.impl.WindowSystemImpl;
import me.cryptforge.engine.util.Sync;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

final class GameProcess {

    private final long windowId;
    private final int targetFramerate;
    private final double updateDelta;
    private final Game game;
    private final WindowSystem windowSystem;
    private final InputSystem inputSystem;

    GameProcess(Game game, GameSettings settings) {
        this.game = game;
        this.targetFramerate = settings.targetFps();

        updateDelta = 1.0 / settings.updateRate();

        // Init GLFW
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit())
            throw new RuntimeException("Failed to initialize GLFW");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 6);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, settings.resizable() ? GLFW_TRUE : GLFW_FALSE);

        windowId = glfwCreateWindow(settings.windowSize().x, settings.windowSize().y, settings.title(), NULL, NULL);

        if (windowId == NULL)
            throw new RuntimeException("Failed to create GLFW window");

        Runtime.getRuntime().addShutdownHook(new Thread(this::exit));

        this.windowSystem = new WindowSystemImpl(
                windowId,
                settings.windowSize().x,
                settings.windowSize().y,
                settings.worldSize().x,
                settings.worldSize().y
        );
        this.inputSystem = new InputSystemImpl(windowId);
    }

    public void run() {
        // Weird hack to increase Thread.sleep accuracy
        final Thread accuracyThread = new Thread(() -> {
            try {
                Thread.sleep(Long.MAX_VALUE);
            } catch (InterruptedException ignored) {
            }
        });
        accuracyThread.setDaemon(false);
        accuracyThread.start();

        // Setup and show window
        windowSystem.center();
        glfwMakeContextCurrent(windowId);

        glfwShowWindow(windowId);

        // Prepare rendering
        GL.createCapabilities();

        windowSystem.resize(windowSystem.width(), windowSystem.height());

        // Update viewport on resize
        glfwSetFramebufferSizeCallback(windowId, (window, newWidth, newHeight) -> {
            windowSystem.resize(newWidth, newHeight);
        });

        // Call user defined init
        game.init();

        Renderer renderer = new Renderer();

        double oldTime = glfwGetTime();
        double accumulator = 0;

        // Start loop
        while (!glfwWindowShouldClose(windowId)) {
            double deltaTime = Sync.snapDelta(glfwGetTime() - oldTime);
            oldTime = glfwGetTime();
            accumulator += deltaTime;

            glfwPollEvents();

            while (accumulator >= updateDelta) {
                game.update();
                accumulator -= updateDelta;
            }

            game.render(renderer);

            glfwSwapBuffers(windowId);

            Sync.sync(targetFramerate);
        }

        glfwFreeCallbacks(windowId);
        glfwDestroyWindow(windowId);

        glfwTerminate();
        glfwSetErrorCallback(null).free();

        Assets.freeAll();
        renderer.free();

        accuracyThread.interrupt();

        game.onClose();
    }

    public void exit() {
        glfwSetWindowShouldClose(windowId, true);
    }

    public WindowSystem windowSystem() {
        return windowSystem;
    }

    public InputSystem inputSystem() {
        return inputSystem;
    }
}
