package me.dalekcraft.structureedit.assets.textures.metadata;

public class TextureMetadataSection {

    public static final TextureMetadataSectionSerializer SERIALIZER = new TextureMetadataSectionSerializer();
    public static final String SECTION_NAME = "texture";
    public static final boolean DEFAULT_BLUR = false;
    public static final boolean DEFAULT_CLAMP = false;
    private final boolean blur;
    private final boolean clamp;

    public TextureMetadataSection(boolean blur, boolean clamp) {
        this.blur = blur;
        this.clamp = clamp;
    }

    public boolean isBlur() {
        return blur;
    }

    public boolean isClamp() {
        return clamp;
    }
}

