package me.cryptforge.engine.system.impl;

import me.cryptforge.engine.Engine;
import me.cryptforge.engine.input.InputButton;
import me.cryptforge.engine.input.listener.InputListener;
import me.cryptforge.engine.input.InputState;
import me.cryptforge.engine.system.InputSystem;
import me.cryptforge.engine.system.WindowSystem;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.lwjgl.system.MemoryStack;

import java.nio.DoubleBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.lwjgl.glfw.GLFW.*;

public class InputSystemImpl implements InputSystem {

    private final long windowId;
    private final Map<Integer, Integer> stateMap;
    private final Set<InputListener> listeners;

    public InputSystemImpl(long windowId) {
        this.windowId = windowId;
        this.stateMap = new HashMap<>();
        this.listeners = new HashSet<>();
        glfwSetKeyCallback(windowId, (window, key, scancode, action, mods) -> handleInput(key, action));
        glfwSetMouseButtonCallback(windowId, (window, button, action, mods) -> handleInput(button, action));
    }

    @Override
    public @NotNull Vector2f mousePosition() {
        final WindowSystem window = Engine.window();
        final Vector2f rawMousePosition = rawMousePosition();
        return window.projectionMatrix()
                     .unproject(
                             rawMousePosition.x,
                             window.height() - rawMousePosition.y,
                             new int[]{0, 0, window.width(), window.height()},
                             new Vector2f()
                     );
    }

    @Override
    public @NotNull Vector2f rawMousePosition() {
        try (final MemoryStack stack = MemoryStack.stackPush()) {
            final DoubleBuffer xBuffer = stack.mallocDouble(1);
            final DoubleBuffer yBuffer = stack.mallocDouble(1);

            glfwGetCursorPos(windowId, xBuffer, yBuffer);

            return new Vector2f((float) xBuffer.get(0), (float) yBuffer.get(0));
        }
    }

    @Override
    public void addListener(InputListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public boolean isPressed(InputButton button) {
        if(!stateMap.containsKey(button.glfwCode())) {
            return false;
        }
        final int state = stateMap.get(button.glfwCode());
        return state == GLFW_PRESS;
    }

    private void handleInput(int code, int action) {
        final InputButton button = InputButton.fromGlfw(code);
        final InputState state = InputState.fromGlfw(action);

        if (state != InputState.REPEAT) {
            stateMap.put(code, action);
        }

        if (button == null) {
            // TODO: Give some kind of warning
            return;
        }

        for (InputListener listener : listeners) {
            listener.handle(button, state);
        }
    }
}
