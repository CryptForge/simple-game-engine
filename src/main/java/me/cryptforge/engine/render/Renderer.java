package me.cryptforge.engine.render;

import me.cryptforge.engine.Application;
import me.cryptforge.engine.Drawable;
import me.cryptforge.engine.Freeable;
import me.cryptforge.engine.asset.Asset;
import me.cryptforge.engine.asset.Assets;
import me.cryptforge.engine.asset.type.Font;
import me.cryptforge.engine.asset.type.Shader;
import me.cryptforge.engine.asset.type.Texture;
import me.cryptforge.engine.render.buffer.InstanceBuffer;
import org.joml.Matrix3x2f;
import org.joml.Vector2f;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.lwjgl.opengl.GL33.*;

public class Renderer implements Freeable {

    private final Application application;

    private final Matrix3x2f projectionMatrix;

    private final InstanceBuffer instanceBuffer;

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

        Assets.load(loader -> {
            loader.shader("sprite", Asset.internal("shaders/sprite.vert"), Asset.internal("shaders/sprite.frag"));
            loader.shader("text", Asset.internal("shaders/text.vert"), Asset.internal("shaders/text.frag"));
            loader.shader("shape", Asset.internal("shaders/shape.vert"), Asset.internal("shaders/shape.frag"));
        });

        // init sprite shader
        final Shader spriteShader = Assets.shader("sprite");
        spriteShader.use();
        spriteShader.setInt("image", 0);

        // init text shader
        final Shader textShader = Assets.shader("text");
        textShader.use();
        textShader.setInt("bitmap", 0);

        instanceBuffer = new InstanceBuffer(this, 25600);
        instanceBuffer.init();

        spriteBatch = new SpriteBatch(instanceBuffer);
        textBatch = new TextBatch(instanceBuffer);
        shapeBatch = new ShapeBatch(instanceBuffer);
    }

    public void drawAll(Collection<? extends Drawable> drawables) {
        final Map<Texture, List<Drawable>> grouped = drawables.stream().collect(Collectors.groupingBy(Drawable::texture));

        for (final var entry : grouped.entrySet()) {
            final Texture texture = entry.getKey();
            final List<Drawable> objects = entry.getValue();
            spriteBatch(texture, batch -> {
                for (Drawable object : objects) {
                    batch.drawSprite(object.transform(), object.color());
                }
            });
        }
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

    public Matrix3x2f getProjectionMatrix() {
        return projectionMatrix;
    }

    @Override
    public void free() {
        instanceBuffer.free();
    }
}
