package me.cryptforge.engine.render;

import me.cryptforge.engine.Application;
import me.cryptforge.engine.asset.*;
import org.joml.Matrix3x2f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.stb.STBTruetype.*;

public class Renderer {

    private final Application application;

    private final Matrix3x2f projectionMatrix;
    private final Matrix4f modelMatrix;
    private final SpriteBuilder cachedBuilder;

    private final Shader spriteShader;
    private final Shader textShader;
    private final VertexArrayObject vao;
    private final VertexBufferObject vbo;

    private final VertexBuffer vertexBuffer;

    private boolean isDrawing;

    public Renderer(Application application) {
        this.application = application;

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);



        cachedBuilder = new SpriteBuilder(this);
        modelMatrix = new Matrix4f();
        projectionMatrix = new Matrix3x2f()
                .view(0f, application.getWidth(), application.getHeight(), 0f);


        // init sprite rendering
        spriteShader = AssetManager.loadShader("sprite", AssetPathType.RESOURCE, "shaders/sprite");
        spriteShader.use();
        spriteShader.setInt("image", 0);
        spriteShader.setProjectionMatrix("projection", projectionMatrix);
        spriteShader.setMatrix4f("model", new Matrix4f());

        // init text rendering
        textShader = AssetManager.loadShader("text", AssetPathType.RESOURCE, "shaders/text");
        textShader.use();
        textShader.setInt("text", 0);
        textShader.setProjectionMatrix("projection", projectionMatrix);

        vao = new VertexArrayObject();
        vbo = new VertexBufferObject();

        vao.bind();
        vbo.bind(GL_ARRAY_BUFFER);

        vertexBuffer = new VertexBuffer(this);

