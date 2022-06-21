package me.dalekcraft.structureedit.assets.blockstates.multipart;

import com.google.common.base.MoreObjects;
import com.google.common.base.Splitter;
import me.dalekcraft.structureedit.schematic.container.BlockState;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class KeyValueCondition implements Condition {
    private static final Splitter PIPE_SPLITTER = Splitter.on('|').omitEmptyStrings();
    private final String key;
    private final String value;

    public KeyValueCondition(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public Predicate<BlockState> getPredicate(BlockState blockState) {
        String key = this.key;
        String value = this.value;
        boolean negated = !value.isEmpty() && value.charAt(0) == '!';
        if (negated) {
            value = value.substring(1);
        }
        List<String> orLists = PIPE_SPLITTER.splitToList(value);
        Predicate<BlockState> predicate;
        if (orLists.size() == 1) {
            predicate = getBlockStatePredicate(key, value);
        } else {
            List<Predicate<BlockState>> predicates = orLists.stream().map(value2 -> getBlockStatePredicate(key, value2)).toList();
            predicate = blockState2 -> predicates.stream().anyMatch(predicate1 -> predicate1.test(blockState2));
        }
        return negated ? predicate.negate() : predicate;
    }

    private Predicate<BlockState> getBlockStatePredicate(String key, String value) {
        return blockState -> Objects.equals(blockState.getProperties().get(key), value);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("key", key).add("value", value).toString();
    }
}
