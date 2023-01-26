package me.cryptforge.engine.input;

import me.cryptforge.engine.Engine;

public interface InputListener {

    void handleInput (InputButton button, InputState state);

    default void registerInput() {
        Engine.input().registerListener(this);
    }

    default void unregisterInput() {
        Engine.input().unregisterListener(this);
    }
}
