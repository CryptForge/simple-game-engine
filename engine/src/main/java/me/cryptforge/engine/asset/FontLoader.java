package me.cryptforge.engine.asset;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.MemoryUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class FontLoader extends AssetLoader<Font, Integer> {

    private final Map<String, Font> fonts = new HashMap<>();

    @Override
    protected @Nullable Font get(String id) {
        return fonts.get(id);
    }

    @Override
    protected @NotNull Font load(String id, AssetPathType pathType, String path, Integer data) throws FileNotFoundException {
        final java.awt.Font awtFont = new java.awt.Font(java.awt.Font.MONOSPACED, java.awt.Font.PLAIN, data);
        final Map<Character, Glyph> glyphs = new HashMap<>();

        int imageWidth = 0;
        int imageHeight = 0;
        for (char c = 32; c < 256; c++) {
            if (c == 127)
                continue;
            final BufferedImage image = createCharImage(awtFont, c, true);

            imageWidth += image.getWidth();
            imageHeight = Math.max(imageHeight, image.getHeight());
        }

        BufferedImage bitmap = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D bitmapGraphics = bitmap.createGraphics();
        {
            int x = 0;

            for (char c = 32; c < 256; c++) {
                if (c == 127) {
                    continue;
                }
                final BufferedImage charImage = createCharImage(awtFont, c, true);

                final int charWidth = charImage.getWidth();
                final int charHeight = charImage.getHeight();

                final Glyph glyph = new Glyph(charWidth, charHeight, x, bitmap.getHeight() - charHeight, 0f);
                bitmapGraphics.drawImage(charImage, x, 0, null);
                x += glyph.getWidth();
                glyphs.put(c, glyph);
            }
        }

//        final AffineTransform transform = AffineTransform.getScaleInstance(1f, -1f);
//        transform.translate(0, -bitmap.getHeight());
//        final AffineTransformOp operation = new AffineTransformOp(transform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
//        bitmap = operation.filter(bitmap, null);

        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();

        int[] pixels = new int[width * height];
        bitmap.getRGB(0, 0, width, height, pixels, 0, width);

        final ByteBuffer buffer = MemoryUtil.memAlloc(width * height * 4);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = pixels[y * width + x];
                buffer.put((byte) ((pixel >> 16) & 0xFF)); //red
                buffer.put((byte) ((pixel >> 8) & 0xFF)); //green
                buffer.put((byte) (pixel & 0xFF)); //blue
                buffer.put((byte) ((pixel >> 24) & 0xFF)); //alpha
            }
        }
        buffer.flip();

        try {
            ImageIO.write(bitmap, "png", new File("bitmap.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        final Texture texture = TextureLoader.loadTexture(buffer, width, height, true);
        MemoryUtil.memFree(buffer);
        return new Font(texture, glyphs, imageHeight);
    }

    private BufferedImage createCharImage(java.awt.Font font, char c, boolean antiAlias) {
        final BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D graphics = image.createGraphics();
        if (antiAlias) {
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }
        graphics.setFont(font);
        final FontMetrics metrics = graphics.getFontMetrics();
        graphics.dispose();

        final BufferedImage charImage = new BufferedImage(metrics.charWidth(c), metrics.getHeight(), BufferedImage.TYPE_INT_ARGB);
        final Graphics2D charGraphics = charImage.createGraphics();
        if (antiAlias) {
            charGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }
        charGraphics.setFont(font);
        charGraphics.setPaint(Color.WHITE);
        charGraphics.drawString(String.valueOf(c), 0, metrics.getAscent());
        charGraphics.dispose();
        return charImage;
    }

    /*
    @Override
    protected @NotNull Font load(String id, AssetPathType pathType, String path, Integer size) throws FileNotFoundException {
        try (final InputStream inputStream = pathType.openStream(path)) {
            final byte[] bytes = inputStream.readAllBytes();
            final ByteBuffer data = MemoryUtil.memAlloc(bytes.length).put(bytes);

            final STBTTFontinfo fontInfo = STBTTFontinfo.create();
            if (!stbtt_InitFont(fontInfo, data)) {
                throw new RuntimeException("Failed to initialize font");
            }

            final int ascent;
            final int descent;
            final int lineGap;
            try(final MemoryStack stack = MemoryStack.stackPush()) {
                final IntBuffer bAscent = stack.mallocInt(1);
                final IntBuffer bDescent = stack.mallocInt(1);
                final IntBuffer bLineGap = stack.mallocInt(1);

                stbtt_GetFontVMetrics(fontInfo,bAscent,bDescent,bLineGap);

                ascent = bAscent.get(0);
                descent = bDescent.get(0);
                lineGap = bLineGap.get(0);
            }

            MemoryUtil.memFree(data);

            return initFont(fontInfo,ascent,descent,lineGap);
        } catch (FileNotFoundException e) {
            throw e;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Font initFont(STBTTFontinfo fontInfo, int ascent, int descent, int lineGap) {
        final int textureId = glGenTextures();
        final STBTTBakedChar.Buffer charData = STBTTBakedChar.malloc(96);
        final STBTTBitmap.Buffer bitmap = STBTTBitmap.create(512 * 128);

        float scale = stbtt_ScaleForPixelHeight(fontInfo,48);
        int baseline = (int) (ascent * scale);

        try(final MemoryStack stack = MemoryStack.stackPush()) {
            final IntBuffer b1 = stack.mallocInt(1);
        }

        return null;
    }
     */
}
