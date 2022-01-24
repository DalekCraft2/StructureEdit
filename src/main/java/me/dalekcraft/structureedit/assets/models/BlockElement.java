package me.dalekcraft.structureedit.assets.models;

import com.google.common.collect.Maps;
import com.google.gson.*;
import me.dalekcraft.structureedit.util.Direction;
import me.dalekcraft.structureedit.util.GsonHelper;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.lang.reflect.Type;
import java.util.Locale;
import java.util.Map;

public class BlockElement {

    private static final boolean DEFAULT_RESCALE = false;
    private static final float MIN_EXTENT = -16.0f;
    private static final float MAX_EXTENT = 32.0f;
    public final Vector3f from;
    public final Vector3f to;
    public final Map<Direction, BlockElementFace> faces;
    public final BlockElementRotation rotation;
    public final boolean shade;

    public BlockElement(Vector3f from, Vector3f to, Map<Direction, BlockElementFace> faces, @Nullable BlockElementRotation rotation, boolean shade) {
        this.from = from;
        this.to = to;
        this.faces = faces;
        this.rotation = rotation;
        this.shade = shade;
        fillUvs();
    }

    private void fillUvs() {
        for (Map.Entry<Direction, BlockElementFace> entry : faces.entrySet()) {
            float[] fArray = uvsByFace(entry.getKey());
            entry.getValue().uv.setMissingUv(fArray);
        }
    }

    private float[] uvsByFace(Direction direction) {
        return switch (direction) {
            case DOWN -> new float[]{from.x(), 16.0f - to.z(), to.x(), 16.0f - from.z()};
            case UP -> new float[]{from.x(), from.z(), to.x(), to.z()};
            default -> new float[]{16.0f - to.x(), 16.0f - to.y(), 16.0f - from.x(), 16.0f - from.y()};
            case SOUTH -> new float[]{from.x(), 16.0f - to.y(), to.x(), 16.0f - from.y()};
            case WEST -> new float[]{from.z(), 16.0f - to.y(), to.z(), 16.0f - from.y()};
            case EAST -> new float[]{16.0f - to.z(), 16.0f - to.y(), 16.0f - from.z(), 16.0f - from.y()};
        };
    }

    protected static class Deserializer implements JsonDeserializer<BlockElement> {

        private static final boolean DEFAULT_SHADE = true;

        @Override
        public BlockElement deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            Vector3f vector3f = getFrom(jsonObject);
            Vector3f vector3f2 = getTo(jsonObject);
            BlockElementRotation blockElementRotation = getRotation(jsonObject);
            Map<Direction, BlockElementFace> map = getFaces(context, jsonObject);
            if (jsonObject.has("shade") && !GsonHelper.isBooleanValue(jsonObject, "shade")) {
                throw new JsonParseException("Expected shade to be a Boolean");
            }
            boolean bl = GsonHelper.getAsBoolean(jsonObject, "shade", DEFAULT_SHADE);
            return new BlockElement(vector3f, vector3f2, map, blockElementRotation, bl);
        }

        @Nullable
        private BlockElementRotation getRotation(JsonObject jsonObject) {
            BlockElementRotation blockElementRotation = null;
            if (jsonObject.has("rotation")) {
                JsonObject jsonObject2 = GsonHelper.getAsJsonObject(jsonObject, "rotation");
                Vector3f vector3f = getVector3f(jsonObject2, "origin");
                vector3f.mul(0.0625f);
                Direction.Axis axis = getAxis(jsonObject2);
                float f = getAngle(jsonObject2);
                boolean bl = GsonHelper.getAsBoolean(jsonObject2, "rescale", DEFAULT_RESCALE);
                blockElementRotation = new BlockElementRotation(vector3f, axis, f, bl);
            }
            return blockElementRotation;
        }

        private float getAngle(JsonObject jsonObject) {
            float f = GsonHelper.getAsFloat(jsonObject, "angle");
            if (f != 0.0f && Math.abs(f) != 22.5f && Math.abs(f) != 45.0f) {
                throw new JsonParseException("Invalid rotation " + f + " found, only -45/-22.5/0/22.5/45 allowed");
            }
            return f;
        }

        private Direction.Axis getAxis(JsonObject jsonObject) {
            String axisName = GsonHelper.getAsString(jsonObject, "axis");
            Direction.Axis axis = Direction.Axis.byName(axisName.toLowerCase(Locale.ROOT));
            if (axis == null) {
                throw new JsonParseException("Invalid rotation axis: " + axisName);
            }
            return axis;
        }

        private Map<Direction, BlockElementFace> getFaces(JsonDeserializationContext context, JsonObject jsonObject) {
            Map<Direction, BlockElementFace> map = filterNullFromFaces(context, jsonObject);
            if (map.isEmpty()) {
                throw new JsonParseException("Expected between 1 and 6 unique faces, got 0");
            }
            return map;
        }

        private Map<Direction, BlockElementFace> filterNullFromFaces(JsonDeserializationContext context, JsonObject jsonObject) {
            Map<Direction, BlockElementFace> enumMap = Maps.newEnumMap(Direction.class);
            JsonObject jsonObject2 = GsonHelper.getAsJsonObject(jsonObject, "faces");
            for (Map.Entry<String, JsonElement> entry : jsonObject2.entrySet()) {
                Direction direction = getFacing(entry.getKey());
                enumMap.put(direction, context.deserialize(entry.getValue(), BlockElementFace.class));
            }
            return enumMap;
        }

        private Direction getFacing(String axisName) {
            Direction direction = Direction.byName(axisName);
            if (direction == null) {
                throw new JsonParseException("Unknown facing: " + axisName);
            }
            return direction;
        }

        private Vector3f getTo(JsonObject jsonObject) {
            Vector3f vector3f = getVector3f(jsonObject, "to");
            if (vector3f.x() < MIN_EXTENT || vector3f.y() < MIN_EXTENT || vector3f.z() < MIN_EXTENT || vector3f.x() > MAX_EXTENT || vector3f.y() > MAX_EXTENT || vector3f.z() > MAX_EXTENT) {
                throw new JsonParseException("'to' specifier exceeds the allowed boundaries: " + vector3f);
            }
            return vector3f;
        }

        private Vector3f getFrom(JsonObject jsonObject) {
            Vector3f vector3f = getVector3f(jsonObject, "from");
            if (vector3f.x() < MIN_EXTENT || vector3f.y() < MIN_EXTENT || vector3f.z() < MIN_EXTENT || vector3f.x() > MAX_EXTENT || vector3f.y() > MAX_EXTENT || vector3f.z() > MAX_EXTENT) {
                throw new JsonParseException("'from' specifier exceeds the allowed boundaries: " + vector3f);
            }
            return vector3f;
        }

        private Vector3f getVector3f(JsonObject jsonObject, String key) {
            JsonArray jsonArray = GsonHelper.getAsJsonArray(jsonObject, key);
            if (jsonArray.size() != 3) {
                throw new JsonParseException("Expected 3 " + key + " values, found: " + jsonArray.size());
            }
            float[] fArray = new float[3];
            for (int i = 0; i < fArray.length; ++i) {
                fArray[i] = GsonHelper.convertToFloat(jsonArray.get(i), key + "[" + i + "]");
            }
            return new Vector3f(fArray[0], fArray[1], fArray[2]);
        }
    }
}
