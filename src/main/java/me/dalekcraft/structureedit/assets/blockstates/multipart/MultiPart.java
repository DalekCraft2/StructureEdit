package me.dalekcraft.structureedit.assets.blockstates.multipart;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.*;
import me.dalekcraft.structureedit.assets.blockstates.BlockModelDefinition;
import me.dalekcraft.structureedit.assets.blockstates.MultiVariant;
import me.dalekcraft.structureedit.schematic.container.BlockState;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class MultiPart {
    private final BlockState definition;
    private final List<Selector> selectors;

    public MultiPart(BlockState definition, List<Selector> selectors) {
        this.definition = definition;
        this.selectors = selectors;
    }

    public List<Selector> getSelectors() {
        return selectors;
    }

    public Set<MultiVariant> getMultiVariants() {
        Set<MultiVariant> multiVariants = Sets.newHashSet();
        for (Selector selector : selectors) {
            multiVariants.add(selector.getVariant());
        }
        return multiVariants;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof MultiPart multiPart) {
            return Objects.equals(definition, multiPart.definition) && Objects.equals(selectors, multiPart.selectors);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(definition, selectors);
    }

    public static class Deserializer implements JsonDeserializer<MultiPart> {
        private final BlockModelDefinition.Context context;

        public Deserializer(BlockModelDefinition.Context context) {
            this.context = context;
        }

        @Override
        public MultiPart deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new MultiPart(this.context.getDefinition(), getSelectors(context, json.getAsJsonArray()));
        }

        private List<Selector> getSelectors(JsonDeserializationContext context, JsonArray jsonArray) {
            List<Selector> selectors = Lists.newArrayList();
            for (JsonElement jsonElement : jsonArray) {
                selectors.add(context.deserialize(jsonElement, Selector.class));
            }
            return selectors;
        }
    }
}
