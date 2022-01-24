package me.dalekcraft.structureedit.util;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.BigInteger;

public final class GsonHelper {

    private static final Gson GSON = new GsonBuilder().create();

    private GsonHelper() {
        throw new UnsupportedOperationException();
    }

    public static boolean isStringValue(JsonObject jsonObject, String key) {
        if (!isValidPrimitive(jsonObject, key)) {
            return false;
        }
        return jsonObject.getAsJsonPrimitive(key).isString();
    }

    public static boolean isStringValue(JsonElement jsonElement) {
        if (!jsonElement.isJsonPrimitive()) {
            return false;
        }
        return jsonElement.getAsJsonPrimitive().isString();
    }

    public static boolean isNumberValue(JsonObject jsonObject, String key) {
        if (!isValidPrimitive(jsonObject, key)) {
            return false;
        }
        return jsonObject.getAsJsonPrimitive().isNumber();
    }

    public static boolean isNumberValue(JsonElement jsonElement) {
        if (!jsonElement.isJsonPrimitive()) {
            return false;
        }
        return jsonElement.getAsJsonPrimitive().isNumber();
    }

    public static boolean isBooleanValue(JsonObject jsonObject, String key) {
        if (!isValidPrimitive(jsonObject, key)) {
            return false;
        }
        return jsonObject.getAsJsonPrimitive(key).isBoolean();
    }

    public static boolean isBooleanValue(JsonElement jsonElement) {
        if (!jsonElement.isJsonPrimitive()) {
            return false;
        }
        return jsonElement.getAsJsonPrimitive().isBoolean();
    }

    public static boolean isArrayNode(JsonObject jsonObject, String key) {
        if (!isValidNode(jsonObject, key)) {
            return false;
        }
        return jsonObject.get(key).isJsonArray();
    }

    public static boolean isObjectNode(JsonObject jsonObject, String key) {
        if (!isValidNode(jsonObject, key)) {
            return false;
        }
        return jsonObject.get(key).isJsonObject();
    }

    public static boolean isValidPrimitive(JsonObject jsonObject, String key) {
        if (!isValidNode(jsonObject, key)) {
            return false;
        }
        return jsonObject.get(key).isJsonPrimitive();
    }

    public static boolean isValidNode(JsonObject jsonObject, String key) {
        if (jsonObject == null) {
            return false;
        }
        return jsonObject.get(key) != null;
    }

    public static String convertToString(JsonElement jsonElement, String key) {
        if (jsonElement.isJsonPrimitive()) {
            return jsonElement.getAsString();
        }
        throw new JsonSyntaxException("Expected " + key + " to be a string, was " + getType(jsonElement));
    }

    public static String getAsString(JsonObject jsonObject, String key) {
        if (jsonObject.has(key)) {
            return convertToString(jsonObject.get(key), key);
        }
        throw new JsonSyntaxException("Missing " + key + ", expected to find a string");
    }

    @Nullable
    public static String getAsString(JsonObject jsonObject, String key, @Nullable String defaultValue) {
        if (jsonObject.has(key)) {
            return convertToString(jsonObject.get(key), key);
        }
        return defaultValue;
    }

    public static boolean convertToBoolean(JsonElement jsonElement, String key) {
        if (jsonElement.isJsonPrimitive()) {
            return jsonElement.getAsBoolean();
        }
        throw new JsonSyntaxException("Expected " + key + " to be a Boolean, was " + getType(jsonElement));
    }

    public static boolean getAsBoolean(JsonObject jsonObject, String key) {
        if (jsonObject.has(key)) {
            return convertToBoolean(jsonObject.get(key), key);
        }
        throw new JsonSyntaxException("Missing " + key + ", expected to find a Boolean");
    }

    public static boolean getAsBoolean(JsonObject jsonObject, String key, boolean defaultValue) {
        if (jsonObject.has(key)) {
            return convertToBoolean(jsonObject.get(key), key);
        }
        return defaultValue;
    }

    public static double convertToDouble(JsonElement jsonElement, String key) {
        if (jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isNumber()) {
            return jsonElement.getAsDouble();
        }
        throw new JsonSyntaxException("Expected " + key + " to be a Double, was " + getType(jsonElement));
    }

