package me.cryptforge.engine.render;

import me.cryptforge.engine.Application;
import me.cryptforge.engine.asset.*;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.stb.STBTruetype.stbtt_GetBakedQuad;
import static org.lwjgl.stb.STBTruetype.stbtt_ScaleForPixelHeight;

public class Renderer {

    private final Application application;

    private final Matrix3x2f projectionMatrix;
    private final SpriteBuilder cachedBuilder;

    private final Shader spriteShader;
    private final Shader textShader;
    private final VertexArrayObject vao;
    private final VertexBufferObject vbo;

    private final VertexBuffer vertexBuffer;

    private RenderType renderType;
    private Texture activeTexture;

    private boolean isDrawing;

    public Renderer(Application application) {
        this.application = application;

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);


        cachedBuilder = new SpriteBuilder(this);
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

    public void begin(RenderType renderType) {
        if (isDrawing) {
            throw new RuntimeException("start during draw");
        }
        this.renderType = renderType;
        isDrawing = true;
        vertexBuffer.clear();
    }

    public void end() {
        if (!isDrawing) {
            throw new RuntimeException("end without drawing");
        }
        isDrawing = false;

        vertexBuffer.flush();

        renderType = null;
        activeTexture = null;
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

    public void drawSprite(Texture texture, float posX, float posY, float sizeX, float sizeY, float r, float g, float b, float a) {
        setActiveTexture(texture);

        vertexBuffer.region(posX, posY, posX + sizeX, posY + sizeY, 0, 0, 1, 1, r, g, b, a);
    }

    public void drawText(Font font, String text, float x, float y, Color color) {
        final float scale = stbtt_ScaleForPixelHeight(font.getInfo(), font.getSize());

        setActiveTexture(font.getTexture());

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

                vertexBuffer.region(x0, y0, x1, y1, alignedQuad.s0(), alignedQuad.t0(), alignedQuad.s1(), alignedQuad.t1(), color);
            }
        }
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

    private void initVertexAttributes() {
        // x, y, r, g, b, a, textureX, textureY

        // position
        spriteShader.initAttribute(0, 2, 8 * Float.BYTES, 0);
        // color
        spriteShader.initAttribute(1, 4, 8 * Float.BYTES, 2 * Float.BYTES);
        // texture coordinates
        spriteShader.initAttribute(2, 2, 8 * Float.BYTES, 6 * Float.BYTES);
    }

    private void setActiveTexture(@NotNull Texture texture) {
        if (activeTexture != null && !activeTexture.equals(texture)) {
            throw new RuntimeException("Attempted to change texture twice during draw session");
        }
        activeTexture = texture;
    }

    public Shader getActiveShader() {
        return renderType == RenderType.SPRITE ? spriteShader : textShader;
    }

    public Texture getActiveTexture() {
        return activeTexture;
    }


    public VertexBufferObject vbo() {
        return vbo;
    }

    public VertexArrayObject vao() {
        return vao;
    }
}
