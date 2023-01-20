package me.cryptforge.engine.input.listener;

import me.cryptforge.engine.input.InputButton;
import me.cryptforge.engine.input.InputState;

public class FilteredListener implements InputListener {

    private final InputButton button;
    private final InputState state;
    private final Runnable handler;

    FilteredListener(InputButton button, InputState state, Runnable handler) {
        this.button = button;
        this.state = state;
        this.handler = handler;
    }

    @Override
    public void handle(InputButton button, InputState state) {
        if(this.button == button && this.state == state) {
            handler.run();
        }
    }
}
