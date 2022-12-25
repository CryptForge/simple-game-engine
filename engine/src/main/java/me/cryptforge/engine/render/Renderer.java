package me.cryptforge.engine.render;

import me.cryptforge.engine.Application;
import me.cryptforge.engine.asset.*;
import org.joml.Matrix3x2f;
import org.joml.Matrix4f;
import org.joml.Vector2f;

import java.util.function.Consumer;

import static org.lwjgl.opengl.GL33.*;

public class Renderer {

    private final Application application;

    private final Matrix3x2f projectionMatrix;

    private final VertexArrayObject vao;
    private final VertexBufferObject vbo;

    private final VertexBuffer vertexBuffer;

    private final SpriteBatch spriteBatch;
    private final TextBatch textBatch;

    private RenderBatch currentBatch;

    public Renderer(Application application) {
        this.application = application;

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        projectionMatrix = new Matrix3x2f()
                .view(0f, application.getWidth(), application.getHeight(), 0f);


        // init sprite rendering
        final Shader spriteShader = AssetManager.loadShader("sprite", AssetPathType.RESOURCE, "shaders/sprite");
        spriteShader.use();
        spriteShader.setInt("image", 0);
        spriteShader.setProjectionMatrix("projection", projectionMatrix);
        spriteShader.setMatrix4f("model", new Matrix4f());

        // init text rendering
        final Shader textShader = AssetManager.loadShader("text", AssetPathType.RESOURCE, "shaders/text");
        textShader.use();
        textShader.setInt("text", 0);
        textShader.setProjectionMatrix("projection", projectionMatrix);

        vao = new VertexArrayObject();
        vbo = new VertexBufferObject();

        vao.bind();
        vbo.bind(GL_ARRAY_BUFFER);

        vertexBuffer = new VertexBuffer(this);

        initVertexAttributes();

        spriteBatch = new SpriteBatch(vertexBuffer);
        textBatch = new TextBatch(vertexBuffer);
    }

    private void begin() {
        if (currentBatch != null) {
            throw new RuntimeException("Cannot start multiple batches at once");
        }
        vertexBuffer.clear();
    }

    public void spriteBatch(Texture texture, Consumer<SpriteBatch> actions) {
        begin();

        spriteBatch.setTexture(texture);
        spriteBatch.init();
        currentBatch = spriteBatch;
        actions.accept(spriteBatch);
        end();
    }

    public void textBatch(Font font, Consumer<TextBatch> actions) {
        begin();

        textBatch.setFont(font);
        textBatch.init();
        currentBatch = textBatch;
        actions.accept(textBatch);
        end();
    }

    private void end() {
        if (currentBatch == null) {
            throw new RuntimeException("Cannot end batch without starting");
        }

        flushBuffer();

        currentBatch.clear();
        currentBatch = null;
    }

    protected void flushBuffer() {
        currentBatch.getShader().use();

        final Texture texture = currentBatch.getTexture();
        if (texture != null) {
            glActiveTexture(GL_TEXTURE0);
            texture.bind();
        }
        vertexBuffer.flush();
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
        initAttribute(0, 2, 8 * Float.BYTES, 0);
        // color
        initAttribute(1, 4, 8 * Float.BYTES, 2 * Float.BYTES);
        // texture coordinates
        initAttribute(2, 2, 8 * Float.BYTES, 6 * Float.BYTES);
    }

    public void initAttribute(int index, int size, int stride, int offset) {
        glVertexAttribPointer(index,size,GL_FLOAT,false,stride,offset);
        glEnableVertexAttribArray(index);
    }

    protected VertexBufferObject vbo() {
        return vbo;
    }

    protected VertexArrayObject vao() {
        return vao;
    }
}
