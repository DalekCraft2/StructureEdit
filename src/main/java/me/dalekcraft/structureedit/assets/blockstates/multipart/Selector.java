package me.dalekcraft.structureedit.assets.blockstates.multipart;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Streams;
import com.google.gson.*;
import me.dalekcraft.structureedit.assets.blockstates.MultiVariant;
import me.dalekcraft.structureedit.schematic.container.BlockState;
import me.dalekcraft.structureedit.util.GsonHelper;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Selector {
    private final Condition condition;
    private final MultiVariant variant;

    public Selector(Condition condition, MultiVariant variant) {
        if (condition == null) {
            throw new IllegalArgumentException("Missing condition for selector");
        }
        if (variant == null) {
            throw new IllegalArgumentException("Missing variant for selector");
        }
        this.condition = condition;
        this.variant = variant;
    }

    public MultiVariant getVariant() {
        return variant;
    }

    public Predicate<BlockState> getPredicate(BlockState stateDefinition) {
        return condition.getPredicate(stateDefinition);
    }

    public boolean equals(Object obj) {
        return this == obj;
    }

    public int hashCode() {
        return System.identityHashCode(this);
    }

    public static class Deserializer implements JsonDeserializer<Selector> {
        @VisibleForTesting
        static Condition getCondition(JsonObject jsonObject) {
            Set<Map.Entry<String, JsonElement>> set = jsonObject.entrySet();
            if (set.isEmpty()) {
                throw new JsonParseException("No elements found in selector");
            }
            if (set.size() == 1) {
                if (jsonObject.has(OrCondition.TOKEN)) {
                    List<Condition> list = Streams.stream(GsonHelper.getAsJsonArray(jsonObject, OrCondition.TOKEN)).map(jsonElement -> getCondition(jsonElement.getAsJsonObject())).collect(Collectors.toList());
                    return new OrCondition(list);
                }
                if (jsonObject.has(AndCondition.TOKEN)) {
                    List<Condition> list = Streams.stream(GsonHelper.getAsJsonArray(jsonObject, AndCondition.TOKEN)).map(jsonElement -> getCondition(jsonElement.getAsJsonObject())).collect(Collectors.toList());
                    return new AndCondition(list);
                }
                return getKeyValueCondition(set.iterator().next());
            }
            return new AndCondition(set.stream().map(Deserializer::getKeyValueCondition).collect(Collectors.toList()));
        }

        private static Condition getKeyValueCondition(Map.Entry<String, JsonElement> entry) {
            return new KeyValueCondition(entry.getKey(), entry.getValue().getAsString());
        }

        @Override
        public Selector deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            return new Selector(getSelector(jsonObject), context.deserialize(jsonObject.get("apply"), MultiVariant.class));
        }

        private Condition getSelector(JsonObject jsonObject) {
            if (jsonObject.has("when")) {
                return getCondition(GsonHelper.getAsJsonObject(jsonObject, "when"));
            }
            return Condition.TRUE;
        }
    }
}
