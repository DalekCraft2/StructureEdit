package me.dalekcraft.structureedit.assets.textures.metadata;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public interface MetadataSectionDeserializer<T> {

    T deserialize(JsonElement json) throws JsonParseException;

    String getMetadataSectionName();
}

