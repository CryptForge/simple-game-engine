package me.cryptforge.engine.ui.component;

import me.cryptforge.engine.Engine;
import me.cryptforge.engine.asset.type.Font;
import me.cryptforge.engine.asset.type.Texture;
import me.cryptforge.engine.input.InputButton;
import me.cryptforge.engine.input.InputModifiers;
import me.cryptforge.engine.input.InputState;
import me.cryptforge.engine.render.Color;
import me.cryptforge.engine.render.Renderer;
import org.joml.Vector2f;

public class ButtonComponent implements Component {

    private final Texture texture;
    private final Font font;
    private final String text;
    private final float width, height;
    private final Runnable callback;
    private boolean hovering;

    ButtonComponent(Texture texture, Font font, String text, float size, Runnable callback) {
        this.texture = texture;
        this.font = font;
        this.text = text;
        this.width = size;
        this.height = size / 4f;
        this.callback = callback;
    }

    @Override
    public void render(Renderer renderer, float x, float y) {
        final Vector2f mousePosition = Engine.input().mousePosition();
        hovering = isWithinBounds(x, y, mousePosition.x, mousePosition.y);

        renderer.spriteBatch(texture, batch -> {
            batch.drawSpriteRegion(
                    x,
                    y,
                    width,
                    height,
                    0, hovering ? 64 : 0,
                    256, hovering ? 128 : 64,
                    0,
                    Color.WHITE
            );
        });

        renderer.textBatch(font, batch -> {
            batch.drawTextCentered(
                    text,
                    x + width / 2, y + height / 2,
                    Color.BLACK
            );
        });
    }

    @Override
    public void handleInput(InputButton inputButton, InputState inputState, InputModifiers modifiers) {
        if (inputButton == InputButton.MOUSE_LEFT && inputState == InputState.PRESSED) {
            if (hovering) {
                callback.run();
            }
        }
    }

    @Override
    public float width() {
        return width;
    }

    @Override
    public float height() {
        return height;
    }

    private boolean isWithinBounds(float thisX, float thisY, float x, float y) {
        final float minX = thisX;
        final float minY = thisY;
        final float maxX = thisX + width;
        final float maxY = thisY + height;
        return x >= minX && x <= maxX  && y >= minY && y <= maxY;
    }
}
