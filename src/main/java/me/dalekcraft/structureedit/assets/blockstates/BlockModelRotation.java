package me.dalekcraft.structureedit.assets.blockstates;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum BlockModelRotation {
    X0_Y0(0, 0),
    X0_Y90(0, 90),
    X0_Y180(0, 180),
    X0_Y270(0, 270),
    X90_Y0(90, 0),
    X90_Y90(90, 90),
    X90_Y180(90, 180),
    X90_Y270(90, 270),
    X180_Y0(180, 0),
    X180_Y90(180, 90),
    X180_Y180(180, 180),
    X180_Y270(180, 270),
    X270_Y0(270, 0),
    X270_Y90(270, 90),
    X270_Y180(270, 180),
    X270_Y270(270, 270);

    private static final int DEGREES = 360;
    private static final Map<Integer, BlockModelRotation> BY_INDEX;

    static {
        BY_INDEX = Arrays.stream(values()).collect(Collectors.toMap(blockModelRotation -> blockModelRotation.index, blockModelRotation -> blockModelRotation));
    }

    private final int xRotation;
    private final int yRotation;
    private final int index;

    BlockModelRotation(int xRotation, int yRotation) {
        this.xRotation = xRotation;
        this.yRotation = yRotation;
        index = getIndex(xRotation, yRotation);
    }

    private static int getIndex(int xRotation, int yRotation) {
        return xRotation * 360 + yRotation;
    }

    public static BlockModelRotation by(int xRotation, int yRotation) {
        return BY_INDEX.get(getIndex(Math.floorMod(xRotation, 360), Math.floorMod(yRotation, 360)));
    }

    public int getXRotation() {
        return xRotation;
    }

    public int getYRotation() {
        return yRotation;
    }
}
