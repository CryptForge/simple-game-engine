package me.cryptforge.engine.system.impl;

import me.cryptforge.engine.Engine;
import me.cryptforge.engine.input.InputButton;
import me.cryptforge.engine.input.InputState;
import me.cryptforge.engine.input.InputListener;
import me.cryptforge.engine.system.InputSystem;
import me.cryptforge.engine.system.WindowSystem;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.lwjgl.system.MemoryStack;

import java.lang.ref.WeakReference;
import java.nio.DoubleBuffer;
import java.util.*;

import static org.lwjgl.glfw.GLFW.*;

public class InputSystemImpl implements InputSystem {

    private final long windowId;
    private Map<InputButton, InputState> stateMap;
    private List<WeakReference<InputListener>> listeners;

    public InputSystemImpl(long windowId) {
        this.windowId = windowId;
        this.stateMap = new HashMap<>();
        this.listeners = new ArrayList<>();
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
    public boolean isPressed(@NotNull InputButton button) {
        return stateMap.getOrDefault(button, InputState.RELEASED) == InputState.PRESSED;
    }

    @Override
    public void registerListener(@NotNull InputListener listener) {
        listeners.add(new WeakReference<>(listener));
    }

    @Override
    public void unregisterListener(@NotNull InputListener listener) {
        listeners.removeIf(reference -> {
            if (reference.get() == null || reference.get() == listener) {
                reference.clear();
                return true;
            }
            return false;
        });
    }

    private void handleInput(int code, int action) {
        final InputButton button = InputButton.fromGlfw(code);
        final InputState state = InputState.fromGlfw(action);

        if (button == null) {
            // TODO: Give some kind of warning
            return;
        }
        if (state != InputState.REPEAT) {
            stateMap.put(button, state);
        }

        triggerListeners(button, state);
    }

    private void triggerListeners(InputButton button, InputState state) {
        listeners.removeIf(ref -> ref.get() == null);

        listeners.forEach(ref -> Objects.requireNonNull(ref.get()).handleInput(button, state));
    }
}
