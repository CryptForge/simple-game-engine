package me.cryptforge.engine.render;

import me.cryptforge.engine.Application;
import me.cryptforge.engine.asset.*;
import me.cryptforge.engine.render.buffer.InstanceBuffer;
import me.cryptforge.engine.render.buffer.VertexBuffer;
import org.joml.Matrix3x2f;
import org.joml.Vector2f;

import java.util.function.Consumer;

import static org.lwjgl.opengl.GL33.*;

public class Renderer {

    private final Application application;

    private final Matrix3x2f projectionMatrix;

    private final InstanceBuffer instanceBuffer;
    private final VertexBuffer vertexBuffer;

    private final SpriteBatch spriteBatch;
    private final TextBatch textBatch;
    private final ShapeBatch shapeBatch;

    private RenderBatch<?> currentBatch;

    public Renderer(Application application) {
        this.application = application;

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        projectionMatrix = new Matrix3x2f()
                .view(0f, application.getWorldWidth(), application.getWorldHeight(), 0f);


        // init sprite shader
        final Shader spriteShader = AssetManager.loadShader("sprite", AssetPathType.RESOURCE, "shaders/sprite");
        spriteShader.use();
        spriteShader.setInt("image", 0);
        spriteShader.setProjectionMatrix("projection", projectionMatrix);

        // init text shader
        final Shader textShader = AssetManager.loadShader("text", AssetPathType.RESOURCE, "shaders/text");
        textShader.use();
        textShader.setInt("text", 0);
        textShader.setProjectionMatrix("projection", projectionMatrix);

        // init shape shader
        final Shader shapeShader = AssetManager.loadShader("shape", AssetPathType.RESOURCE, "shaders/shape");
        shapeShader.use();
        shapeShader.setProjectionMatrix("projection", projectionMatrix);

        instanceBuffer = new InstanceBuffer(this, 12288);
        instanceBuffer.init();
        vertexBuffer = new VertexBuffer(this,4096);
        vertexBuffer.init();

        spriteBatch = new SpriteBatch(instanceBuffer);
        textBatch = new TextBatch(vertexBuffer);
        shapeBatch = new ShapeBatch(vertexBuffer);
    }

    public void spriteBatch(Texture texture, Consumer<SpriteBatch> actions) {
        if (currentBatch != null) {
            throw new RuntimeException("Cannot start multiple batches at once");
        }

        spriteBatch.setTexture(texture);
        spriteBatch.init();
        currentBatch = spriteBatch;
        actions.accept(spriteBatch);
        finishCurrentBatch();
    }

    public void textBatch(Font font, Consumer<TextBatch> actions) {
        if (currentBatch != null) {
            throw new RuntimeException("Cannot start multiple batches at once");
        }

        textBatch.setFont(font);
        textBatch.init();
        currentBatch = textBatch;
        actions.accept(textBatch);
        finishCurrentBatch();
    }

    public void shapeBatch(Consumer<ShapeBatch> actions) {
        if (currentBatch != null) {
            throw new RuntimeException("Cannot start multiple batches at once");
        }

        shapeBatch.init();
        currentBatch = shapeBatch;
        actions.accept(shapeBatch);
        finishCurrentBatch();
    }

    private void finishCurrentBatch() {
        if (currentBatch == null) {
            throw new RuntimeException("Cannot end batch without starting");
        }

        flushBuffer();

        currentBatch.clear();
        currentBatch = null;
    }

    public void flushBuffer() {
        currentBatch.getShader().use();

        final Texture texture = currentBatch.getTexture();
        if (texture != null) {
            glActiveTexture(GL_TEXTURE0);
            texture.bind();
        }
        currentBatch.buffer().flush();
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
}
