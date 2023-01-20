package me.cryptforge.engine.input.listener;

import me.cryptforge.engine.input.InputButton;
import me.cryptforge.engine.input.InputState;

import java.util.function.BiConsumer;

final class AnyListener implements InputListener {

    private final BiConsumer<InputButton, InputState> handler;

    AnyListener(BiConsumer<InputButton, InputState> handler) {
        this.handler = handler;
    }

    @Override
    public void handle(InputButton button, InputState state) {
        handler.accept(button, state);
    }
}
