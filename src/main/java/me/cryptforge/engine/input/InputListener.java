package me.cryptforge.engine.input;

import me.cryptforge.engine.Engine;

public interface InputListener {

    default void handleInput(InputButton button, InputState state, InputModifiers modifiers) {
    }

    default void handleChar(char character) {
    }

    default void registerInput() {
        Engine.input().registerListener(this);
    }

    default void unregisterInput() {
        Engine.input().unregisterListener(this);
    }
}
