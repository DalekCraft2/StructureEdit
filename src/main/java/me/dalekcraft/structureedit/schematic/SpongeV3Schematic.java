package me.dalekcraft.structureedit.schematic;

import me.dalekcraft.structureedit.exception.ValidationException;
import net.querz.nbt.io.NamedTag;
import net.querz.nbt.tag.*;
import org.jetbrains.annotations.NotNull;

public class SpongeV3Schematic extends SpongeV2Schematic {

    public SpongeV3Schematic(@NotNull NamedTag schematic) throws ValidationException {
        super(schematic);
    }

    @Override
    public void validate() throws ValidationException {
        if (!schematic.getName().equals("")) {
            throw new ValidationException("Root tag name is not empty");
        }

        String currentKey = "Version";
        Class<?> expectedType = IntTag.class;
        if (!root.containsKey("Version")) {
            throw new ValidationException(expectedType.getSimpleName() + " " + currentKey + " is missing");
        }
        if (!expectedType.isInstance(root.get("Version"))) {
            throw new ValidationException("Key " + currentKey + " is not an instance of " + expectedType.getSimpleName());
        }
        int version = root.getInt("Version");
        if (version != 3) {
            throw new ValidationException("Key " + currentKey + " is " + version + "; should be 3");
        }

        currentKey = "DataVersion";
        if (!root.containsKey("DataVersion")) {
            throw new ValidationException(expectedType.getSimpleName() + " " + currentKey + " is missing");
        }
        if (!expectedType.isInstance(root.get("DataVersion"))) {
            throw new ValidationException("Key " + currentKey + " is not an instance of " + expectedType.getSimpleName());
        }

        if (root.containsKey("Metadata")) {
            currentKey = "Metadata";
            expectedType = CompoundTag.class;
            if (!expectedType.isInstance(root.get("Metadata"))) {
                throw new ValidationException("Key " + currentKey + " is not an instance of " + expectedType.getSimpleName());
            }
        }

        currentKey = "Width";
        expectedType = ShortTag.class;
        if (!root.containsKey("Width")) {
            throw new ValidationException(expectedType.getSimpleName() + " " + currentKey + " is missing");
        }
        if (!expectedType.isInstance(root.get("Width"))) {
            throw new ValidationException("Key " + currentKey + " is not an instance of " + expectedType.getSimpleName());
        }
        currentKey = "Height";
        if (!root.containsKey("Height")) {
            throw new ValidationException(expectedType.getSimpleName() + " " + currentKey + " is missing");
        }
        if (!expectedType.isInstance(root.get("Height"))) {
            throw new ValidationException("Key " + currentKey + " is not an instance of " + expectedType.getSimpleName());
        }
        currentKey = "Length";
        if (!root.containsKey("Length")) {
            throw new ValidationException(expectedType.getSimpleName() + " " + currentKey + " is missing");
        }
        if (!expectedType.isInstance(root.get("Length"))) {
            throw new ValidationException("Key " + currentKey + " is not an instance of " + expectedType.getSimpleName());
        }

        if (!root.containsKey("Offset")) {
            currentKey = "Offset";
            expectedType = IntArrayTag.class;
            if (!expectedType.isInstance(root.get("Offset"))) {
                throw new ValidationException("Key " + currentKey + " is not an instance of " + expectedType.getSimpleName());
            }
            int[] position = root.getIntArray("Offset");
            if (position.length != 3) {
                throw new ValidationException(expectedType.getSimpleName() + " " + currentKey + " has size " + position.length + "; should be 3");
            }
        }

        if (root.containsKey("Blocks")) {
            currentKey = "Blocks";
            expectedType = CompoundTag.class;
            if (!expectedType.isInstance(root.get("Blocks"))) {
                throw new ValidationException("Key " + currentKey + " is not an instance of " + expectedType.getSimpleName());
            }
            CompoundTag blockContainer = root.getCompoundTag("Blocks");

            currentKey = "Palette";
            if (!blockContainer.containsKey("Palette")) {
                throw new ValidationException(expectedType.getSimpleName() + " " + currentKey + " is missing");
            }
            if (!expectedType.isInstance(blockContainer.get("Palette"))) {
                throw new ValidationException("Key " + currentKey + " is not an instance of " + expectedType.getSimpleName());
            }
            CompoundTag palette = blockContainer.getCompoundTag("Palette");
            for (String key : palette.keySet()) {
                currentKey = "Palette." + key;
                expectedType = IntTag.class;
                if (!expectedType.isInstance(palette.get(key))) {
                    throw new ValidationException("Key " + currentKey + " is not an instance of " + expectedType.getSimpleName());
                }
            }

            currentKey = "Data";
            expectedType = ByteArrayTag.class;
            if (!blockContainer.containsKey("Data")) {
                throw new ValidationException(expectedType.getSimpleName() + " " + currentKey + " is missing");
            }
            if (!expectedType.isInstance(blockContainer.get("Data"))) {
                throw new ValidationException("Key " + currentKey + " is not an instance of " + expectedType.getSimpleName());
            }
            byte[] blockList = blockContainer.getByteArray("Data");
            for (int i = 0; i < blockList.length; i++) {
                currentKey = "Data[" + i + "]";
                byte state = blockList[i];
                if (!palette.containsValue(new IntTag(state))) {
                    throw new ValidationException("Key " + currentKey + " has invalid palette index " + state);
                }
            }

            if (blockContainer.containsKey("BlockEntities")) {
                currentKey = "BlockEntities";
                expectedType = ListTag.class;
                if (!expectedType.isInstance(blockContainer.get("BlockEntities"))) {
                    throw new ValidationException("Key " + currentKey + " is not an instance of " + expectedType.getSimpleName());
                }
                ListTag<? extends Tag<?>> blockEntityList = blockContainer.getListTag("BlockEntities");
                for (int i = 0; i < blockEntityList.size(); i++) {
                    currentKey = "BlockEntities[" + i + "]";
                    expectedType = CompoundTag.class;
                    if (!expectedType.isInstance(blockEntityList.get(i))) {
                        throw new ValidationException("Key " + currentKey + " is not an instance of " + expectedType.getSimpleName());
                    }
                    CompoundTag blockEntity = blockEntityList.asCompoundTagList().get(i);
                    currentKey = "BlockEntities[" + i + "].Pos";
                    expectedType = IntArrayTag.class;
                    if (!blockEntity.containsKey("Pos")) {
                        throw new ValidationException(expectedType.getSimpleName() + " " + currentKey + " is missing");
                    }
                    if (!expectedType.isInstance(blockEntity.get("Pos"))) {
                        throw new ValidationException("Key " + currentKey + " is not an instance of " + expectedType.getSimpleName());
                    }
                    int[] position = blockEntity.getIntArray("Pos");
                    if (position.length != 3) {
                        throw new ValidationException(expectedType.getSimpleName() + " " + currentKey + " has size " + position.length + "; should be 3");
                    }

                    currentKey = "BlockEntities[" + i + "].Id";
                    expectedType = StringTag.class;
                    if (!blockEntity.containsKey("Id")) {
                        throw new ValidationException(expectedType.getSimpleName() + " " + currentKey + " is missing");
                    }
                    if (!expectedType.isInstance(blockEntity.get("Id"))) {
                        throw new ValidationException("Key " + currentKey + " is not an instance of " + expectedType.getSimpleName());
                    }
                }
            }
        }

        if (root.containsKey("Biomes")) {
            currentKey = "Biomes";
            expectedType = CompoundTag.class;
            if (!expectedType.isInstance(root.get("Biomes"))) {
                throw new ValidationException("Key " + currentKey + " is not an instance of " + expectedType.getSimpleName());
            }
            CompoundTag biomeContainer = root.getCompoundTag("Biomes");

            currentKey = "Palette";
            if (!expectedType.isInstance(biomeContainer.get("Palette"))) {
                throw new ValidationException("Key " + currentKey + " is not an instance of " + expectedType.getSimpleName());
            }
            CompoundTag biomePalette = biomeContainer.getCompoundTag("Palette");
            for (String key : biomePalette.keySet()) {
                currentKey = "Palette." + key;
                expectedType = IntTag.class;
                if (!expectedType.isInstance(biomePalette.get(key))) {
                    throw new ValidationException("Key " + currentKey + " is not an instance of " + expectedType.getSimpleName());
                }
            }

            currentKey = "Data";
            expectedType = ByteArrayTag.class;
            if (!biomeContainer.containsKey("Data")) {
                throw new ValidationException(expectedType.getSimpleName() + " " + currentKey + " is missing");
            }
            if (!expectedType.isInstance(biomeContainer.get("Data"))) {
                throw new ValidationException("Key " + currentKey + " is not an instance of " + expectedType.getSimpleName());
            }
            byte[] biomeList = biomeContainer.getByteArray("Data");
            for (int i = 0; i < biomeList.length; i++) {
                currentKey = "Data[" + i + "]";
                byte biomeState = biomeList[i];
                if (!biomePalette.containsValue(new IntTag(biomeState))) {
                    throw new ValidationException("Key " + currentKey + " has invalid palette index " + biomeState);
                }
            }
        }

        if (root.containsKey("Entities")) {
            currentKey = "Entities";
            expectedType = ListTag.class;
            if (!expectedType.isInstance(root.get("Entities"))) {
                throw new ValidationException("Key " + currentKey + " is not an instance of " + expectedType.getSimpleName());
            }
            ListTag<? extends Tag<?>> entityList = root.getListTag("Entities");
            for (int i = 0; i < entityList.size(); i++) {
                currentKey = "Entities[" + i + "]";
                expectedType = CompoundTag.class;
                if (!expectedType.isInstance(entityList.get(i))) {
                    throw new ValidationException("Key " + currentKey + " is not an instance of " + expectedType.getSimpleName());
                }
                CompoundTag entity = entityList.asCompoundTagList().get(i);
                currentKey = "Entities[" + i + "].Pos";
                expectedType = ListTag.class;
                if (!entity.containsKey("Pos")) {
                    throw new ValidationException(expectedType.getSimpleName() + " " + currentKey + " is missing");
                }
                if (!expectedType.isInstance(entity.get("Pos"))) {
                    throw new ValidationException("Key " + currentKey + " is not an instance of " + expectedType.getSimpleName());
                }
                ListTag<? extends Tag<?>> position = entity.getListTag("Pos");
                expectedType = DoubleTag.class;
                if (!expectedType.equals(position.getTypeClass())) {
                    throw new ValidationException(ListTag.class.getSimpleName() + " " + currentKey + " does not have type " + expectedType.getSimpleName());
                }
                if (position.size() != 3) {
                    throw new ValidationException(expectedType.getSimpleName() + " " + currentKey + " has size " + position.size() + "; should be 3");
                }

                currentKey = "Entities[" + i + "].Id";
                expectedType = StringTag.class;
                if (!entity.containsKey("Id")) {
                    throw new ValidationException(expectedType.getSimpleName() + " " + currentKey + " is missing");
                }
                if (!expectedType.isInstance(entity.get("Id"))) {
                    throw new ValidationException("Key " + currentKey + " is not an instance of " + expectedType.getSimpleName());
                }
            }
        }
    }

