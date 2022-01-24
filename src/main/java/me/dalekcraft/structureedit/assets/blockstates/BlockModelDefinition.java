package me.dalekcraft.structureedit.assets.blockstates;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.*;
import me.dalekcraft.structureedit.assets.blockstates.multipart.MultiPart;
import me.dalekcraft.structureedit.assets.blockstates.multipart.Selector;
import me.dalekcraft.structureedit.schematic.container.BlockState;
import me.dalekcraft.structureedit.util.GsonHelper;
import org.jetbrains.annotations.Nullable;

import java.io.Reader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BlockModelDefinition {
    private final Map<String, MultiVariant> variants = Maps.newLinkedHashMap();
    private MultiPart multiPart;

    public BlockModelDefinition(Map<String, MultiVariant> variants, MultiPart multiPart) {
        this.multiPart = multiPart;
        this.variants.putAll(variants);
    }

    public BlockModelDefinition(List<BlockModelDefinition> list) {
        BlockModelDefinition blockModelDefinition = null;
        for (BlockModelDefinition blockModelDefinition2 : list) {
            if (blockModelDefinition2.isMultiPart()) {
                variants.clear();
                blockModelDefinition = blockModelDefinition2;
            }
            variants.putAll(blockModelDefinition2.variants);
        }
        if (blockModelDefinition != null) {
            multiPart = blockModelDefinition.multiPart;
        }
    }

    public static BlockModelDefinition fromStream(Context context, Reader reader) {
        return GsonHelper.fromJson(context.gson, reader, BlockModelDefinition.class);
    }

    @VisibleForTesting
    public boolean hasVariant(String key) {
        return variants.get(key) != null;
    }

    @VisibleForTesting
    public MultiVariant getVariant(String key) {
        MultiVariant multiVariant = variants.get(key);
        if (multiVariant == null) {
            throw new MissingVariantException();
        }
        return multiVariant;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof BlockModelDefinition blockModelDefinition) {
            if (variants.equals(blockModelDefinition.variants)) {
                return isMultiPart() ? multiPart.equals(blockModelDefinition.multiPart) : !blockModelDefinition.isMultiPart();
            }
        }
        return false;
    }

    public int hashCode() {
        return 31 * variants.hashCode() + (isMultiPart() ? multiPart.hashCode() : 0);
    }

    public Map<String, MultiVariant> getVariants() {
        return variants;
    }

    @VisibleForTesting
    public Set<MultiVariant> getMultiVariants() {
        Set<MultiVariant> hashSet = Sets.newHashSet(variants.values());
        if (isMultiPart()) {
            hashSet.addAll(multiPart.getMultiVariants());
        }
        return hashSet;
    }

    public boolean isMultiPart() {
        return multiPart != null;
    }

    public MultiPart getMultiPart() {
        return multiPart;
    }

    public static final class Context {
        private final Gson gson = new GsonBuilder().registerTypeAdapter(BlockModelDefinition.class, new Deserializer()).registerTypeAdapter(Variant.class, new Variant.Deserializer()).registerTypeAdapter(MultiVariant.class, new MultiVariant.Deserializer()).registerTypeAdapter(MultiPart.class, new MultiPart.Deserializer(this)).registerTypeAdapter(Selector.class, new Selector.Deserializer()).create();
        private BlockState definition;

        public BlockState getDefinition() {
            return definition;
        }

        public void setDefinition(BlockState definition) {
            this.definition = definition;
        }
    }

    protected static class MissingVariantException extends RuntimeException {
    }

    public static class Deserializer implements JsonDeserializer<BlockModelDefinition> {
        @Override
        public BlockModelDefinition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            Map<String, MultiVariant> map = getVariants(context, jsonObject);
            MultiPart multiPart = getMultiPart(context, jsonObject);
            if (map.isEmpty() && (multiPart == null || multiPart.getMultiVariants().isEmpty())) {
                throw new JsonParseException("Neither 'variants' nor 'multipart' found");
            }
            return new BlockModelDefinition(map, multiPart);
        }

        protected Map<String, MultiVariant> getVariants(JsonDeserializationContext context, JsonObject jsonObject) {
            Map<String, MultiVariant> hashMap = Maps.newHashMap();
            if (jsonObject.has("variants")) {
                JsonObject jsonObject2 = GsonHelper.getAsJsonObject(jsonObject, "variants");
                for (Map.Entry<String, JsonElement> entry : jsonObject2.entrySet()) {
                    hashMap.put(entry.getKey(), context.deserialize(entry.getValue(), MultiVariant.class));
                }
            }
            return hashMap;
        }

        @Nullable
        protected MultiPart getMultiPart(JsonDeserializationContext context, JsonObject jsonObject) {
            if (!jsonObject.has("multipart")) {
                return null;
            }
            JsonArray jsonArray = GsonHelper.getAsJsonArray(jsonObject, "multipart");
            return context.deserialize(jsonArray, MultiPart.class);
        }
    }
}
