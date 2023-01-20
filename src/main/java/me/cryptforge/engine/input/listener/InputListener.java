package me.cryptforge.engine.input.listener;

import me.cryptforge.engine.input.InputButton;
import me.cryptforge.engine.input.InputState;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface InputListener {

    void handle(InputButton button, InputState state);

    static InputListener any(BiConsumer<InputButton, InputState> handler) {
        return new AnyListener(handler);
    }

    static InputListener filtered(InputButton button, Consumer<InputState> handler) {
        return new FilteredButtonListener(button, handler);
    }

    static InputListener filtered(InputButton button, InputState state, Runnable handler) {
        return new FilteredListener(button, state, handler);
    }
}
