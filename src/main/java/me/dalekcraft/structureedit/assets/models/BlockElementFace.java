package me.dalekcraft.structureedit.assets.models;

import com.google.gson.*;
import me.dalekcraft.structureedit.util.Direction;
import me.dalekcraft.structureedit.util.GsonHelper;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

public class BlockElementFace {

    public static final int NO_TINT = -1;
    public final Direction cullForDirection;
    public final int tintIndex;
    public final String texture;
    public final BlockFaceUV uv;

    public BlockElementFace(@Nullable Direction cullForDirection, int tintIndex, String texture, BlockFaceUV uv) {
        this.cullForDirection = cullForDirection;
        this.tintIndex = tintIndex;
        this.texture = texture;
        this.uv = uv;
    }

    protected static class Deserializer implements JsonDeserializer<BlockElementFace> {
        private static final int DEFAULT_TINT_INDEX = NO_TINT;

        @Override
        public BlockElementFace deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            Direction cullForDirection = getCullFacing(jsonObject);
            int tintIndex = getTintIndex(jsonObject);
            String texture = getTexture(jsonObject);
            BlockFaceUV uv = context.deserialize(jsonObject, BlockFaceUV.class);
            return new BlockElementFace(cullForDirection, tintIndex, texture, uv);
        }

        protected int getTintIndex(JsonObject jsonObject) {
            return GsonHelper.getAsInt(jsonObject, "tintindex", DEFAULT_TINT_INDEX);
        }

        private String getTexture(JsonObject jsonObject) {
            return GsonHelper.getAsString(jsonObject, "texture");
        }

        @Nullable
        private Direction getCullFacing(JsonObject jsonObject) {
            String cullface = GsonHelper.getAsString(jsonObject, "cullface", "");
            return Direction.byName(cullface);
        }
    }
}
