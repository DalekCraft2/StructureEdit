package me.dalekcraft.structureedit.assets.models;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.*;
import com.mojang.datafixers.util.Either;
import me.dalekcraft.structureedit.assets.ResourceLocation;
import me.dalekcraft.structureedit.assets.textures.MissingTexture;
import me.dalekcraft.structureedit.util.GsonHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.io.Reader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BlockModel {

    public static final String PARTICLE_TEXTURE_REFERENCE = "particle";
    @VisibleForTesting
    static final Gson GSON = new GsonBuilder().registerTypeAdapter(BlockModel.class, new Deserializer()).registerTypeAdapter(BlockElement.class, new BlockElement.Deserializer()).registerTypeAdapter(BlockElementFace.class, new BlockElementFace.Deserializer()).registerTypeAdapter(BlockFaceUv.class, new BlockFaceUv.Deserializer()).create();
    private static final Logger LOGGER = LogManager.getLogger();
    private static final char REFERENCE_CHAR = '#';
    @VisibleForTesting
    protected final Map<String, Either<Material, String>> textureMap;
    private final List<BlockElement> elements;
    private final boolean hasAmbientOcclusion;
    public String name = "";
    @Nullable
    protected BlockModel parent;
    @Nullable
    protected ResourceLocation parentLocation;

    public BlockModel(@Nullable ResourceLocation parentLocation, List<BlockElement> elements, Map<String, Either<Material, String>> textureMap, boolean hasAmbientOcclusion) {
        this.elements = elements;
        this.hasAmbientOcclusion = hasAmbientOcclusion;
        this.textureMap = textureMap;
        this.parentLocation = parentLocation;
    }

    public static BlockModel fromStream(Reader reader) {
        return GsonHelper.fromJson(GSON, reader, BlockModel.class);
    }

    static boolean isTextureReference(String reference) {
        return reference.charAt(0) == REFERENCE_CHAR;
    }

    public List<BlockElement> getElements() {
        if (elements.isEmpty() && parent != null) {
            return parent.getElements();
        }
        return elements;
    }

    public boolean hasAmbientOcclusion() {
        if (parent != null) {
            return parent.hasAmbientOcclusion();
        }
        return hasAmbientOcclusion;
    }

    public boolean isResolved() {
        return parentLocation == null || parent != null && parent.isResolved();
    }

    public void fillInheritance(Function<ResourceLocation, BlockModel> function) {
        BlockModel parent;
        Set<BlockModel> inheritanceSet = Sets.newLinkedHashSet();
        BlockModel child = this;
        while (child.parentLocation != null && child.parent == null) {
            inheritanceSet.add(child);
            parent = function.apply(child.parentLocation);
            if (parent == null) {
                LOGGER.warn("No parent '{}' while loading model '{}'", parentLocation, child);
            }
            if (inheritanceSet.contains(parent)) {
                LOGGER.warn("Found 'parent' loop while loading model '{}' in chain: {} -> {}", child, inheritanceSet.stream().map(BlockModel::toString).collect(Collectors.joining(" -> ")), parentLocation);
                parent = null;
            }
            if (parent == null) {
                child.parentLocation = MissingBlockModel.MISSING_MODEL_LOCATION;
                parent = function.apply(child.parentLocation);
            }
            child.parent = parent;
            child = child.parent;
        }
    }

    public boolean hasTexture(String reference) {
        return !MissingTexture.MISSING_TEXTURE_LOCATION.equals(getMaterial(reference).texture());
    }

    public Material getMaterial(String reference) {
        if (isTextureReference(reference)) {
            reference = reference.substring(1);
        }
        List<String> arrayList = Lists.newArrayList();
        Either<Material, String> either;
        Optional<Material> optional;
        while ((optional = (either = findTextureEntry(reference)).left()).isEmpty()) {
            reference = either.right().get();
            if (arrayList.contains(reference)) {
                LOGGER.warn("Unable to resolve texture due to reference chain {}->{} in {}", Joiner.on("->").join(arrayList), reference, name);
                return new Material(MissingTexture.MISSING_TEXTURE_LOCATION);
            }
            arrayList.add(reference);
        }
        return optional.get();
    }

    private Either<Material, String> findTextureEntry(String reference) {
        BlockModel blockModel = this;
        while (blockModel != null) {
            Either<Material, String> either = blockModel.textureMap.get(reference);
            if (either != null) {
                return either;
            }
            blockModel = blockModel.parent;
        }
        return Either.left(new Material(MissingTexture.MISSING_TEXTURE_LOCATION));
    }

    public BlockModel getRootModel() {
        return parent == null ? this : parent.getRootModel();
    }

    @Override
    public String toString() {
        return name;
    }

    public static class Deserializer implements JsonDeserializer<BlockModel> {
        private static final boolean DEFAULT_AMBIENT_OCCLUSION = true;

        private static Either<Material, String> parseTextureLocationOrReference(String reference) {
            if (isTextureReference(reference)) {
                return Either.right(reference.substring(1));
            }
            ResourceLocation texture = ResourceLocation.tryParse(reference);
            if (texture == null) {
                throw new JsonParseException(reference + " is not valid resource location");
            }
            return Either.left(new Material(texture));
        }

        @Override
        public BlockModel deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            List<BlockElement> list = getElements(context, jsonObject);
            String parentName = getParentName(jsonObject);
            Map<String, Either<Material, String>> map = getTextureMap(jsonObject);
            boolean bl = getAmbientOcclusion(jsonObject);
            ResourceLocation resourceLocation = parentName.isEmpty() ? null : new ResourceLocation(parentName);
            return new BlockModel(resourceLocation, list, map, bl);
        }

        private Map<String, Either<Material, String>> getTextureMap(JsonObject jsonObject) {
            Map<String, Either<Material, String>> hashMap = Maps.newHashMap();
            if (jsonObject.has("textures")) {
                JsonObject jsonObject2 = GsonHelper.getAsJsonObject(jsonObject, "textures");
                for (Map.Entry<String, JsonElement> entry : jsonObject2.entrySet()) {
                    hashMap.put(entry.getKey(), parseTextureLocationOrReference(entry.getValue().getAsString()));
                }
            }
            return hashMap;
        }

        private String getParentName(JsonObject jsonObject) {
            return GsonHelper.getAsString(jsonObject, "parent", "");
        }

        protected boolean getAmbientOcclusion(JsonObject jsonObject) {
            return GsonHelper.getAsBoolean(jsonObject, "ambientocclusion", DEFAULT_AMBIENT_OCCLUSION);
        }

        protected List<BlockElement> getElements(JsonDeserializationContext context, JsonObject jsonObject) {
            List<BlockElement> arrayList = Lists.newArrayList();
            if (jsonObject.has("elements")) {
                for (JsonElement jsonElement : GsonHelper.getAsJsonArray(jsonObject, "elements")) {
                    arrayList.add(context.deserialize(jsonElement, BlockElement.class));
                }
            }
            return arrayList;
        }
    }
}
