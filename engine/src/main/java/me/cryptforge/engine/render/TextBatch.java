package me.cryptforge.engine.render;

import me.cryptforge.engine.asset.Assets;
import me.cryptforge.engine.asset.type.Font;
import me.cryptforge.engine.render.buffer.VertexBuffer;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

import static org.lwjgl.stb.STBTruetype.stbtt_GetBakedQuad;

public final class TextBatch extends RenderBatch<VertexBuffer> {

    private Font font;

    public TextBatch(VertexBuffer buffer) {
        super(buffer, Assets.shader("text"));
    }

    @Override
    void init() {
        if(font == null) {
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

                buffer().putRegion(x0, y0, x1, y1, s0, t0, s1, t1, color);
            }
        }
    }

    public void drawTextCentered(String text, float x, float y, Color color) {
        final float textWidth = font.getTextWidth(text);
        drawText(text,x - (textWidth / 2),y - (font.getSize() / 2f),color);
    }

    void setFont(Font font) {
        if(this.font != null && !this.font.equals(font)) {
            throw new RuntimeException("Cannot set font twice during text batch");
        }
        this.font = font;
        setTexture(font.getTexture());
    }
}
