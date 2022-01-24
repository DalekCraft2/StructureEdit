package me.dalekcraft.structureedit.assets.blockstates;

import me.dalekcraft.structureedit.assets.ResourceLocation;
import me.dalekcraft.structureedit.assets.blockstates.multipart.MultiPart;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MissingBlockModelDefinition extends BlockModelDefinition {

    public static final ResourceLocation MISSING_MODEL_DEFINITION_LOCATION = new ResourceLocation("missing");
    private static final MissingBlockModelDefinition INSTANCE;

    static {
        Map<String, MultiVariant> variants = new HashMap<>();
        Variant variant = new Variant(MISSING_MODEL_DEFINITION_LOCATION, BlockModelRotation.X0_Y0, false, 1);
        List<Variant> variantList = List.of(variant);
        MultiVariant multiVariant = new MultiVariant(variantList);
        variants.put("", multiVariant);

        INSTANCE = new MissingBlockModelDefinition(variants, null);
    }

    private MissingBlockModelDefinition(Map<String, MultiVariant> variants, MultiPart multiPart) {
        super(variants, multiPart);
    }

    public static MissingBlockModelDefinition getInstance() {
        return INSTANCE;
    }
}
