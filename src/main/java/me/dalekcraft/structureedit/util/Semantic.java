package me.dalekcraft.structureedit.util;

public final class Semantic {

    private Semantic() {
        throw new UnsupportedOperationException();
    }

    public static final class Attribute {
        public static final int POSITION = 0;
        public static final int COLOR = 1;
        public static final int NORMAL = 2;
        public static final int TEX_COORD = 3;
    }

    public static final class Uniform {
        public static final int PROJECTION_MATRIX = 0;
        public static final int VIEW_MATRIX = 1;
        public static final int MODEL_MATRIX = 2;
        public static final int NORMAL_MATRIX = 3;
        public static final int TEXTURE_MATRIX = 4;
    }
}