    public static double getAsDouble(JsonObject jsonObject, String key) {
        if (jsonObject.has(key)) {
            return convertToDouble(jsonObject.get(key), key);
        }
        throw new JsonSyntaxException("Missing " + key + ", expected to find a Double");
    }

    public static double getAsDouble(JsonObject jsonObject, String key, double defaultValue) {
        if (jsonObject.has(key)) {
            return convertToDouble(jsonObject.get(key), key);
        }
        return defaultValue;
    }

    public static float convertToFloat(JsonElement jsonElement, String key) {
        if (jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isNumber()) {
            return jsonElement.getAsFloat();
        }
        throw new JsonSyntaxException("Expected " + key + " to be a Float, was " + getType(jsonElement));
    }

    public static float getAsFloat(JsonObject jsonObject, String key) {
        if (jsonObject.has(key)) {
            return convertToFloat(jsonObject.get(key), key);
        }
        throw new JsonSyntaxException("Missing " + key + ", expected to find a Float");
    }

    public static float getAsFloat(JsonObject jsonObject, String key, float defaultValue) {
        if (jsonObject.has(key)) {
            return convertToFloat(jsonObject.get(key), key);
        }
        return defaultValue;
    }

    public static long convertToLong(JsonElement jsonElement, String key) {
        if (jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isNumber()) {
            return jsonElement.getAsLong();
        }
        throw new JsonSyntaxException("Expected " + key + " to be a Long, was " + getType(jsonElement));
    }

    public static long getAsLong(JsonObject jsonObject, String key) {
        if (jsonObject.has(key)) {
            return convertToLong(jsonObject.get(key), key);
        }
        throw new JsonSyntaxException("Missing " + key + ", expected to find a Long");
    }

    public static long getAsLong(JsonObject jsonObject, String key, long defaultValue) {
        if (jsonObject.has(key)) {
            return convertToLong(jsonObject.get(key), key);
        }
        return defaultValue;
    }

    public static int convertToInt(JsonElement jsonElement, String key) {
        if (jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isNumber()) {
            return jsonElement.getAsInt();
        }
        throw new JsonSyntaxException("Expected " + key + " to be a Int, was " + getType(jsonElement));
    }

    public static int getAsInt(JsonObject jsonObject, String key) {
        if (jsonObject.has(key)) {
            return convertToInt(jsonObject.get(key), key);
        }
        throw new JsonSyntaxException("Missing " + key + ", expected to find a Int");
    }

    public static int getAsInt(JsonObject jsonObject, String key, int defaultValue) {
        if (jsonObject.has(key)) {
            return convertToInt(jsonObject.get(key), key);
        }
        return defaultValue;
    }

    public static byte convertToByte(JsonElement jsonElement, String key) {
        if (jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isNumber()) {
            return jsonElement.getAsByte();
        }
        throw new JsonSyntaxException("Expected " + key + " to be a Byte, was " + getType(jsonElement));
    }

    public static byte getAsByte(JsonObject jsonObject, String key) {
        if (jsonObject.has(key)) {
            return convertToByte(jsonObject.get(key), key);
        }
        throw new JsonSyntaxException("Missing " + key + ", expected to find a Byte");
    }

    public static byte getAsByte(JsonObject jsonObject, String key, byte defaultValue) {
        if (jsonObject.has(key)) {
            return convertToByte(jsonObject.get(key), key);
        }
        return defaultValue;
    }

    public static char convertToCharacter(JsonElement jsonElement, String key) {
        if (jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isNumber()) {
            return jsonElement.getAsCharacter();
        }
        throw new JsonSyntaxException("Expected " + key + " to be a Character, was " + getType(jsonElement));
    }

    public static char getAsCharacter(JsonObject jsonObject, String key) {
        if (jsonObject.has(key)) {
            return convertToCharacter(jsonObject.get(key), key);
        }
        throw new JsonSyntaxException("Missing " + key + ", expected to find a Character");
    }

    public static char getAsCharacter(JsonObject jsonObject, String key, char defaultValue) {
        if (jsonObject.has(key)) {
            return convertToCharacter(jsonObject.get(key), key);
        }
        return defaultValue;
    }

