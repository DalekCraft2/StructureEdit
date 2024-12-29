package me.dalekcraft.structureedit.assets.textures.metadata;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;

import java.util.List;

public class AnimationMetadataSection {

    public static final AnimationMetadataSectionSerializer SERIALIZER = new AnimationMetadataSectionSerializer();
    public static final String SECTION_NAME = "animation";
    public static final int DEFAULT_FRAME_TIME = 1;
    public static final int UNKNOWN_SIZE = -1;
    public static final AnimationMetadataSection EMPTY = new AnimationMetadataSection(Lists.newArrayList(), UNKNOWN_SIZE, UNKNOWN_SIZE, DEFAULT_FRAME_TIME, false) {

        @Override
        public Pair<Integer, Integer> getFrameSize(int n, int n2) {
            return Pair.of(n, n2);
        }
    };
    public final List<AnimationFrame> frames;
    // private final List<AnimationFrame> frames;
    private final int frameWidth;
    private final int frameHeight;
    private final int defaultFrameTime;
    private final boolean interpolatedFrames;

    public AnimationMetadataSection(List<AnimationFrame> frames, int frameWidth, int frameHeight, int defaultFrameTime, boolean interpolatedFrames) {
        this.frames = frames;
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
        this.defaultFrameTime = defaultFrameTime;
        this.interpolatedFrames = interpolatedFrames;
    }

    private static boolean isDivisionInteger(int n, int n2) {
        return n / n2 * n2 == n;
    }

    public Pair<Integer, Integer> getFrameSize(int n, int n2) {
        Pair<Integer, Integer> pair = calculateFrameSize(n, n2);
        int n3 = pair.getFirst();
        int n4 = pair.getSecond();
        if (!isDivisionInteger(n, n3) || !isDivisionInteger(n2, n4)) {
            throw new IllegalArgumentException(String.format("Image size %s,%s is not multiply of frame size %s,%s", n, n2, n3, n4));
        }
        return pair;
    }

    private Pair<Integer, Integer> calculateFrameSize(int n, int n2) {
        if (frameWidth != UNKNOWN_SIZE) {
            if (frameHeight != UNKNOWN_SIZE) {
                return Pair.of(frameWidth, frameHeight);
            }
            return Pair.of(frameWidth, n2);
        }
        if (frameHeight != UNKNOWN_SIZE) {
            return Pair.of(n, frameHeight);
        }
        int n3 = Math.min(n, n2);
        return Pair.of(n3, n3);
    }

    public int getFrameHeight(int n) {
        return frameHeight == UNKNOWN_SIZE ? n : frameHeight;
    }

    public int getFrameWidth(int n) {
        return frameWidth == UNKNOWN_SIZE ? n : frameWidth;
    }

    public int getDefaultFrameTime() {
        return defaultFrameTime;
    }

    public boolean isInterpolatedFrames() {
        return interpolatedFrames;
    }

    public void forEachFrame(FrameOutput frameOutput) {
        for (AnimationFrame animationFrame : frames) {
            frameOutput.accept(animationFrame.getIndex(), animationFrame.getTime(defaultFrameTime));
        }
    }

    @FunctionalInterface
    public interface FrameOutput {
        void accept(int var1, int var2);
    }
}
