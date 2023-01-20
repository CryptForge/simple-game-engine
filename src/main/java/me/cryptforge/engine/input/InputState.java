package me.cryptforge.engine.input;

import static org.lwjgl.glfw.GLFW.*;

public enum InputState {
    PRESSED,RELEASED,REPEAT;

    public static InputState fromGlfw(int action) {
        return switch (action) {
            case GLFW_REPEAT -> REPEAT;
            case GLFW_PRESS -> PRESSED;
            case GLFW_RELEASE -> RELEASED;
            default -> throw new IllegalArgumentException("Unknown action \"" + action + "\"");
        };
    }
}