    public static BigDecimal convertToBigDecimal(JsonElement jsonElement, String key) {
        if (jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isNumber()) {
            return jsonElement.getAsBigDecimal();
        }
        throw new JsonSyntaxException("Expected " + key + " to be a BigDecimal, was " + getType(jsonElement));
    }

    public static BigDecimal getAsBigDecimal(JsonObject jsonObject, String key) {
        if (jsonObject.has(key)) {
            return convertToBigDecimal(jsonObject.get(key), key);
        }
        throw new JsonSyntaxException("Missing " + key + ", expected to find a BigDecimal");
    }

    public static BigDecimal getAsBigDecimal(JsonObject jsonObject, String key, BigDecimal defaultValue) {
        if (jsonObject.has(key)) {
            return convertToBigDecimal(jsonObject.get(key), key);
        }
        return defaultValue;
    }

    public static BigInteger convertToBigInteger(JsonElement jsonElement, String key) {
        if (jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isNumber()) {
            return jsonElement.getAsBigInteger();
        }
        throw new JsonSyntaxException("Expected " + key + " to be a BigInteger, was " + getType(jsonElement));
    }

    public static BigInteger getAsBigInteger(JsonObject jsonObject, String key) {
        if (jsonObject.has(key)) {
            return convertToBigInteger(jsonObject.get(key), key);
        }
        throw new JsonSyntaxException("Missing " + key + ", expected to find a BigInteger");
    }

    public static BigInteger getAsBigInteger(JsonObject jsonObject, String key, BigInteger defaultValue) {
        if (jsonObject.has(key)) {
            return convertToBigInteger(jsonObject.get(key), key);
        }
        return defaultValue;
    }

    public static short convertToShort(JsonElement jsonElement, String key) {
        if (jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isNumber()) {
            return jsonElement.getAsShort();
        }
        throw new JsonSyntaxException("Expected " + key + " to be a Short, was " + getType(jsonElement));
    }

    public static short getAsShort(JsonObject jsonObject, String key) {
        if (jsonObject.has(key)) {
            return convertToShort(jsonObject.get(key), key);
        }
        throw new JsonSyntaxException("Missing " + key + ", expected to find a Short");
    }

    public static short getAsShort(JsonObject jsonObject, String key, short defaultValue) {
        if (jsonObject.has(key)) {
            return convertToShort(jsonObject.get(key), key);
        }
        return defaultValue;
    }

    public static JsonObject convertToJsonObject(JsonElement jsonElement, String key) {
        if (jsonElement.isJsonObject()) {
            return jsonElement.getAsJsonObject();
        }
        throw new JsonSyntaxException("Expected " + key + " to be a JsonObject, was " + getType(jsonElement));
    }

    public static JsonObject getAsJsonObject(JsonObject jsonObject, String key) {
        if (jsonObject.has(key)) {
            return convertToJsonObject(jsonObject.get(key), key);
        }
        throw new JsonSyntaxException("Missing " + key + ", expected to find a JsonObject");
    }

    @Nullable
    public static JsonObject getAsJsonObject(JsonObject jsonObject, String key, @Nullable JsonObject defaultValue) {
        if (jsonObject.has(key)) {
            return convertToJsonObject(jsonObject.get(key), key);
        }
        return defaultValue;
    }

    public static JsonArray convertToJsonArray(JsonElement jsonElement, String key) {
        if (jsonElement.isJsonArray()) {
            return jsonElement.getAsJsonArray();
        }
        throw new JsonSyntaxException("Expected " + key + " to be a JsonArray, was " + getType(jsonElement));
    }

    public static JsonArray getAsJsonArray(JsonObject jsonObject, String key) {
        if (jsonObject.has(key)) {
            return convertToJsonArray(jsonObject.get(key), key);
        }
        throw new JsonSyntaxException("Missing " + key + ", expected to find a JsonArray");
    }

    @Nullable
    public static JsonArray getAsJsonArray(JsonObject jsonObject, String key, @Nullable JsonArray defaultValue) {
        if (jsonObject.has(key)) {
            return convertToJsonArray(jsonObject.get(key), key);
        }
        return defaultValue;
    }

