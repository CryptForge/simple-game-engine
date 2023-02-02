package me.cryptforge.demo.ui;

import me.cryptforge.engine.asset.type.Font;
import me.cryptforge.engine.input.InputButton;
import me.cryptforge.engine.input.InputListener;
import me.cryptforge.engine.input.InputModifiers;
import me.cryptforge.engine.input.InputState;
import me.cryptforge.engine.render.Color;
import me.cryptforge.engine.render.Renderer;
import me.cryptforge.engine.util.MathUtil;

import java.util.function.Consumer;

public class InputField implements InputListener {

    private final Font font;
    private final float x, y;
    private final Color color;
    private final Color selectorColor;
    private final StringBuilder value;
    private final Consumer<String> onConfirm;
    private int selector = 0;

    public InputField(Font font, float x, float y, String defaultValue, Color color, Color selectorColor, Consumer<String> onConfirm) {
        this.font = font;
        this.color = color;
        this.selectorColor = selectorColor;
        this.value = new StringBuilder(defaultValue);
        this.onConfirm = onConfirm;
        this.x = x;
        this.y = y;
    }

    public void render(Renderer renderer) {
        renderer.textBatch(font, batch -> {
            batch.drawText(value.toString(), x, y, color);
        });

        renderer.shapeBatch(batch -> {
            final float x = this.x + font.getTextWidth(value.substring(0, selector));
            final float height = (font.getAscent() - font.getDescent()) * font.getScale();
            batch.drawRectangle(
                    x,
                    y,
                    4,
                    height,
                    selectorColor
            );
        });
    }

    @Override
    public void handleInput(InputButton button, InputState state, InputModifiers modifiers) {
        if (state == InputState.RELEASED) {
            return;
        }
        switch (button) {
            case ENTER -> onConfirm.accept(value.toString());
            case LEFT -> left(modifiers.isControlHeld());
            case RIGHT -> right(modifiers.isControlHeld());
            case BACKSPACE -> backspace(modifiers.isControlHeld());
            case DELETE -> delete();
        }
    }

    @Override
    public void handleChar(char character) {
        if (character == '\n') {
            return;
        }
        value.insert(selector, character);
        selector++;
    }

    public void setValue(String value) {
        clear();
        this.value.append(value);
    }

    public void clear() {
        this.value.delete(0, this.value.length());
        selector = 0;
    }

    private void left(boolean ctrl) {
        if (ctrl) {
            selector = leftCtrlTarget();
            return;
        }
        selector = MathUtil.clamp(selector - 1, 0, value.length());
    }

    private void right(boolean ctrl) {
        if (ctrl) {
            selector = rightCtrlTarget();
            return;
        }
        selector = MathUtil.clamp(selector + 1, 0, value.length());
    }

    private void backspace(boolean ctrl) {
        if (ctrl) {
            final int target = leftCtrlTarget();
            value.delete(target, selector);
            selector = target;
            return;
        }
        if (value.isEmpty()) {
            return;
        }
        if (selector == 0) {
            return;
        }
        value.delete(selector - 1, selector);
        selector--;
    }

    private void delete() {
        if (value.isEmpty()) {
            return;
        }
        value.delete(selector, selector + 1);
    }

    private int rightCtrlTarget() {
        for (int i = selector; i < value.length(); i++) {
            final char c = value.charAt(i);
            if (c == ' ' && i != selector) {
                return i + 1;
            }
        }
        return value.length();
    }

    private int leftCtrlTarget() {
        for (int i = selector - 1; i >= 0; i--) {
            final char c = value.charAt(i);
            if (c == ' ' && i != selector - 1) {
                return i + 1;
            }
        }
        return 0;
    }

    private char currentChar() {
        return value.charAt(selector);
    }
}