    @Override
    protected CompoundTag initializeRoot() throws ValidationException {
        if (schematic.getTag() instanceof CompoundTag compoundTag) {
            return compoundTag.getCompoundTag("Schematic");
        } else {
            throw new ValidationException("Root tag is not an instance of " + CompoundTag.class.getSimpleName());
        }
    }

    @Override
    @NotNull
    public SpongePalette getPalette() {
        return new SpongePalette(root.getCompoundTag("Blocks").getCompoundTag("Palette"));
    }

    @Override
    public void setPalette(Palette palette) {
        root.getCompoundTag("Blocks").put("Palette", ((SpongePalette) palette).getData());
    }

    @Override
    public ByteArrayTag getBlockList() {
        return root.getCompoundTag("Blocks").getByteArrayTag("Data");
    }

    @Override
    public void setBlockList(ByteArrayTag blocks) {
        root.getCompoundTag("Blocks").put("Data", blocks);
    }

    @Override
    public ListTag<CompoundTag> getBlockEntityList() {
        return root.getCompoundTag("Blocks").getListTag("BlockEntities").asCompoundTagList();
    }

    @Override
    public void setBlockEntityList(ListTag<CompoundTag> blockEntities) {
        root.getCompoundTag("Blocks").put("BlockEntities", blockEntities);
    }
}
