package me.dalekcraft.structureedit.assets.models;

import com.mojang.datafixers.util.Either;
import me.dalekcraft.structureedit.assets.ResourceLocation;
import me.dalekcraft.structureedit.assets.textures.MissingTexture;
import me.dalekcraft.structureedit.util.Direction;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class MissingBlockModel extends BlockModel {

    public static final ResourceLocation MISSING_MODEL_LOCATION = new ResourceLocation("missing");
    private static final List<BlockElement> ELEMENTS;
    private static final Map<String, Either<Material, String>> TEXTURE_MAP;
    private static final MissingBlockModel INSTANCE;

    static {
        ELEMENTS = new ArrayList<>();
        Map<Direction, BlockElementFace> faces = new HashMap<>();
        faces.put(Direction.DOWN, new BlockElementFace(Direction.DOWN, BlockElementFace.NO_TINT, "#missingno", new BlockFaceUv(new float[]{0, 0, 16, 16}, 0)));
        faces.put(Direction.UP, new BlockElementFace(Direction.UP, BlockElementFace.NO_TINT, "#missingno", new BlockFaceUv(new float[]{0, 0, 16, 16}, 0)));
        faces.put(Direction.NORTH, new BlockElementFace(Direction.NORTH, BlockElementFace.NO_TINT, "#missingno", new BlockFaceUv(new float[]{0, 0, 16, 16}, 0)));
        faces.put(Direction.SOUTH, new BlockElementFace(Direction.SOUTH, BlockElementFace.NO_TINT, "#missingno", new BlockFaceUv(new float[]{0, 0, 16, 16}, 0)));
        faces.put(Direction.WEST, new BlockElementFace(Direction.WEST, BlockElementFace.NO_TINT, "#missingno", new BlockFaceUv(new float[]{0, 0, 16, 16}, 0)));
        faces.put(Direction.EAST, new BlockElementFace(Direction.EAST, BlockElementFace.NO_TINT, "#missingno", new BlockFaceUv(new float[]{0, 0, 16, 16}, 0)));
        BlockElement element = new BlockElement(new Vector3f(0, 0, 0), new Vector3f(16, 16, 16), faces, null, true);
        ELEMENTS.add(element);

        TEXTURE_MAP = new HashMap<>();
        TEXTURE_MAP.put(MissingTexture.MISSING_TEXTURE_LOCATION.getPath(), Either.left(new Material(MissingTexture.MISSING_TEXTURE_LOCATION)));
        TEXTURE_MAP.put(PARTICLE_TEXTURE_REFERENCE, Either.left(new Material(MissingTexture.MISSING_TEXTURE_LOCATION)));

        INSTANCE = new MissingBlockModel(ELEMENTS, TEXTURE_MAP, true);
        INSTANCE.name = MISSING_MODEL_LOCATION.toString();
    }

    private MissingBlockModel(List<BlockElement> elements, Map<String, Either<Material, String>> textureMap, boolean hasAmbientOcclusion) {
        super(null, elements, textureMap, hasAmbientOcclusion);
    }

    public static MissingBlockModel getInstance() {
        return INSTANCE;
    }
}

