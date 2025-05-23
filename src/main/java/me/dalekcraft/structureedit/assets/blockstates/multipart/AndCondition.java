package me.dalekcraft.structureedit.assets.blockstates.multipart;

import com.google.common.collect.Streams;
import me.dalekcraft.structureedit.schematic.container.BlockState;

import java.util.List;
import java.util.function.Predicate;

public class AndCondition implements Condition {
    public static final String TOKEN = "AND";
    private final Iterable<? extends Condition> conditions;

    public AndCondition(Iterable<? extends Condition> conditions) {
        this.conditions = conditions;
    }

    @Override
    public Predicate<BlockState> getPredicate(BlockState blockState) {
        List<Predicate<BlockState>> predicates = Streams.stream(conditions).map(condition -> condition.getPredicate(blockState)).toList();
        return blockState2 -> predicates.stream().allMatch(predicate -> predicate.test(blockState2));
    }
}
