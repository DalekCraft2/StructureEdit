package me.dalekcraft.structureedit.assets.blockstates.multipart;

import me.dalekcraft.structureedit.schematic.container.BlockState;

import java.util.function.Predicate;

@FunctionalInterface
public interface Condition {
    Condition TRUE = blockState -> blockState2 -> true;
    Condition FALSE = blockState -> blockState2 -> false;

    Predicate<BlockState> getPredicate(BlockState blockState);
}