    public static <T> T convertToObject(@Nullable JsonElement jsonElement, String key, JsonDeserializationContext context, Class<? extends T> clazz) {
        if (jsonElement != null) {
            return context.deserialize(jsonElement, clazz);
        }
        throw new JsonSyntaxException("Missing " + key);
    }

    public static <T> T getAsObject(JsonObject jsonObject, String key, JsonDeserializationContext context, Class<? extends T> clazz) {
        if (jsonObject.has(key)) {
            return convertToObject(jsonObject.get(key), key, context, clazz);
        }
        throw new JsonSyntaxException("Missing " + key);
    }

    @Nullable
    public static <T> T getAsObject(JsonObject jsonObject, String key, @Nullable T defaultValue, JsonDeserializationContext context, Class<? extends T> clazz) {
        if (jsonObject.has(key)) {
            return convertToObject(jsonObject.get(key), key, context, clazz);
        }
        return defaultValue;
    }

    public static String getType(@Nullable JsonElement jsonElement) {
        String string = StringUtils.abbreviateMiddle(String.valueOf(jsonElement), "...", 10);
        if (jsonElement == null) {
            return "null (missing)";
        }
        if (jsonElement.isJsonNull()) {
            return "null (json)";
        }
        if (jsonElement.isJsonArray()) {
            return "an array (" + string + ")";
        }
        if (jsonElement.isJsonObject()) {
            return "an object (" + string + ")";
        }
        if (jsonElement.isJsonPrimitive()) {
            JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
            if (jsonPrimitive.isNumber()) {
                return "a number (" + string + ")";
            }
            if (jsonPrimitive.isBoolean()) {
                return "a boolean (" + string + ")";
            }
        }
        return string;
    }

    @Nullable
    public static <T> T fromJson(Gson gson, Reader reader, Class<T> clazz, boolean lenient) {
        try {
            JsonReader jsonReader = new JsonReader(reader);
            jsonReader.setLenient(lenient);
            return gson.getAdapter(clazz).read(jsonReader);
        } catch (IOException iOException) {
            throw new JsonParseException(iOException);
        }
    }

    @Nullable
    public static <T> T fromJson(Gson gson, Reader reader, TypeToken<T> typeToken, boolean lenient) {
        try {
            JsonReader jsonReader = new JsonReader(reader);
            jsonReader.setLenient(lenient);
            return gson.getAdapter(typeToken).read(jsonReader);
        } catch (IOException iOException) {
            throw new JsonParseException(iOException);
        }
    }

    @Nullable
    public static <T> T fromJson(Gson gson, String jsonString, TypeToken<T> typeToken, boolean lenient) {
        return fromJson(gson, new StringReader(jsonString), typeToken, lenient);
    }

    @Nullable
    public static <T> T fromJson(Gson gson, String jsonString, Class<T> clazz, boolean lenient) {
        return fromJson(gson, new StringReader(jsonString), clazz, lenient);
    }

    @Nullable
    public static <T> T fromJson(Gson gson, Reader reader, TypeToken<T> typeToken) {
        return fromJson(gson, reader, typeToken, false);
    }

    @Nullable
    public static <T> T fromJson(Gson gson, String jsonString, TypeToken<T> typeToken) {
        return fromJson(gson, jsonString, typeToken, false);
    }

    @Nullable
    public static <T> T fromJson(Gson gson, Reader reader, Class<T> clazz) {
        return fromJson(gson, reader, clazz, false);
    }

    @Nullable
    public static <T> T fromJson(Gson gson, String jsonString, Class<T> clazz) {
        return fromJson(gson, jsonString, clazz, false);
    }

    public static JsonObject parse(String jsonString, boolean lenient) {
        return parse(new StringReader(jsonString), lenient);
    }

    public static JsonObject parse(Reader reader, boolean lenient) {
        return fromJson(GSON, reader, JsonObject.class, lenient);
    }

    public static JsonObject parse(String jsonString) {
        return parse(jsonString, false);
    }

    public static JsonObject parse(Reader reader) {
        return parse(reader, false);
    }

    public static JsonArray parseArray(Reader reader) {
        return fromJson(GSON, reader, JsonArray.class, false);
    }
}
