package me.cryptforge.engine.ui.component;

import me.cryptforge.engine.asset.type.Font;
import me.cryptforge.engine.asset.type.Texture;
import me.cryptforge.engine.input.InputListener;
import me.cryptforge.engine.render.Color;
import me.cryptforge.engine.render.Renderer;

import java.util.function.Consumer;

public interface Component extends InputListener {

    void render(Renderer renderer, float x, float y);

    float width();

    float height();

    default void debug(Renderer renderer, float x, float y) {
        renderer.shapeBatch(batch -> {
            batch.drawSquare(x, y, 4, Color.RED);
        });
    }

    static ButtonComponent button(Texture texture, Font font, String text, float size, Runnable callback) {
        return new ButtonComponent(texture, font, text, size, callback);
    }

    static LabelComponent label(Font font, String label) {
        return new LabelComponent(label, font);
    }

    static InputFieldComponent inputField(Font font, String defaultValue, Color textColor, Color selectorColor, Consumer<String> onSubmit) {
        return new InputFieldComponent(font, defaultValue, textColor, selectorColor, onSubmit);
    }

    static InputFieldComponent inputField(Font font, Color textColor, Color selectorColor, Consumer<String> onSubmit) {
        return new InputFieldComponent(font, "", textColor, selectorColor, onSubmit);
    }

    static InputFieldComponent inputField(Font font, Consumer<String> onSubmit) {
        return new InputFieldComponent(font, "", Color.WHITE, Color.BLACK, onSubmit);
    }

}
