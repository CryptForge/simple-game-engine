package me.cryptforge.engine.render;

import me.cryptforge.engine.asset.AssetManager;
import me.cryptforge.engine.asset.Font;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

import static org.lwjgl.stb.STBTruetype.stbtt_GetBakedQuad;
import static org.lwjgl.stb.STBTruetype.stbtt_ScaleForPixelHeight;

public final class TextBatch extends RenderBatch {

    private Font font;

    public TextBatch(VertexBuffer vertexBuffer) {
        super(vertexBuffer, AssetManager.getShader("text"));
    }

    @Override
    void init() {
        if(font == null) {
            throw new IllegalStateException("Font is null in text batch");
        }
    }

    @Override
    void cleanup() {
        font = null;
    }

    public void drawText(String text, float x, float y, Color color) {
        final float scale = stbtt_ScaleForPixelHeight(font.getInfo(), font.getSize());

        try (final MemoryStack stack = MemoryStack.stackPush()) {
            final FloatBuffer pX = stack.floats(0.0f);
            final FloatBuffer pY = stack.floats(0.0f);

            final STBTTAlignedQuad alignedQuad = STBTTAlignedQuad.malloc(stack);

            pX.put(0, x);
            pY.put(0, y);

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

                vertexBuffer().region(x0, y0, x1, y1, alignedQuad.s0(), alignedQuad.t0(), alignedQuad.s1(), alignedQuad.t1(), color);
            }
        }
    }

    void setFont(Font font) {
        if(this.font != null && !this.font.equals(font)) {
            throw new RuntimeException("Cannot set font twice during text batch");
        }
        this.font = font;
        setTexture(font.getTexture());
    }
}
