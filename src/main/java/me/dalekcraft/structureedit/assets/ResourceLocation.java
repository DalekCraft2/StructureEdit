package me.dalekcraft.structureedit.assets;

import com.google.gson.*;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.serialization.DataResult;
import me.dalekcraft.structureedit.util.GsonHelper;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

public class ResourceLocation implements Comparable<ResourceLocation> {
    public static final char NAMESPACE_SEPARATOR = ':';
    public static final String DEFAULT_NAMESPACE = "minecraft";
    public static final String REALMS_NAMESPACE = "realms";
    private static final SimpleCommandExceptionType ERROR_INVALID = new SimpleCommandExceptionType(new LiteralMessage("Invalid ID"));
    protected final String namespace;
    protected final String path;

    protected ResourceLocation(String[] namespacePathArray) {
        namespace = StringUtils.isEmpty(namespacePathArray[0]) ? DEFAULT_NAMESPACE : namespacePathArray[0];
        path = namespacePathArray[1];
        if (!isValidNamespace(namespace)) {
            throw new ResourceLocationException("Non [a-z0-9_.-] character in namespace of location: " + namespace + NAMESPACE_SEPARATOR + path);
        }
        if (!isValidPath(path)) {
            throw new ResourceLocationException("Non [a-z0-9/._-] character in path of location: " + namespace + NAMESPACE_SEPARATOR + path);
        }
    }

    public ResourceLocation(String path) {
        this(decompose(path, NAMESPACE_SEPARATOR));
    }

    public ResourceLocation(String namespace, String path) {
        this(new String[]{namespace, path});
    }

    public static ResourceLocation of(String path, char separator) {
        return new ResourceLocation(decompose(path, separator));
    }

    @Nullable
    public static ResourceLocation tryParse(String path) {
        try {
            return new ResourceLocation(path);
        } catch (ResourceLocationException e) {
            return null;
        }
    }

    protected static String[] decompose(String path, char separator) {
        String[] stringArray = {DEFAULT_NAMESPACE, path};
        int n = path.indexOf(separator);
        if (n >= 0) {
            stringArray[1] = path.substring(n + 1);
            if (n >= 1) {
                stringArray[0] = path.substring(0, n);
            }
        }
        return stringArray;
    }

    private static DataResult<ResourceLocation> read(String path) {
        try {
            return DataResult.success(new ResourceLocation(path));
        } catch (ResourceLocationException e) {
            return DataResult.error("Not a valid resource location: " + path + " " + e.getMessage());
        }
    }

    public static ResourceLocation read(StringReader stringReader) throws CommandSyntaxException {
        int n = stringReader.getCursor();
        while (stringReader.canRead() && isAllowedInResourceLocation(stringReader.peek())) {
            stringReader.skip();
        }
        String path = stringReader.getString().substring(n, stringReader.getCursor());
        try {
            return new ResourceLocation(path);
        } catch (ResourceLocationException e) {
            stringReader.setCursor(n);
            throw ERROR_INVALID.createWithContext(stringReader);
        }
    }

    public static boolean isAllowedInResourceLocation(char c) {
        return c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c == '_' || c == NAMESPACE_SEPARATOR || c == '/' || c == '.' || c == '-';
    }

    private static boolean isValidPath(String path) {
        for (int i = 0; i < path.length(); ++i) {
            if (validPathChar(path.charAt(i))) {
                continue;
            }
            return false;
        }
        return true;
    }

    private static boolean isValidNamespace(String namespace) {
        for (int i = 0; i < namespace.length(); ++i) {
            if (validNamespaceChar(namespace.charAt(i))) {
                continue;
            }
            return false;
        }
        return true;
    }

    public static boolean validPathChar(char c) {
        return c == '_' || c == '-' || c >= 'a' && c <= 'z' || c >= '0' && c <= '9' || c == '/' || c == '.';
    }

    private static boolean validNamespaceChar(char c) {
        return c == '_' || c == '-' || c >= 'a' && c <= 'z' || c >= '0' && c <= '9' || c == '.';
    }

    public static boolean isValidResourceLocation(String path) {
        String[] stringArray = decompose(path, NAMESPACE_SEPARATOR);
        return isValidNamespace(StringUtils.isEmpty(stringArray[0]) ? DEFAULT_NAMESPACE : stringArray[0]) && isValidPath(stringArray[1]);
    }

    public String getPath() {
        return path;
    }

    public String getNamespace() {
        return namespace;
    }

    @Override
    public String toString() {
        return namespace + NAMESPACE_SEPARATOR + path;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ResourceLocation resourceLocation) {
            return namespace.equals(resourceLocation.namespace) && path.equals(resourceLocation.path);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return 31 * namespace.hashCode() + path.hashCode();
    }

    @Override
    public int compareTo(ResourceLocation resourceLocation) {
        int n = path.compareTo(resourceLocation.path);
        if (n == 0) {
            n = namespace.compareTo(resourceLocation.namespace);
        }
        return n;
    }

    public String toDebugFileName() {
        return toString().replace('/', '_').replace(NAMESPACE_SEPARATOR, '_');
    }

    public static class Serializer implements JsonDeserializer<ResourceLocation>, JsonSerializer<ResourceLocation> {
        @Override
        public ResourceLocation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new ResourceLocation(GsonHelper.convertToString(json, "location"));
        }

        @Override
        public JsonElement serialize(ResourceLocation resourceLocation, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(resourceLocation.toString());
        }
    }
}

