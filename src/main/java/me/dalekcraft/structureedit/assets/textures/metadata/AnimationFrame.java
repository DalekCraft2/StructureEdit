package me.dalekcraft.structureedit.assets.textures.metadata;

public class AnimationFrame {
    public static final int UNKNOWN_FRAME_TIME = -1;
    private final int index;
    private final int time;

    public AnimationFrame(int index) {
        this(index, UNKNOWN_FRAME_TIME);
    }

    public AnimationFrame(int index, int time) {
        this.index = index;
        this.time = time;
    }

    public int getTime(int n) {
        return time == UNKNOWN_FRAME_TIME ? n : time;
    }

    public int getIndex() {
        return index;
    }
}