        initVertexAttributes();
    }

    public void begin() {
        if (isDrawing) {
            throw new RuntimeException("start during draw");
        }
        isDrawing = true;
        vertexBuffer.clear();
    }

    public void end() {
        if (!isDrawing) {
            throw new RuntimeException("end without drawing");
        }
        isDrawing = false;
        vertexBuffer.flush(spriteShader);
    }

    /**
     * Clears the screen with specified color
     *
     * @param red   Red (0-255)
     * @param green Green (0-255)
     * @param blue  Blue (0-255)
     * @param alpha Alpha (0-1)
     */
    public void clear(int red, int green, int blue, float alpha) {
        glClearColor(
                Math.min(Math.abs(red), 255f) / 255f,
                Math.min(Math.abs(green), 255f) / 255f,
                Math.min(Math.abs(blue), 255f) / 255f,
                alpha);
        glClear(GL_COLOR_BUFFER_BIT);
    }

    /**
     * Creates a sprite builder
     *
     * @param texture Texture to draw
     * @return A new sprite builder
     */
    public SpriteBuilder sprite(Texture texture) {
        return cachedBuilder.reset().texture(texture);
    }

    public void drawSprite(Texture texture, float posX, float posY, float sizeX, float sizeY, float rotation, float r, float g, float b) {
        spriteShader.use();

        modelMatrix.identity()
                   .translate(posX, posY, 0)
                   .translate(0.5f * sizeX, 0.5f * sizeY, 0f)
                   .rotate((float) Math.toRadians(rotation), 0, 0, 1)
                   .translate(-0.5f * sizeX, -0.5f * sizeY, 0f)
                   .scale(sizeX, sizeY, 0);

        spriteShader.setMatrix4f("model", modelMatrix);

        glActiveTexture(GL_TEXTURE0);
        texture.bind();

        //        final float[] quadVertices = {
//                0.0f, 1.0f, 0.0f, 1.0f,
//                1.0f, 0.0f, 1.0f, 0.0f,
//                0.0f, 0.0f, 0.0f, 0.0f,
//
//                0.0f, 1.0f, 0.0f, 1.0f,
//                1.0f, 1.0f, 1.0f, 1.0f,
//                1.0f, 0.0f, 1.0f, 0.0f
//        };

        vao.bind();
        begin();
//        vertexBuffer.add(0,1,1,1,1,1,0,1);
//        vertexBuffer.add(1,0,1,1,1,1,1,0);
//        vertexBuffer.add(0,0,1,1,1,1,0,0);
//
//        vertexBuffer.add(0,1,1,1,1,1,0,1);
//        vertexBuffer.add(1,1,1,1,1,1,1,1);
//        vertexBuffer.add(1,0,1,1,1,1,1,0);
        drawTexture(texture, posX, posY, Color.WHITE);
        end();
    }

    private void drawTexturedRegion(float bottomX, float bottomY, float topX, float topY, float bottomTextureX, float bottomTextureY, float topTextureX, float topTextureY, float r, float g, float b, float a) {
        vertexBuffer.add(bottomX, bottomY, r, g, b, a, bottomTextureX, bottomTextureY);
        vertexBuffer.add(bottomX, topY, r, g, b, a, bottomTextureX, topTextureY);
        vertexBuffer.add(topX, topY, r, g, b, a, topTextureX, topTextureY);

        vertexBuffer.add(bottomX, bottomY, r, g, b, a, bottomTextureX, bottomTextureY);
        vertexBuffer.add(topX, topY, r, g, b, a, topTextureX, topTextureY);
        vertexBuffer.add(topX, bottomY, r, g, b, a, topTextureX, bottomTextureY);
    }

    private void drawTexture(Texture texture, float x, float y, float regX, float regY, float regWidth, float regHeight, Color color) {
        float x2 = x + regWidth;
        float y2 = y + regHeight;

        float s1 = regX / texture.getWidth();
        float t1 = regY / texture.getHeight();
        float s2 = (regX + regWidth) / texture.getWidth();
        float t2 = (regY + regHeight) / texture.getHeight();

        drawTexturedRegion(x, y, x2, y2, s1, t1, s2, t2, color.r(), color.g(), color.b(), color.a());
    }

    public void drawTexture(Texture texture, float x, float y, Color color) {
        /* Vertex positions */
        float x1 = x;
        float y1 = y;
        float x2 = x1 + texture.getWidth();
        float y2 = y1 + texture.getHeight();

        /* Texture coordinates */
        float s1 = 0f;
        float t1 = 0f;
        float s2 = 1f;
        float t2 = 1f;

        drawTexturedRegion(x1, y1, x2, y2, s1, t1, s2, t2, color.r(), color.g(), color.b(), color.a());
    }

    public void drawText(Font font, String text, float x, float y, Color color) {
        final float scale = stbtt_ScaleForPixelHeight(font.getInfo(), font.getSize());

        glActiveTexture(GL_TEXTURE0);
        textShader.use();
        font.getTexture().bind();

        try (final MemoryStack stack = MemoryStack.stackPush()) {
            final IntBuffer pCodePoint = stack.mallocInt(1);
            final FloatBuffer pX = stack.floats(0.0f);
            final FloatBuffer pY = stack.floats(0.0f);

            final STBTTAlignedQuad alignedQuad = STBTTAlignedQuad.malloc(stack);

            final float factorX = 1.0f / application.getContentScaleX();
            final float factorY = 1.0f / application.getContentScaleY();

            float lineY = 0f;

            for (int i = 0, to = text.length(); i < to; ) {
                i += Font.getCodePoint(text, to, i, pCodePoint);

                final int codePoint = pCodePoint.get(0);
                if (codePoint == '\n') {
                    pY.put(0, lineY = pY.get(0) + (font.getAscent() - font.getDescent() + font.getLineGap()) * scale);
                    pX.put(0, 0.0f);
                    continue;
                } else if (codePoint < 32 || 128 <= codePoint) {
                    continue;
                }

                float codePointX = pX.get(0);
                stbtt_GetBakedQuad(font.getCharData(), font.getBitmapWidth(), font.getBitmapHeight(), codePoint - 32, pX, pY, alignedQuad, true);
                pX.put(0, scale(codePointX, pX.get(0), factorX));
                if (Font.KERNING && i < to) {
                    Font.getCodePoint(text, to, i, pCodePoint);
                    pX.put(0, pX.get(0) + stbtt_GetCodepointKernAdvance(font.getInfo(), codePoint, pCodePoint.get(0)) * scale);
                }

                final float x0 = scale(codePointX, alignedQuad.x0(), factorX);
                final float x1 = scale(codePointX, alignedQuad.x1(), factorX);
                final float y0 = scale(lineY, alignedQuad.y0(), factorY);
                final float y1 = scale(lineY, alignedQuad.y1(), factorY);

                vertexBuffer.add(x0, y0, color, alignedQuad.s0(), alignedQuad.t0());
                vertexBuffer.add(x1, y0, color, alignedQuad.s1(), alignedQuad.t0());
                vertexBuffer.add(x1, y1, color, alignedQuad.s1(), alignedQuad.t1());
                vertexBuffer.add(x0, y1, color, alignedQuad.s0(), alignedQuad.t1());
            }
        }
    }

    private static float scale(float center, float offset, float factor) {
        return (offset - center) * factor + center;
    }


    public Vector2f convertMouseToWorld(float mouseX, float mouseY) {
        return projectionMatrix
                .unproject(
                        mouseX,
                        application.getHeight() - mouseY,
                        new int[]{0, 0, application.getWidth(), application.getHeight()},
                        new Vector2f()
                );
    }

    public void initVertexAttributes() {
        // x, y, r, g, b, a, textureX, textureY

        // position
        spriteShader.initAttribute(0, 2, 8 * Float.BYTES, 0);
        // color
        spriteShader.initAttribute(1, 4, 8 * Float.BYTES, 2 * Float.BYTES);
        // texture coordinates
        spriteShader.initAttribute(2, 2, 8 * Float.BYTES, 6 * Float.BYTES);
    }


    public VertexBufferObject vbo() {
        return vbo;
    }

    public VertexArrayObject vao() {
        return vao;
    }
}
