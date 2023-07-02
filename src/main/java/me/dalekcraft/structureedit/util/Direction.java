package me.dalekcraft.structureedit.util;

import com.google.common.collect.Iterators;
import com.mojang.serialization.DataResult;
import javafx.geometry.Point3D;
import javafx.scene.transform.Transform;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Direction {
    DOWN(0, 1, -1, "down", AxisDirection.NEGATIVE, Axis.Y, new Vector3i(0, -1, 0)),
    UP(1, 0, -1, "up", AxisDirection.POSITIVE, Axis.Y, new Vector3i(0, 1, 0)),
    NORTH(2, 3, 2, "north", AxisDirection.NEGATIVE, Axis.Z, new Vector3i(0, 0, -1)),
    SOUTH(3, 2, 0, "south", AxisDirection.POSITIVE, Axis.Z, new Vector3i(0, 0, 1)),
    WEST(4, 5, 1, "west", AxisDirection.NEGATIVE, Axis.X, new Vector3i(-1, 0, 0)),
    EAST(5, 4, 3, "east", AxisDirection.POSITIVE, Axis.X, new Vector3i(1, 0, 0));
    private static final Direction[] VALUES;
    private static final Map<String, Direction> BY_NAME;
    private static final Direction[] BY_3D_DATA;
    private static final Direction[] BY_2D_DATA;

    static {
        VALUES = values();
        BY_NAME = Arrays.stream(VALUES).collect(Collectors.toMap(Direction::getName, direction -> direction));
        BY_3D_DATA = Arrays.stream(VALUES).sorted(Comparator.comparingInt(direction -> direction.data3d)).toArray(Direction[]::new);
        BY_2D_DATA = Arrays.stream(VALUES).filter(direction -> direction.getAxis().isHorizontal()).sorted(Comparator.comparingInt(direction -> direction.data2d)).toArray(Direction[]::new);
    }

    private final int data3d;
    private final int oppositeIndex;
    private final int data2d;
    private final String name;
    private final Axis axis;
    private final AxisDirection axisDirection;
    private final Vector3i normal;

    Direction(int data3d, int oppositeIndex, int data2d, String name, AxisDirection axisDirection, Axis axis, Vector3i normal) {
        this.data3d = data3d;
        this.data2d = data2d;
        this.oppositeIndex = oppositeIndex;
        this.name = name;
        this.axis = axis;
        this.axisDirection = axisDirection;
        this.normal = normal;
    }

    private static Direction[] makeDirectionArray(Direction direction, Direction direction2, Direction direction3) {
        return new Direction[]{direction, direction2, direction3, direction3.getOpposite(), direction2.getOpposite(), direction.getOpposite()};
    }

    public static Direction rotate(Transform transform, Direction direction) {
        Vector3i vec3i = direction.getNormal();
        Point3D vector4f = new Point3D(vec3i.x, vec3i.y, vec3i.z);
        vector4f = transform.transform(vector4f);
        return getNearest(vector4f.getX(), vector4f.getY(), vector4f.getZ());
    }

    @Nullable
    public static Direction byName(@Nullable String name) {
        if (name == null) {
            return null;
        }
        return BY_NAME.get(name.toLowerCase(Locale.ROOT));
    }

    public static Direction from3DDataValue(int n) {
        return BY_3D_DATA[Math.abs(n % BY_3D_DATA.length)];
    }

    public static Direction from2DDataValue(int n) {
        return BY_2D_DATA[Math.abs(n % BY_2D_DATA.length)];
    }

    public static Direction fromYRot(double d) {
        return from2DDataValue((int) Math.floor(d / 90.0 + 0.5) & 3);
    }

    public static Direction fromAxisAndDirection(Axis axis, AxisDirection axisDirection) {
        return switch (axis) {
            case X -> {
                if (axisDirection == AxisDirection.POSITIVE) {
                    yield EAST;
                }
                yield WEST;
            }
            case Y -> {
                if (axisDirection == AxisDirection.POSITIVE) {
                    yield UP;
                }
                yield DOWN;
            }
            case Z -> axisDirection == AxisDirection.POSITIVE ? SOUTH : NORTH;
        };
    }

    public static Direction getNearest(double d, double d2, double d3) {
        return getNearest((float) d, (float) d2, (float) d3);
    }

    public static Direction getNearest(float f, float f2, float f3) {
        Direction direction = NORTH;
        float f4 = Float.MIN_VALUE;
        for (Direction direction2 : VALUES) {
            float f5 = f * direction2.normal.x() + f2 * direction2.normal.y() + f3 * direction2.normal.z();
            if (!(f5 > f4)) {
                continue;
            }
            f4 = f5;
            direction = direction2;
        }
        return direction;
    }

    private static DataResult<Direction> verifyVertical(Direction direction) {
        return direction.getAxis().isVertical() ? DataResult.success(direction) : DataResult.error((String) "Expected a vertical direction");
    }

    public static Direction get(AxisDirection axisDirection, Axis axis) {
        for (Direction direction : VALUES) {
            if (direction.getAxisDirection() != axisDirection || direction.getAxis() != axis) {
                continue;
            }
            return direction;
        }
        throw new IllegalArgumentException("No such direction: " + axisDirection + " " + axis);
    }

    public int get3DDataValue() {
        return data3d;
    }

    public int get2DDataValue() {
        return data2d;
    }

    public AxisDirection getAxisDirection() {
        return axisDirection;
    }

    public Direction getOpposite() {
        return from3DDataValue(oppositeIndex);
    }

    public Direction getClockWise(Axis axis) {
        return switch (axis) {
            case X -> {
                if (this == WEST || this == EAST) {
                    yield this;
                }
                yield getClockWiseX();
            }
            case Y -> {
                if (this == UP || this == DOWN) {
                    yield this;
                }
                yield getClockWise();
            }
            case Z -> this == NORTH || this == SOUTH ? this : getClockWiseZ();
        };
    }

    public Direction getCounterClockWise(Axis axis) {
        return switch (axis) {
            case X -> {
                if (this == WEST || this == EAST) {
                    yield this;
                }
                yield getCounterClockWiseX();
            }
            case Y -> {
                if (this == UP || this == DOWN) {
                    yield this;
                }
                yield getCounterClockWise();
            }
            case Z -> this == NORTH || this == SOUTH ? this : getCounterClockWiseZ();
        };
    }

    public Direction getClockWise() {
        return switch (this) {
            case NORTH -> EAST;
            case EAST -> SOUTH;
            case SOUTH -> WEST;
            case WEST -> NORTH;
            default -> throw new IllegalStateException("Unable to get Y-rotated facing of " + this);
        };
    }

    private Direction getClockWiseX() {
        return switch (this) {
            case UP -> NORTH;
            case NORTH -> DOWN;
            case DOWN -> SOUTH;
            case SOUTH -> UP;
            default -> throw new IllegalStateException("Unable to get X-rotated facing of " + this);
        };
    }

    private Direction getCounterClockWiseX() {
        return switch (this) {
            case UP -> SOUTH;
            case SOUTH -> DOWN;
            case DOWN -> NORTH;
            case NORTH -> UP;
            default -> throw new IllegalStateException("Unable to get X-rotated facing of " + this);
        };
    }

    private Direction getClockWiseZ() {
        return switch (this) {
            case UP -> EAST;
            case EAST -> DOWN;
            case DOWN -> WEST;
            case WEST -> UP;
            default -> throw new IllegalStateException("Unable to get Z-rotated facing of " + this);
        };
    }

    private Direction getCounterClockWiseZ() {
        return switch (this) {
            case UP -> WEST;
            case WEST -> DOWN;
            case DOWN -> EAST;
            case EAST -> UP;
            default -> throw new IllegalStateException("Unable to get Z-rotated facing of " + this);
        };
    }

    public Direction getCounterClockWise() {
        return switch (this) {
            case NORTH -> WEST;
            case EAST -> NORTH;
            case SOUTH -> EAST;
            case WEST -> SOUTH;
            default -> throw new IllegalStateException("Unable to get CCW facing of " + this);
        };
    }

    public int getStepX() {
        return normal.x();
    }

    public int getStepY() {
        return normal.y();
    }

    public int getStepZ() {
        return normal.z();
    }

    public Vector3f step() {
        return new Vector3f(getStepX(), getStepY(), getStepZ());
    }

    public String getName() {
        return name;
    }

    public Axis getAxis() {
        return axis;
    }

    public float toYRot() {
        return (data2d & 3) * 90;
    }

    @Override
    public String toString() {
        return name;
    }

    public Vector3i getNormal() {
        return normal;
    }

    public boolean isFacingAngle(float f) {
        float f2 = f * ((float) Math.PI / 180);
        float f3 = (float) -Math.sin(f2);
        float f4 = (float) Math.cos(f2);
        return normal.x() * f3 + normal.z() * f4 > 0.0f;
    }

    public enum Axis implements Predicate<Direction> {
        X("x") {
            @Override
            public int choose(int x, int y, int z) {
                return x;
            }

            @Override
            public double choose(double x, double y, double z) {
                return x;
            }
        },
        Y("y") {
            @Override
            public int choose(int x, int y, int z) {
                return y;
            }

            @Override
            public double choose(double x, double y, double z) {
                return y;
            }
        },
        Z("z") {
            @Override
            public int choose(int x, int y, int z) {
                return z;
            }

            @Override
            public double choose(double x, double y, double z) {
                return z;
            }
        };

        public static final Axis[] VALUES;
        private static final Map<String, Axis> BY_NAME;

        static {
            VALUES = values();
            BY_NAME = Arrays.stream(VALUES).collect(Collectors.toMap(Axis::getName, axis -> axis));
        }

        private final String name;

        Axis(String string2) {
            name = string2;
        }

        @Nullable
        public static Axis byName(String name) {
            return BY_NAME.get(name.toLowerCase(Locale.ROOT));
        }

        public String getName() {
            return name;
        }

        public boolean isVertical() {
            return this == Y;
        }

        public boolean isHorizontal() {
            return this == X || this == Z;
        }


        @Override
        public String toString() {
            return name;
        }

        @Override
        public boolean test(@Nullable Direction direction) {
            return direction != null && direction.getAxis() == this;
        }

        public Plane getPlane() {
            return switch (this) {
                case X, Z -> Plane.HORIZONTAL;
                case Y -> Plane.VERTICAL;
            };
        }

        public abstract int choose(int x, int y, int z);

        public abstract double choose(double x, double y, double z);
    }

    public enum AxisDirection {
        POSITIVE(1, "Towards positive"),
        NEGATIVE(-1, "Towards negative");

        private final int step;
        private final String name;

        AxisDirection(int n2, String string2) {
            step = n2;
            name = string2;
        }

        public int getStep() {
            return step;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }

        public AxisDirection opposite() {
            return this == POSITIVE ? NEGATIVE : POSITIVE;
        }
    }

    public enum Plane implements Iterable<Direction>, Predicate<Direction> {
        HORIZONTAL(new Direction[]{NORTH, EAST, SOUTH, WEST}, new Axis[]{Axis.X, Axis.Z}),
        VERTICAL(new Direction[]{UP, DOWN}, new Axis[]{Axis.Y});

        private final Direction[] faces;
        private final Axis[] axis;

        Plane(Direction[] directionArray, Axis[] axisArray) {
            faces = directionArray;
            axis = axisArray;
        }

        @Override
        public boolean test(@Nullable Direction direction) {
            return direction != null && direction.getAxis().getPlane() == this;
        }

        @Override
        @NotNull
        public Iterator<Direction> iterator() {
            return Iterators.forArray(faces);
        }

        public Stream<Direction> stream() {
            return Arrays.stream(faces);
        }
    }
}

