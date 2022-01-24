/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 */
package me.dalekcraft.structureedit.assets.blockstates;

import com.google.common.annotations.VisibleForTesting;
import com.google.gson.*;
import me.dalekcraft.structureedit.assets.ResourceLocation;
import me.dalekcraft.structureedit.util.GsonHelper;

import java.lang.reflect.Type;
import java.util.Objects;

public class Variant {
    private final ResourceLocation modelLocation;
    private final BlockModelRotation rotation;
    private final boolean uvLock;
    private final int weight;

    public Variant(ResourceLocation modelLocation, BlockModelRotation rotation, boolean uvLock, int weight) {
        this.modelLocation = modelLocation;
        this.rotation = rotation;
        this.uvLock = uvLock;
        this.weight = weight;
    }

    public ResourceLocation getModelLocation() {
        return modelLocation;
    }

    public BlockModelRotation getRotation() {
        return rotation;
    }

    public boolean isUvLocked() {
        return uvLock;
    }

    public int getWeight() {
        return weight;
    }

    public String toString() {
        return "Variant{modelLocation=" + modelLocation + ", rotation=" + rotation + ", uvLock=" + uvLock + ", weight=" + weight + "}";
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Variant variant) {
            return modelLocation.equals(variant.modelLocation) && Objects.equals(rotation, variant.rotation) && uvLock == variant.uvLock && weight == variant.weight;
        }
        return false;
    }

    public int hashCode() {
        int n = modelLocation.hashCode();
        n = 31 * n + rotation.hashCode();
        n = 31 * n + Boolean.valueOf(uvLock).hashCode();
        n = 31 * n + weight;
        return n;
    }

    public static class Deserializer implements JsonDeserializer<Variant> {
        @VisibleForTesting
        static final boolean DEFAULT_UVLOCK = false;
        @VisibleForTesting
        static final int DEFAULT_WEIGHT = 1;
        @VisibleForTesting
        static final int DEFAULT_X_ROTATION = 0;
        @VisibleForTesting
        static final int DEFAULT_Y_ROTATION = 0;

        @Override
        public Variant deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            ResourceLocation modelLocation = getModel(jsonObject);
            BlockModelRotation blockModelRotation = getBlockRotation(jsonObject);
            boolean uvLock = getUvLock(jsonObject);
            int weight = getWeight(jsonObject);
            return new Variant(modelLocation, blockModelRotation, uvLock, weight);
        }

        private boolean getUvLock(JsonObject jsonObject) {
            return GsonHelper.getAsBoolean(jsonObject, "uvlock", DEFAULT_UVLOCK);
        }

        protected BlockModelRotation getBlockRotation(JsonObject jsonObject) {
            int x = GsonHelper.getAsInt(jsonObject, "x", DEFAULT_X_ROTATION);
            int y = GsonHelper.getAsInt(jsonObject, "y", DEFAULT_Y_ROTATION);
            BlockModelRotation blockModelRotation = BlockModelRotation.by(x, y);
            if (blockModelRotation == null) {
                throw new JsonParseException("Invalid BlockModelRotation x: " + x + ", y: " + y);
            }
            return blockModelRotation;
        }

        protected ResourceLocation getModel(JsonObject jsonObject) {
            return new ResourceLocation(GsonHelper.getAsString(jsonObject, "model"));
        }

        protected int getWeight(JsonObject jsonObject) {
            int weight = GsonHelper.getAsInt(jsonObject, "weight", DEFAULT_WEIGHT);
            if (weight < 1) {
                throw new JsonParseException("Invalid weight " + weight + " found, expected integer >= 1");
            }
            return weight;
        }
    }
}

