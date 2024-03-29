package me.cryptforge.engine.input;

import static org.lwjgl.glfw.GLFW.*;

public enum InputButton {
    MOUSE_LEFT(GLFW_MOUSE_BUTTON_LEFT),
    MOUSE_MIDDLE(GLFW_MOUSE_BUTTON_MIDDLE),
    MOUSE_RIGHT(GLFW_MOUSE_BUTTON_RIGHT),
    A(GLFW_KEY_A),
    B(GLFW_KEY_B),
    C(GLFW_KEY_C),
    D(GLFW_KEY_D),
    E(GLFW_KEY_E),
    F(GLFW_KEY_F),
    G(GLFW_KEY_G),
    H(GLFW_KEY_H),
    I(GLFW_KEY_I),
    J(GLFW_KEY_J),
    K(GLFW_KEY_K),
    L(GLFW_KEY_L),
    M(GLFW_KEY_M),
    N(GLFW_KEY_N),
    O(GLFW_KEY_O),
    P(GLFW_KEY_P),
    Q(GLFW_KEY_Q),
    R(GLFW_KEY_R),
    S(GLFW_KEY_S),
    T(GLFW_KEY_T),
    U(GLFW_KEY_U),
    V(GLFW_KEY_V),
    W(GLFW_KEY_W),
    X(GLFW_KEY_X),
    Y(GLFW_KEY_Y),
    Z(GLFW_KEY_Z),
    LEFT_SHIFT(GLFW_KEY_LEFT_SHIFT),
    RIGHT_SHIFT(GLFW_KEY_RIGHT_SHIFT),
    LEFT_ALT(GLFW_KEY_LEFT_ALT),
    RIGHT_ALT(GLFW_KEY_RIGHT_ALT),
    LEFT_CONTROL(GLFW_KEY_LEFT_CONTROL),
    RIGHT_CONTROL(GLFW_KEY_RIGHT_CONTROL),
    TAB(GLFW_KEY_TAB),
    CAPS_LOCK(GLFW_KEY_CAPS_LOCK),
    SPACE(GLFW_KEY_SPACE),
    LEFT(GLFW_KEY_LEFT),
    UP(GLFW_KEY_UP),
    DOWN(GLFW_KEY_DOWN),
    RIGHT(GLFW_KEY_RIGHT),
    ENTER(GLFW_KEY_ENTER),
    BACKSPACE(GLFW_KEY_BACKSPACE),
    DELETE(GLFW_KEY_DELETE),
    MINUS(GLFW_KEY_MINUS),
    EQUAL(GLFW_KEY_EQUAL),
    GRAVE_ACCENT(GLFW_KEY_GRAVE_ACCENT),
    LEFT_BRACKET(GLFW_KEY_LEFT_BRACKET),
    RIGHT_BRACKET(GLFW_KEY_RIGHT_BRACKET),
    SEMICOLON(GLFW_KEY_SEMICOLON),
    APOSTROPHE(GLFW_KEY_APOSTROPHE),
    BACKSLASH(GLFW_KEY_BACKSLASH),
    SLASH(GLFW_KEY_SLASH),
    COMMA(GLFW_KEY_COMMA),
    PERIOD(GLFW_KEY_PERIOD),
    NUM_1(GLFW_KEY_1),
    NUM_2(GLFW_KEY_2),
    NUM_3(GLFW_KEY_3),
    NUM_4(GLFW_KEY_4),
    NUM_5(GLFW_KEY_5),
    NUM_6(GLFW_KEY_6),
    NUM_7(GLFW_KEY_7),
    NUM_8(GLFW_KEY_8),
    NUM_9(GLFW_KEY_9),
    NUM_0(GLFW_KEY_0),
    NUMPAD_1(GLFW_KEY_KP_1),
    NUMPAD_2(GLFW_KEY_KP_2),
    NUMPAD_3(GLFW_KEY_KP_3),
    NUMPAD_4(GLFW_KEY_KP_4),
    NUMPAD_5(GLFW_KEY_KP_5),
    NUMPAD_6(GLFW_KEY_KP_6),
    NUMPAD_7(GLFW_KEY_KP_7),
    NUMPAD_8(GLFW_KEY_KP_8),
    NUMPAD_9(GLFW_KEY_KP_9),
    NUMPAD_0(GLFW_KEY_KP_0),
    NUMPAD_DIVIDE(GLFW_KEY_KP_DIVIDE),
    NUMPAD_MULTIPLY(GLFW_KEY_KP_MULTIPLY),
    NUMPAD_SUBTRACT(GLFW_KEY_KP_SUBTRACT),
    NUMPAD_ADD(GLFW_KEY_KP_ADD),
    NUMPAD_ENTER(GLFW_KEY_KP_ENTER),
    NUMPAD_DECIMAL(GLFW_KEY_KP_DECIMAL),
    NUM_LOCK(GLFW_KEY_NUM_LOCK),
    F1(GLFW_KEY_F1),
    F2(GLFW_KEY_F2),
    F3(GLFW_KEY_F3),
    F4(GLFW_KEY_F4),
    F5(GLFW_KEY_F5),
    F6(GLFW_KEY_F6),
    F7(GLFW_KEY_F7),
    F8(GLFW_KEY_F8),
    F9(GLFW_KEY_F9),
    F10(GLFW_KEY_F10),
    F11(GLFW_KEY_F11),
    F12(GLFW_KEY_F12),
    INSERT(GLFW_KEY_INSERT),
    PRINT_SCREEN(GLFW_KEY_PRINT_SCREEN),
    LEFT_SUPER(GLFW_KEY_LEFT_SUPER),
    RIGHT_SUPER(GLFW_KEY_RIGHT_SUPER),
    PAUSE(GLFW_KEY_PAUSE),
    HOME(GLFW_KEY_HOME),
    END(GLFW_KEY_END),
    MENU(GLFW_KEY_MENU),
    ESC(GLFW_KEY_ESCAPE),
    WORLD_1(GLFW_KEY_WORLD_1),
    WORLD_2(GLFW_KEY_WORLD_2),
    PAGE_UP(GLFW_KEY_PAGE_UP),
    PAGE_DOWN(GLFW_KEY_PAGE_DOWN),
    UNKNOWN(GLFW_KEY_UNKNOWN);

    private static final InputButton[] values = values();

    private final int glfwCode;

    InputButton(int glfwCode) {
        this.glfwCode = glfwCode;
    }

    public boolean isNumber() {
        return switch (this) {
            case NUM_0,NUM_1,NUM_2,NUM_3,NUM_4,NUM_5,NUM_6,NUM_7,NUM_8,NUM_9, NUMPAD_0, NUMPAD_1, NUMPAD_2, NUMPAD_3, NUMPAD_4, NUMPAD_5, NUMPAD_6, NUMPAD_7, NUMPAD_8, NUMPAD_9 -> true;
            default -> false;
        };
    }

    public boolean isShift() {
        return this == LEFT_SHIFT || this == RIGHT_SHIFT;
    }


    public int glfwCode() {
        return glfwCode;
    }

    public static InputButton fromGlfw(int key) {
        for (InputButton value : values) {
            if(value.glfwCode == key) {
                return value;
            }
        }
        return null;
    }
}
