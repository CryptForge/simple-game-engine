package me.cryptforge.engine.render;

import me.cryptforge.engine.Drawable;
import me.cryptforge.engine.Freeable;
import me.cryptforge.engine.asset.Asset;
import me.cryptforge.engine.asset.Assets;
import me.cryptforge.engine.asset.type.Font;
import me.cryptforge.engine.asset.type.Shader;
import me.cryptforge.engine.asset.type.Texture;
import me.cryptforge.engine.render.buffer.InstanceBuffer;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.lwjgl.opengl.GL46.*;

public class Renderer implements Freeable {

    private final InstanceBuffer instanceBuffer;

    private final SpriteBatch spriteBatch;
    private final TextBatch textBatch;
    private final ShapeBatch shapeBatch;

    private RenderBatch<?> currentBatch;

    public Renderer() {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

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

        instanceBuffer = new InstanceBuffer(this, 128000);
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

    public void clear(Color color) {
        glClearColor(color.r(), color.g(), color.b(), color.a());
        glClear(GL_COLOR_BUFFER_BIT);
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

    @Override
    public void free() {
        instanceBuffer.free();
    }
}
