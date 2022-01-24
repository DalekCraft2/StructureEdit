package me.dalekcraft.structureedit.assets.textures;

import me.dalekcraft.structureedit.assets.ResourceLocation;

public final class MissingTexture {

    private static final String MISSING_TEXTURE_NAME = "missingno";
    private static final ResourceLocation MISSING_TEXTURE_LOCATION = new ResourceLocation(MISSING_TEXTURE_NAME);

    public static ResourceLocation getLocation() {
        return MISSING_TEXTURE_LOCATION;
    }
}

