package me.cryptforge.engine;

import me.cryptforge.engine.input.InputAction;
import me.cryptforge.engine.input.KeyboardKey;
import me.cryptforge.engine.input.MouseButton;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.opengl.GL33.*;

public abstract class Application {

    private final long windowId;
    private final int worldWidth, worldHeight;
    private int width, height;
    private final Map<Integer, Integer> keyboardMap;
    private final Map<Integer, Integer> mouseMap;
    private Renderer renderer;

    private int targetFramerate;
    private boolean vsync;

    /**
     * Creates a window
     *
     * @param title           Window title
     * @param width           Initial window width
     * @param height          Initial window height
     * @param resizeable      Can window be resized
     * @param targetFramerate Framerate cap
     * @param vsync           V-Sync
     */
    public Application(String title, int width, int height, boolean resizeable, int targetFramerate, boolean vsync) {
        this.worldWidth = width;
        this.worldHeight = height;
        this.width = width;
        this.height = height;
        this.targetFramerate = targetFramerate;
        this.vsync = vsync;

        keyboardMap = new HashMap<>();
        mouseMap = new HashMap<>();

        // Init GLFW
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit())
            throw new RuntimeException("Failed to initialize GLFW");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, resizeable ? GLFW_TRUE : GLFW_FALSE);

        windowId = glfwCreateWindow(width, height, title, NULL, NULL);

        if (windowId == NULL)
            throw new RuntimeException("Failed to create GLFW window");
    }

    /**
     * Used to initialize things that require OpenGL to be active
     */
    abstract public void init();

    /**
     * Called every time the window needs to be rendered
     */
    abstract public void render(Renderer renderer);

    /**
     * Called a fixed number of times per second
     */
    abstract public void update();

    /**
     * Called on a keyboard event
     *
     * @param key    Keyboard key
     * @param action Action type
     */
    public void onKey(KeyboardKey key, InputAction action) {
    }

    /**
     * Called on a mouse event
     *
     * @param button Mouse button
     * @param action Action type
     * @param x      Clicked coordinate in world space
     * @param y      Clicked coordinate in world space
     */
    public void onMouse(MouseButton button, InputAction action, float x, float y) {
    }

    public void run() {
        // Setup and show window
        center();
        glfwMakeContextCurrent(windowId);
        setVsync(vsync);

        glfwShowWindow(windowId);

        glfwSetKeyCallback(windowId, this::handleKeyboard);
        glfwSetMouseButtonCallback(windowId, this::handleMouse);

        // Prepare rendering
        GL.createCapabilities();

        glViewport(0, 0, width, height);

        // Update viewport on resize
        glfwSetFramebufferSizeCallback(windowId, (window, newWidth, newHeight) -> {
            glViewport(0, 0, newWidth, newHeight);
            width = newWidth;
            height = newHeight;
        });

        // Call user defined init
        init();

        renderer = new Renderer(this);

        double oldTime = glfwGetTime();
        double accumulator = 0;

        // Start loop
        while (!glfwWindowShouldClose(windowId)) {
            double deltaTime = glfwGetTime() - oldTime;
            oldTime = glfwGetTime();

            if (Math.abs(deltaTime - 1.0 / 60.0) < .0002) {
                deltaTime = 1.0 / 60.0;
            }
            accumulator += deltaTime;

            while (accumulator >= 1.0 / 60.0) {
                update();
                accumulator -= 1.0 / 60.0;
            }

            render(renderer);

            glfwPollEvents();
            glfwSwapBuffers(windowId);
        }

        glfwFreeCallbacks(windowId);
        glfwDestroyWindow(windowId);

        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void handleKeyboard(long window, int key, int scancode, int action, int mods) {
        final InputAction inputAction = InputAction.fromGlfw(action);
        final KeyboardKey input = KeyboardKey.fromGlfw(key);

        if (inputAction != InputAction.REPEAT) {
            keyboardMap.put(key, action);
        }

        if (input == null) {
            System.err.println("Key \"" + key + "\" not supported");
            return;
        }

        onKey(input, inputAction);
    }

    private void handleMouse(long window, int button, int action, int mods) {
        final InputAction inputAction = InputAction.fromGlfw(action);
        final MouseButton mouseButton = MouseButton.fromGlfw(button);

        if (mouseButton == null) {
            System.err.println("Mouse Button \"" + button + "\" not supported");
            return;
        }
        mouseMap.put(button, action);

        final double x, y;
        try (final MemoryStack stack = stackPush()) {
            final DoubleBuffer bPosX = stack.mallocDouble(1);
            final DoubleBuffer bPosY = stack.mallocDouble(1);

            glfwGetCursorPos(window, bPosX, bPosY);

            x = bPosX.get(0);
            y = bPosY.get(0);
        }

        final Vector2f worldPos = renderer.convertMouseToWorld((float) x, (float) y);

        onMouse(mouseButton, inputAction, worldPos.x, worldPos.y);
    }

    /**
     * Stops the game loop
     */
    public void exit() {
        glfwSetWindowShouldClose(windowId, true);
    }

    /**
     * Checks if a key is pressed
     *
     * @param key Key to check
     * @return If the key is currently pressed
     */
    public boolean isKeyPressed(@NotNull KeyboardKey key) {
        if (!keyboardMap.containsKey(key.glfwKey()))
            return false;
        return keyboardMap.get(key.glfwKey()) == GLFW_PRESS;
    }

    public boolean isMouseButtonPressed(@NotNull MouseButton button) {
        if (!mouseMap.containsKey(button.glfwKey()))
            return false;
        return mouseMap.get(button.glfwKey()) == GLFW_PRESS;
    }

    public Vector2f getMousePosition() {
        try (final MemoryStack stack = MemoryStack.stackPush()) {
            final DoubleBuffer xBuffer = stack.mallocDouble(1);
            final DoubleBuffer yBuffer = stack.mallocDouble(1);

            glfwGetCursorPos(windowId, xBuffer, yBuffer);

            return renderer.convertMouseToWorld((float) xBuffer.get(0), (float) yBuffer.get(0));
        }
    }

    /**
     * Sets the position of the window
     */
    public void setWindowPosition(int x, int y) {
        glfwSetWindowPos(windowId, x, y);
    }

    /**
     * Centers the window
     */
    public void center() {
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            glfwGetWindowSize(windowId, pWidth, pHeight);

            GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            setWindowPosition(
                    (vidMode.width() - pWidth.get(0)) / 2,
                    (vidMode.height() - pHeight.get(0)) / 2
            );
        }
    }

    public void setTargetFramerate(int targetFramerate) {
        this.targetFramerate = targetFramerate;
    }

    /**
     * Enables or disables v-sync
     *
     * @param vsync State
     */
    public void setVsync(boolean vsync) {
        this.vsync = vsync;
        glfwSwapInterval(vsync ? 1 : 0);
    }

    /**
     * Gets the width of the window
     * Can change if window is resizeable
     *
     * @return Window width
     */
    public int getWidth() {
        return width;
    }

    /**
     * Gets the height of the window
     * Can change if window is resizeable
     *
     * @return Window height
     */
    public int getHeight() {
        return height;
    }

    /**
     * Gets the width of the game world
     *
     * @return World width
     */
    public int getWorldWidth() {
        return worldWidth;
    }

    /**
     * Gets the height of the game world
     *
     * @return World height
     */
    public int getWorldHeight() {
        return worldHeight;
    }

    /**
     * Gets the window handle
     *
     * @return Window handle
     */
    public long getWindowId() {
        return windowId;
    }
}
