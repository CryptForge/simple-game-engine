package me.cryptforge.engine.input;

import static org.lwjgl.glfw.GLFW.*;

public enum MouseButton {
    LEFT(GLFW_MOUSE_BUTTON_LEFT),
    MIDDLE(GLFW_MOUSE_BUTTON_MIDDLE),
    RIGHT(GLFW_MOUSE_BUTTON_RIGHT);

    private static final MouseButton[] values = values();

    private final int glfwKey;

    MouseButton(int glfwKey) {
        this.glfwKey = glfwKey;
    }

    public int glfwKey() {
        return glfwKey;
    }

    public int scancode() {
        return glfwGetKeyScancode(glfwKey);
    }

    public static MouseButton fromGlfw(int key) {
        for (MouseButton value : values) {
            if(value.glfwKey == key) {
                return value;
            }
        }
        return null;
    }
}
