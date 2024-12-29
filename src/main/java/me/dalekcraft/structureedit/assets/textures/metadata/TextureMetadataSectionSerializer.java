package me.dalekcraft.structureedit.assets.textures.metadata;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import me.dalekcraft.structureedit.util.GsonHelper;

public class TextureMetadataSectionSerializer implements MetadataSectionDeserializer<TextureMetadataSection> {

    @Override
    public TextureMetadataSection deserialize(JsonElement json) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        boolean blur = GsonHelper.getAsBoolean(jsonObject, "blur", TextureMetadataSection.DEFAULT_BLUR);
        boolean clamp = GsonHelper.getAsBoolean(jsonObject, "clamp", TextureMetadataSection.DEFAULT_CLAMP);
        return new TextureMetadataSection(blur, clamp);
    }

    @Override
    public String getMetadataSectionName() {
        return TextureMetadataSection.SECTION_NAME;
    }
}

