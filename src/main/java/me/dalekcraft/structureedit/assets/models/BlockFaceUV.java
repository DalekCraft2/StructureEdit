package me.dalekcraft.structureedit.assets.models;

import com.google.gson.*;
import me.dalekcraft.structureedit.util.GsonHelper;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Objects;

public class BlockFaceUV {

    public final int rotation;
    public float[] uvs;

    public BlockFaceUV(float @Nullable [] uvs, int rotation) {
        this.uvs = uvs;
        this.rotation = rotation;
    }

    public float getU(int n) {
        Objects.requireNonNull(uvs);
        int shiftedIndex = getShiftedIndex(n);
        return uvs[shiftedIndex == 0 || shiftedIndex == 1 ? 0 : 2];
    }

    public float getV(int n) {
        Objects.requireNonNull(uvs);
        int shiftedIndex = getShiftedIndex(n);
        return uvs[shiftedIndex == 0 || shiftedIndex == 3 ? 1 : 3];
    }

    private int getShiftedIndex(int n) {
        return (n + rotation / 90) % 4;
    }

    public int getReverseIndex(int n) {
        return (n + 4 - rotation / 90) % 4;
    }

    public void setMissingUv(float[] uvs) {
        if (this.uvs == null) {
            this.uvs = uvs;
        }
    }

    protected static class Deserializer implements JsonDeserializer<BlockFaceUV> {
        private static final int DEFAULT_ROTATION = 0;

        @Override
        public BlockFaceUV deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            float[] fArray = getUVs(jsonObject);
            int n = getRotation(jsonObject);
            return new BlockFaceUV(fArray, n);
        }

        protected int getRotation(JsonObject jsonObject) {
            int n = GsonHelper.getAsInt(jsonObject, "rotation", DEFAULT_ROTATION);
            if (n < 0 || n % 90 != 0 || n / 90 > 3) {
                throw new JsonParseException("Invalid rotation " + n + " found, only 0/90/180/270 allowed");
            }
            return n;
        }

        private float @Nullable [] getUVs(JsonObject jsonObject) {
            if (!jsonObject.has("uv")) {
                return null;
            }
            JsonArray jsonArray = GsonHelper.getAsJsonArray(jsonObject, "uv");
            if (jsonArray.size() != 4) {
                throw new JsonParseException("Expected 4 uv values, found: " + jsonArray.size());
            }
            float[] fArray = new float[4];
            for (int i = 0; i < fArray.length; ++i) {
                fArray[i] = GsonHelper.convertToFloat(jsonArray.get(i), "uv[" + i + "]");
            }
            return fArray;
        }
    }
}
