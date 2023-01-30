package me.cryptforge.engine.ui.component;

import me.cryptforge.engine.asset.type.Font;
import me.cryptforge.engine.render.Color;
import me.cryptforge.engine.render.Renderer;

public class LabelComponent implements Component {

    private final String text;
    private final Font font;
    private final float width, height;

    LabelComponent(String text, Font font) {
        this.text = text;
        this.font = font;
        this.width = font.getTextWidth(text);
        this.height = (font.getAscent() - font.getDescent()) * font.getScale();
    }

    @Override
    public void render(Renderer renderer, float x, float y) {
        renderer.textBatch(font, batch -> {
            batch.drawText(text, x, y, Color.BLACK);
        });
    }

    @Override
    public float width() {
        return width;
    }

    @Override
    public float height() {
        return height;
    }
}
