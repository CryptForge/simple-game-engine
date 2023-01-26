package me.cryptforge.engine.system;

import me.cryptforge.engine.input.InputButton;
import me.cryptforge.engine.input.InputListener;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;

public interface InputSystem extends EngineSystem {

    @NotNull Vector2f mousePosition();

    @NotNull Vector2f rawMousePosition();

    boolean isPressed(@NotNull InputButton button);

    void registerListener(@NotNull InputListener listener);

    void unregisterListener(@NotNull InputListener listener);
}
