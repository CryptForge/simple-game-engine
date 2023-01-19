package me.cryptforge.engine.asset;

public record TextureSettings(
        boolean generateMipmap,
        boolean hasAlpha,
        TextureFilter filter
) {

    public static final class Builder {
        private boolean mipmap = false;
        private boolean alpha = false;
        private TextureFilter filter = TextureFilter.LINEAR;

        private Builder() {}

        public Builder mipmap(boolean generateMipmap) {
            this.mipmap = generateMipmap;
            return this;
        }

        public Builder alpha(boolean hasAlpha) {
            this.alpha = hasAlpha;
            return this;
        }

        public Builder filter(TextureFilter filter) {
            this.filter = filter;
            return this;
        }


        public TextureSettings build() {
            return new TextureSettings(mipmap,alpha,filter);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static TextureSettings defaultSettings() {
        return TextureSettings.builder().build();
    }

    public static TextureSettings pixelSettings() {
        return TextureSettings.builder()
                .alpha(true)
                .filter(TextureFilter.NEAREST)
                .build();
    }


}
