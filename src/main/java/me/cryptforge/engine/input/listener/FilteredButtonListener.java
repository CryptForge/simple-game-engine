package me.cryptforge.engine.input.listener;

import me.cryptforge.engine.input.InputButton;
import me.cryptforge.engine.input.InputState;

import java.util.function.Consumer;

final class FilteredButtonListener implements InputListener {

    private final InputButton button;
    private final Consumer<InputState> handler;

    FilteredButtonListener(InputButton button, Consumer<InputState> handler) {
        this.button = button;
        this.handler = handler;
    }

    @Override
    public void handle(InputButton button, InputState state) {
        if(this.button == button) {
            handler.accept(state);
        }
    }
}
