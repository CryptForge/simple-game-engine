package me.cryptforge.engine.asset;

public record TextureSettings(
        boolean generateMipmap,
        boolean hasAlpha,
        TextureFilter downFilter,
        TextureFilter upFilter
) {

    public static final class Builder {
        private boolean mipmap = false;
        private boolean alpha = false;
        private TextureFilter downFilter = TextureFilter.LINEAR;
        private TextureFilter upFilter = TextureFilter.LINEAR;

        private Builder() {}

        public Builder generateMipmap(boolean generateMipmap) {
            this.mipmap = generateMipmap;
            return this;
        }

        public Builder hasAlpha(boolean hasAlpha) {
            this.alpha = hasAlpha;
            return this;
        }

        public Builder downscaleFilter(TextureFilter filter) {
            this.downFilter = filter;
            return this;
        }

        public Builder upscaleFilter(TextureFilter filter) {
            this.upFilter = filter;
            return this;
        }

        public TextureSettings build() {
            return new TextureSettings(mipmap,alpha,downFilter,upFilter);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static TextureSettings defaultSettings() {
        return TextureSettings.builder().build();
    }


}
