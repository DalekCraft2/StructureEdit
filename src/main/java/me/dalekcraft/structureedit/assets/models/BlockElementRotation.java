package me.dalekcraft.structureedit.assets.models;

import me.dalekcraft.structureedit.util.Direction;
import org.joml.Vector3f;

public class BlockElementRotation {

    public final Vector3f origin;
    public final Direction.Axis axis;
    public final float angle;
    public final boolean rescale;

    public BlockElementRotation(Vector3f origin, Direction.Axis axis, float angle, boolean rescale) {
        this.origin = origin;
        this.axis = axis;
        this.angle = angle;
        this.rescale = rescale;
    }
}
