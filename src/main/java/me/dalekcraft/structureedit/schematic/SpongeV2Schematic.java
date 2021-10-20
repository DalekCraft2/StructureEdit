package me.dalekcraft.structureedit.schematic;

import me.dalekcraft.structureedit.exception.MissingKeyException;
import net.querz.nbt.io.NamedTag;
import net.querz.nbt.tag.*;
import org.jetbrains.annotations.NotNull;

public class SpongeV2Schematic extends SpongeSchematic implements VersionedSchematic {

    public SpongeV2Schematic(@NotNull NamedTag schematic) throws MissingKeyException {
        super(schematic);
    }

    @Override
    public void validate() throws MissingKeyException {
        String currentKey = "Version";
        Class<?> expectedType = IntTag.class;
        if (!root.containsKey("Version")) {
            throw new MissingKeyException(expectedType.getSimpleName() + " " + currentKey + " is missing");
        }
        if (!expectedType.isInstance(root.get("Version"))) {
            throw new MissingKeyException("Key " + currentKey + " is not an instance of " + expectedType.getSimpleName());
        }
        int version = root.getInt("Version");
        if (version != 2) {
            throw new MissingKeyException("Key " + currentKey + " is " + version + "; should be 2");
        }

        currentKey = "DataVersion";
        if (!root.containsKey("DataVersion")) {
            throw new MissingKeyException(expectedType.getSimpleName() + " " + currentKey + " is missing");
        }
        if (!expectedType.isInstance(root.get("DataVersion"))) {
            throw new MissingKeyException("Key " + currentKey + " is not an instance of " + expectedType.getSimpleName());
        }

        if (root.containsKey("Metadata")) {
            currentKey = "Metadata";
            expectedType = CompoundTag.class;
            if (!expectedType.isInstance(root.get("Metadata"))) {
                throw new MissingKeyException("Key " + currentKey + " is not an instance of " + expectedType.getSimpleName());
            }
        }

        currentKey = "Width";
        expectedType = ShortTag.class;
        if (!root.containsKey("Width") || !expectedType.isInstance(root.get("Width"))) {
            throw new MissingKeyException("Key " + currentKey + " is either missing or not an instance of " + expectedType.getSimpleName());
        }
        currentKey = "Length";
        if (!root.containsKey("Length") || !expectedType.isInstance(root.get("Length"))) {
            throw new MissingKeyException("Key " + currentKey + " is either missing or not an instance of " + expectedType.getSimpleName());
        }
        currentKey = "Height";
        if (!root.containsKey("Height") || !expectedType.isInstance(root.get("Height"))) {
            throw new MissingKeyException("Key " + currentKey + " is either missing or not an instance of " + expectedType.getSimpleName());
        }

        if (!root.containsKey("Offset")) {
            currentKey = "Offset";
            expectedType = IntArrayTag.class;
            if (!expectedType.isInstance(root.get("Offset"))) {
                throw new MissingKeyException("Key " + currentKey + " is not an instance of " + expectedType.getSimpleName());
            }
            int[] position = root.getIntArray("Offset");
            if (position.length != 3) {
                throw new MissingKeyException(expectedType.getSimpleName() + " " + currentKey + " has size " + position.length + "; should be 3");
            }
        }

        currentKey = "PaletteMax";
        expectedType = IntTag.class;
        if (!root.containsKey("PaletteMax")) {
            throw new MissingKeyException(expectedType.getSimpleName() + " " + currentKey + " is missing");
        }
        if (!expectedType.isInstance(root.get("PaletteMax"))) {
            throw new MissingKeyException("Key " + currentKey + " is not an instance of " + expectedType.getSimpleName());
        }
        int paletteMax = root.getInt("PaletteMax");

        currentKey = "Palette";
        expectedType = CompoundTag.class;
        if (!root.containsKey("Palette")) {
            throw new MissingKeyException(expectedType.getSimpleName() + " " + currentKey + " is missing");
        }
        if (!expectedType.isInstance(root.get("Palette"))) {
            throw new MissingKeyException("Key " + currentKey + " is not an instance of " + expectedType.getSimpleName());
        }
        CompoundTag palette = root.getCompoundTag("Palette");
        if (palette.size() > paletteMax) {
            throw new MissingKeyException("Size of palette (" + palette.size() + ") is greater than PaletteMax (" + paletteMax + ")");
        }
        for (String key : palette.keySet()) {
            currentKey = "Palette." + key;
            expectedType = IntTag.class;
            if (!expectedType.isInstance(palette.get(key))) {
                throw new MissingKeyException("Key " + currentKey + " is not an instance of " + expectedType.getSimpleName());
            }
        }

        currentKey = "BlockData";
        expectedType = ByteArrayTag.class;
        if (!root.containsKey("BlockData")) {
            throw new MissingKeyException(expectedType.getSimpleName() + " " + currentKey + " is missing");
        }
        if (!expectedType.isInstance(root.get("BlockData"))) {
            throw new MissingKeyException("Key " + currentKey + " is not an instance of " + expectedType.getSimpleName());
        }
        byte[] blockList = root.getByteArray("BlockData");
        for (int i = 0; i < blockList.length; i++) {
            currentKey = "BlockData[" + i + "]";
            byte state = blockList[i];
            if (!palette.containsValue(new IntTag(state))) {
                throw new MissingKeyException("Key " + currentKey + " has invalid palette index " + state);
            }
        }

        if (root.containsKey("BlockEntities")) {
            currentKey = "BlockEntities";
            expectedType = ListTag.class;
            if (!expectedType.isInstance(root.get("BlockEntities"))) {
                throw new MissingKeyException("Key " + currentKey + " is not an instance of " + expectedType.getSimpleName());
            }
            ListTag<? extends Tag<?>> tileEntityList = root.getListTag("BlockEntities");
            for (int i = 0; i < tileEntityList.size(); i++) {
                expectedType = CompoundTag.class;
                if (!expectedType.isInstance(tileEntityList.get(i))) {
                    throw new MissingKeyException(ListTag.class.getSimpleName() + " " + currentKey + " does not have type " + expectedType.getSimpleName());
                }
                CompoundTag tileEntity = tileEntityList.asCompoundTagList().get(i);
                currentKey = "BlockEntities[" + i + "].Pos";
                expectedType = IntArrayTag.class;
                if (!tileEntity.containsKey("Pos")) {
                    throw new MissingKeyException(expectedType.getSimpleName() + " " + currentKey + " is missing");
                }
                if (!expectedType.isInstance(tileEntity.get("Pos"))) {
                    throw new MissingKeyException("Key " + currentKey + " is not an instance of " + expectedType.getSimpleName());
                }
                int[] position = tileEntity.getIntArray("Pos");
                if (position.length != 3) {
                    throw new MissingKeyException(expectedType.getSimpleName() + " " + currentKey + " has size " + position.length + "; should be 3");
                }

                currentKey = "BlockEntities[" + i + "].Id";
                expectedType = StringTag.class;
                if (!tileEntity.containsKey("Id")) {
                    throw new MissingKeyException(expectedType.getSimpleName() + " " + currentKey + " is missing");
                }
                if (!expectedType.isInstance(tileEntity.get("Id"))) {
                    throw new MissingKeyException("Key " + currentKey + " is not an instance of " + expectedType.getSimpleName());
                }
            }
        }

        if (root.containsKey("Entities")) {
            currentKey = "Entities";
            expectedType = ListTag.class;
            if (!expectedType.isInstance(root.get("Entities"))) {
                throw new MissingKeyException("Key " + currentKey + " is not an instance of " + expectedType.getSimpleName());
            }
            ListTag<? extends Tag<?>> entityList = root.getListTag("Entities");
            for (int i = 0; i < entityList.size(); i++) {
                expectedType = CompoundTag.class;
                if (!expectedType.isInstance(entityList.get(i))) {
                    throw new MissingKeyException(ListTag.class.getSimpleName() + " " + currentKey + " does not have type " + expectedType.getSimpleName());
                }
                CompoundTag entity = entityList.asCompoundTagList().get(i);
                currentKey = "Entities[" + i + "].Pos";
                expectedType = ListTag.class;
                if (!entity.containsKey("Pos")) {
                    throw new MissingKeyException(expectedType.getSimpleName() + " " + currentKey + " is missing");
                }
                if (!expectedType.isInstance(entity.get("Pos"))) {
                    throw new MissingKeyException("Key " + currentKey + " is not an instance of " + expectedType.getSimpleName());
                }
                ListTag<? extends Tag<?>> rawPosition = entity.getListTag("Pos");
                expectedType = DoubleTag.class;
                if (!expectedType.equals(rawPosition.getTypeClass())) {
                    throw new MissingKeyException(ListTag.class.getSimpleName() + " " + currentKey + " does not have type " + expectedType.getSimpleName());
                }
                ListTag<DoubleTag> position = rawPosition.asDoubleTagList();
                if (position.size() != 3) {
                    throw new MissingKeyException(expectedType.getSimpleName() + " " + currentKey + " has size " + position.size() + "; should be 3");
                }

                currentKey = "Entities[" + i + "].Id";
                expectedType = StringTag.class;
                if (!entity.containsKey("Id")) {
                    throw new MissingKeyException(expectedType.getSimpleName() + " " + currentKey + " is missing");
                }
                if (!expectedType.isInstance(entity.get("Id"))) {
                    throw new MissingKeyException("Key " + currentKey + " is not an instance of " + expectedType.getSimpleName());
                }
            }
        }

        if (root.containsKey("BiomePalette") && root.containsKey("BiomePaletteMax") && root.containsKey("BiomeData")) {
            currentKey = "BiomePaletteMax";
            expectedType = IntTag.class;
            if (!root.containsKey("BiomePaletteMax")) {
                throw new MissingKeyException(expectedType.getSimpleName() + " " + currentKey + " is missing");
            }
            if (!expectedType.isInstance(root.get("BiomePaletteMax"))) {
                throw new MissingKeyException("Key " + currentKey + " is not an instance of " + expectedType.getSimpleName());
            }
            int biomePaletteMax = root.getInt("BiomePaletteMax");

            currentKey = "BiomePalette";
            expectedType = CompoundTag.class;
            if (!expectedType.isInstance(root.get("BiomePalette"))) {
                throw new MissingKeyException("Key " + currentKey + " is not an instance of " + expectedType.getSimpleName());
            }
            CompoundTag biomePalette = root.getCompoundTag("BiomePalette");
            if (biomePalette.size() > biomePaletteMax) {
                throw new MissingKeyException("Size of " + currentKey + " (" + biomePalette.size() + ") is greater than BiomePaletteMax (" + biomePaletteMax + ")");
            }
            for (String key : biomePalette.keySet()) {
                currentKey = "BiomePalette." + key;
                expectedType = IntTag.class;
                if (!expectedType.isInstance(biomePalette.get(key))) {
                    throw new MissingKeyException("Key " + currentKey + " is not an instance of " + expectedType.getSimpleName());
                }
            }

            currentKey = "BiomeData";
            expectedType = ByteArrayTag.class;
            if (!root.containsKey("BiomeData")) {
                throw new MissingKeyException(expectedType.getSimpleName() + " " + currentKey + " is missing");
            }
            if (!expectedType.isInstance(root.get("BiomeData"))) {
                throw new MissingKeyException("Key " + currentKey + " is not an instance of " + expectedType.getSimpleName());
            }
            byte[] biomeList = root.getByteArray("BiomeData");
            for (int i = 0; i < biomeList.length; i++) {
                currentKey = "BiomeData[" + i + "]";
                byte biomeState = biomeList[i];
                if (!palette.containsValue(new IntTag(biomeState))) {
                    throw new MissingKeyException("Key " + currentKey + " has invalid palette index " + biomeState);
                }
            }
        }
    }

    @Override
    public int getDataVersion() {
        return root.getInt("DataVersion");
    }

    @Override
    public ListTag<CompoundTag> getBlockEntityList() {
        return root.getListTag("BlockEntities").asCompoundTagList();
    }

    @Override
    public void setBlockEntityList(ListTag<CompoundTag> blockEntities) {
        root.put("BlockEntities", blockEntities);
    }
}
