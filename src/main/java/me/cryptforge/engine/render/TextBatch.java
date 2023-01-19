package me.cryptforge.engine.render;

import me.cryptforge.engine.asset.Assets;
import me.cryptforge.engine.asset.Glyph;
import me.cryptforge.engine.asset.type.Font;
import me.cryptforge.engine.render.buffer.InstanceBuffer;
import org.joml.Matrix3x2f;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

import static org.lwjgl.stb.STBTruetype.stbtt_GetBakedQuad;

public final class TextBatch extends RenderBatch<InstanceBuffer> {

    private final Matrix3x2f matrix;
    private Font font;

    public TextBatch(InstanceBuffer buffer) {
        super(buffer, Assets.shader("text"));
        this.matrix = new Matrix3x2f();
    }

    @Override
    void init() {
        if (font == null) {
            throw new IllegalStateException("Font is null in text batch");
        }
        buffer().clear();
    }

    @Override
    void cleanup() {
        font = null;
    }

    public void drawText(String text, float x, float y, Color color) {
        final float scale = font.getScale();

        try (final MemoryStack stack = MemoryStack.stackPush()) {
            final FloatBuffer pX = stack.floats(0.0f);
            final FloatBuffer pY = stack.floats(0.0f);

            final STBTTAlignedQuad alignedQuad = STBTTAlignedQuad.malloc(stack);

            pX.put(0, x);
            pY.put(0, y + (font.getAscent() * scale));

            for (int i = 0; i < text.length(); i++) {
                final char c = text.charAt(i);
                final int codePoint = Character.codePointAt(text, i);
                final Glyph glyph = font.getGlyph(codePoint);

                if (c == ' ') {
                    pX.put(0, pX.get(0) + glyph.advance() * scale);
                    continue;
                }
                if (c == '\n') {
                    pX.put(0, x);
                    pY.put(0, pY.get(0) + (font.getAscent() - font.getDescent() + font.getLineGap()) * scale);
                    continue;
                }

                stbtt_GetBakedQuad(
                        font.getCharData(),
                        font.getBitmapWidth(),
                        font.getBitmapHeight(),
                        codePoint - 32,
                        pX,
                        pY,
                        alignedQuad,
                        true
                );

                final float x0 = alignedQuad.x0();
                final float x1 = alignedQuad.x1();
                final float y0 = alignedQuad.y0();
                final float y1 = alignedQuad.y1();
                final float s0 = alignedQuad.s0();
                final float s1 = alignedQuad.s1();
                final float t0 = alignedQuad.t0();
                final float t1 = alignedQuad.t1();

                matrix.translation(x0, y0)
                      .scale(x1 - x0, y1 - y0);

                drawChar(
                        s0,
                        s1,
                        t0,
                        t1,
                        color.r(),
                        color.g(),
                        color.b(),
                        color.a()
                );
            }
        }
    }

    private void drawChar(
            float s0,
            float s1,
            float t0,
            float t1,
            float r,
            float g,
            float b,
            float a
    ) {
        buffer().putInstance(
                r,
                g,
                b,
                a,
                s1 - s0,
                t1 - t0,
                s0,
                t0,
                matrix
        );
    }

    public void drawTextCentered(String text, float x, float y, Color color) {
        final float textWidth = font.getTextWidth(text);
        drawText(text, x - (textWidth / 2), y - (font.getSize() / 2f), color);
    }

    void setFont(Font font) {
        if (this.font != null && !this.font.equals(font)) {
            throw new RuntimeException("Cannot set font twice during text batch");
        }
        this.font = font;
        setTexture(font.getTexture());
    }
}
