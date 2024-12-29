package me.dalekcraft.structureedit.assets.textures.metadata;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import me.dalekcraft.structureedit.util.GsonHelper;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.Nullable;

public class AnimationMetadataSectionSerializer implements MetadataSectionDeserializer<AnimationMetadataSection> {

    @Override
    public AnimationMetadataSection deserialize(JsonElement json) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        int frameHeight;
        ImmutableList.Builder<AnimationFrame> builder = ImmutableList.builder();
        int defaultFrameTime = GsonHelper.getAsInt(jsonObject, "frametime", AnimationMetadataSection.DEFAULT_FRAME_TIME);
        if (defaultFrameTime != AnimationMetadataSection.DEFAULT_FRAME_TIME) {
            Validate.inclusiveBetween(1L, Integer.MAX_VALUE, defaultFrameTime, "Invalid default frame time");
        }
        if (jsonObject.has("frames")) {
            try {
                JsonArray jsonArray = GsonHelper.getAsJsonArray(jsonObject, "frames");
                for (frameHeight = 0; frameHeight < jsonArray.size(); ++frameHeight) {
                    JsonElement jsonElement = jsonArray.get(frameHeight);
                    AnimationFrame animationFrame = getFrame(frameHeight, jsonElement);
                    if (animationFrame == null) {
                        continue;
                    }
                    builder.add(animationFrame);
                }
            } catch (ClassCastException classCastException) {
                throw new JsonParseException("Invalid animation->frames: expected array, was " + jsonObject.get("frames"), classCastException);
            }
        }
        int frameWidth = GsonHelper.getAsInt(jsonObject, "width", AnimationMetadataSection.UNKNOWN_SIZE);
        frameHeight = GsonHelper.getAsInt(jsonObject, "height", AnimationMetadataSection.UNKNOWN_SIZE);
        if (frameWidth != AnimationMetadataSection.UNKNOWN_SIZE) {
            Validate.inclusiveBetween(1L, Integer.MAX_VALUE, frameWidth, "Invalid width");
        }
        if (frameHeight != AnimationMetadataSection.UNKNOWN_SIZE) {
            Validate.inclusiveBetween(1L, Integer.MAX_VALUE, frameHeight, "Invalid height");
        }
        boolean interpolatedFrames = GsonHelper.getAsBoolean(jsonObject, "interpolate", false);
        return new AnimationMetadataSection(builder.build(), frameWidth, frameHeight, defaultFrameTime, interpolatedFrames);
    }

    @Nullable
    private AnimationFrame getFrame(int n, JsonElement jsonElement) {
        if (jsonElement.isJsonPrimitive()) {
            return new AnimationFrame(GsonHelper.convertToInt(jsonElement, "frames[" + n + "]"));
        }
        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = GsonHelper.convertToJsonObject(jsonElement, "frames[" + n + "]");
            int time = GsonHelper.getAsInt(jsonObject, "time", AnimationFrame.UNKNOWN_FRAME_TIME);
            if (jsonObject.has("time")) {
                Validate.inclusiveBetween(1L, Integer.MAX_VALUE, time, "Invalid frame time");
            }
            int index = GsonHelper.getAsInt(jsonObject, "index");
            Validate.inclusiveBetween(0L, Integer.MAX_VALUE, index, "Invalid frame index");
            return new AnimationFrame(index, time);
        }
        return null;
    }

    @Override
    public String getMetadataSectionName() {
        return AnimationMetadataSection.SECTION_NAME;
    }
}
