package me.dalekcraft.structureedit.schematic;

import me.dalekcraft.structureedit.exception.MissingKeyException;
import me.dalekcraft.structureedit.util.PropertyUtils;
import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.io.NamedTag;
import net.querz.nbt.io.SNBTUtil;
import net.querz.nbt.tag.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;

public class NbtStructure implements VersionedSchematic, MultiPaletteSchematic {

    public static final String EXTENSION = "nbt";
    private final NamedTag schematic;
    private final CompoundTag root;
    private NbtPalette palette;

    public NbtStructure(@NotNull NamedTag schematic) throws MissingKeyException {
        this.schematic = schematic;
        if (!(schematic.getTag() instanceof CompoundTag compoundTag)) {
            throw new MissingKeyException("Root tag is not an instance of " + CompoundTag.class.getSimpleName());
        }
        root = compoundTag;
        validate();
        if (hasPaletteList()) {
            palette = getPaletteListEntry(0);
        } else {
            palette = new NbtPalette(root.getListTag("palette").asCompoundTagList());
        }
    }

    @Override
    public void validate() throws MissingKeyException {
        String currentKey = "DataVersion";
        Class<?> expectedType = IntTag.class;
        if (!root.containsKey("DataVersion")) {
            throw new MissingKeyException(expectedType.getSimpleName() + " " + currentKey + " is missing");
        }
        if (!expectedType.isInstance(root.get("DataVersion"))) {
            throw new MissingKeyException("Key " + currentKey + " is not an instance of " + expectedType.getSimpleName());
        }

        currentKey = "size";
        expectedType = ListTag.class;
        if (!root.containsKey("size")) {
            throw new MissingKeyException(expectedType.getSimpleName() + " " + currentKey + " is missing");
        }
        if (!expectedType.isInstance(root.get("size"))) {
            throw new MissingKeyException("Key " + currentKey + " is not an instance of " + expectedType.getSimpleName());
        }
        ListTag<? extends Tag<?>> size = root.getListTag("size");
        expectedType = IntTag.class;
        if (!expectedType.equals(size.getTypeClass())) {
            throw new MissingKeyException(ListTag.class.getSimpleName() + " " + currentKey + " does not have type " + expectedType.getSimpleName());
        }
        if (size.size() != 3) {
            throw new MissingKeyException(expectedType.getSimpleName() + " " + currentKey + " has size " + size.size() + "; should be 3");
        }

        if (root.containsKey("palette")) {
            currentKey = "palette";
            expectedType = ListTag.class;
            if (!expectedType.isInstance(root.get("palette"))) {
                throw new MissingKeyException("Key " + currentKey + " is not an instance of " + expectedType.getSimpleName());
            }
            ListTag<? extends Tag<?>> rawPalette = root.getListTag("palette");
            expectedType = CompoundTag.class;
            if (!expectedType.equals(rawPalette.getTypeClass())) {
                throw new MissingKeyException(ListTag.class.getSimpleName() + " " + currentKey + " does not have type " + expectedType.getSimpleName());
            }
            ListTag<CompoundTag> palette = rawPalette.asCompoundTagList();
            for (int i = 0; i < palette.size(); i++) {
                CompoundTag state = palette.get(i);
                currentKey = "palette[" + i + "].Name";
                expectedType = StringTag.class;
                if (!state.containsKey("Name")) {
                    throw new MissingKeyException(expectedType.getSimpleName() + " " + currentKey + " is missing");
                }
                if (!expectedType.isInstance(state.get("Name"))) {
                    throw new MissingKeyException("Key " + currentKey + " is not an instance of " + expectedType.getSimpleName());
                }

                currentKey = "palette[" + i + "].Properties";
                expectedType = CompoundTag.class;
                if (state.containsKey("Properties") && !expectedType.isInstance(state.get("Properties"))) {
                    throw new MissingKeyException("Key " + currentKey + " is not an instance of " + expectedType.getSimpleName());
                }
            }
        } else if (root.containsKey("palettes")) {
            currentKey = "palettes";
            expectedType = ListTag.class;
            if (!expectedType.isInstance(root.get("palettes"))) {
                throw new MissingKeyException("Key " + currentKey + " is not an instance of " + expectedType.getSimpleName());
            }
            ListTag<? extends Tag<?>> rawPaletteList = root.getListTag("palettes");
            if (!expectedType.equals(rawPaletteList.getTypeClass())) {
                throw new MissingKeyException(ListTag.class.getSimpleName() + " " + currentKey + " does not have type " + expectedType.getSimpleName());
            }
            ListTag<ListTag<?>> paletteList = rawPaletteList.asListTagList();
            for (int i = 0; i < paletteList.size(); i++) {
                currentKey = "palettes[" + i + "]";
                ListTag<? extends Tag<?>> rawPalette = paletteList.get(i);
                expectedType = CompoundTag.class;
                if (!expectedType.equals(rawPalette.getTypeClass())) {
                    throw new MissingKeyException(ListTag.class.getSimpleName() + " " + currentKey + " does not have type " + expectedType.getSimpleName());
                }
                ListTag<CompoundTag> palette = rawPalette.asCompoundTagList();
                for (int j = 0; j < palette.size(); j++) {
                    CompoundTag state = palette.get(j);
                    currentKey = "palettes[" + i + "][" + j + "].Name";
                    expectedType = StringTag.class;
                    if (!state.containsKey("Name")) {
                        throw new MissingKeyException(expectedType.getSimpleName() + " " + currentKey + " is missing");
                    }
                    if (!expectedType.isInstance(state.get("Name"))) {
                        throw new MissingKeyException("Key " + currentKey + " is not an instance of " + expectedType.getSimpleName());
                    }

                    currentKey = "palette[" + j + "].Properties";
                    expectedType = CompoundTag.class;
                    if (state.containsKey("Properties") && !expectedType.isInstance(state.get("Properties"))) {
                        throw new MissingKeyException("Key " + currentKey + " is not an instance of " + expectedType.getSimpleName());
                    }
                }
            }
        } else {
            throw new MissingKeyException(expectedType.getSimpleName() + " " + currentKey + " is missing");
        }

        currentKey = "blocks";
        expectedType = ListTag.class;
        if (!root.containsKey("blocks")) {
            throw new MissingKeyException(expectedType.getSimpleName() + " " + currentKey + " is missing");
        }
        if (!expectedType.isInstance(root.get("blocks"))) {
            throw new MissingKeyException("Key " + currentKey + " is not an instance of " + expectedType.getSimpleName());
        }
        ListTag<? extends Tag<?>> rawBlockList = root.getListTag("blocks");
        expectedType = CompoundTag.class;
        if (!expectedType.equals(rawBlockList.getTypeClass())) {
            throw new MissingKeyException(ListTag.class.getSimpleName() + " " + currentKey + " does not have type " + expectedType.getSimpleName());
        }
        ListTag<CompoundTag> blockList = rawBlockList.asCompoundTagList();
        for (int i = 0; i < blockList.size(); i++) {
            CompoundTag block = blockList.get(i);
            currentKey = "blocks[" + i + "].state";
            expectedType = IntTag.class;
            if (!block.containsKey("state")) {
                throw new MissingKeyException(expectedType.getSimpleName() + " " + currentKey + " is missing");
            }
            if (!expectedType.isInstance(block.get("state"))) {
                throw new MissingKeyException("Key " + currentKey + " is not an instance of " + expectedType.getSimpleName());
            }

            currentKey = "blocks[" + i + "].pos";
            expectedType = ListTag.class;
            if (!block.containsKey("pos")) {
                throw new MissingKeyException(expectedType.getSimpleName() + " " + currentKey + " is missing");
            }
            if (!expectedType.isInstance(block.get("pos"))) {
                throw new MissingKeyException("Key " + currentKey + " is not an instance of " + expectedType.getSimpleName());
            }
            ListTag<? extends Tag<?>> position = block.getListTag("pos");
            expectedType = IntTag.class;
            if (!expectedType.equals(position.getTypeClass())) {
                throw new MissingKeyException(ListTag.class.getSimpleName() + " " + currentKey + " does not have type " + expectedType.getSimpleName());
            }
            if (position.size() != 3) {
                throw new MissingKeyException(expectedType.getSimpleName() + " " + currentKey + " has size " + position.size() + "; should be 3");
            }

            currentKey = "blocks[" + i + "].nbt";
            expectedType = CompoundTag.class;
            if (block.containsKey("nbt") && !expectedType.isInstance(block.get("nbt"))) {
                throw new MissingKeyException("Key " + currentKey + " is not an instance of " + expectedType.getSimpleName());
            }
        }

        if (root.containsKey("entities")) {
            currentKey = "entities";
            expectedType = ListTag.class;
            if (!expectedType.isInstance(root.get("entities"))) {
                throw new MissingKeyException("Key " + currentKey + " is not an instance of " + expectedType.getSimpleName());
            }
            ListTag<? extends Tag<?>> entityList = root.getListTag("entities");
            for (int i = 0; i < entityList.size(); i++) {
                expectedType = CompoundTag.class;
                if (!expectedType.isInstance(entityList.get(i))) {
                    throw new MissingKeyException(ListTag.class.getSimpleName() + " " + currentKey + " does not have type " + expectedType.getSimpleName());
                }
                CompoundTag entity = entityList.asCompoundTagList().get(i);
                currentKey = "entities[" + i + "].pos";
                expectedType = ListTag.class;
                if (!entity.containsKey("pos")) {
                    throw new MissingKeyException(expectedType.getSimpleName() + " " + currentKey + " is missing");
                }
                if (!expectedType.isInstance(entity.get("pos"))) {
                    throw new MissingKeyException("Key " + currentKey + " is not an instance of " + expectedType.getSimpleName());
                }
                ListTag<? extends Tag<?>> position = entity.getListTag("pos");
                expectedType = DoubleTag.class;
                if (!expectedType.equals(position.getTypeClass())) {
                    throw new MissingKeyException(ListTag.class.getSimpleName() + " " + currentKey + " does not have type " + expectedType.getSimpleName());
                }
                if (position.size() != 3) {
                    throw new MissingKeyException(expectedType.getSimpleName() + " " + currentKey + " has size " + position.size() + "; should be 3");
                }

                currentKey = "entities[" + i + "].blockPos";
                expectedType = ListTag.class;
                if (!entity.containsKey("pos")) {
                    throw new MissingKeyException(expectedType.getSimpleName() + " " + currentKey + " is missing");
                }
                if (!expectedType.isInstance(entity.get("blockPos"))) {
                    throw new MissingKeyException("Key " + currentKey + " is not an instance of " + expectedType.getSimpleName());
                }
                ListTag<? extends Tag<?>> blockPosition = entity.getListTag("blockPos");
                expectedType = IntTag.class;
                if (!expectedType.equals(blockPosition.getTypeClass())) {
                    throw new MissingKeyException(ListTag.class.getSimpleName() + " " + currentKey + " does not have type " + expectedType.getSimpleName());
                }
                if (blockPosition.size() != 3) {
                    throw new MissingKeyException(expectedType.getSimpleName() + " " + currentKey + " has size " + blockPosition.size() + "; should be 3");
                }

                currentKey = "entities[" + i + "].nbt";
                expectedType = CompoundTag.class;
                if (entity.containsKey("nbt") && !expectedType.isInstance(entity.get("nbt"))) {
                    throw new MissingKeyException("Key " + currentKey + " is not an instance of " + expectedType.getSimpleName());
                }
            }
        }
    }

    @Override
    public void saveTo(File file) throws IOException {
        NBTUtil.write(schematic, file);
    }

    @Contract(pure = true)
    @Override
    public NamedTag getData() {
        return schematic;
    }

    @Contract(pure = true)
    @Override
    @NotNull
    public String getFormat() {
        return EXTENSION;
    }

    @Override
    public int getDataVersion() {
        return root.getInt("DataVersion");
    }

    @Override
    public int @NotNull [] getSize() {
        ListTag<IntTag> size = root.getListTag("size").asIntTagList();
        return new int[]{size.get(0).asInt(), size.get(1).asInt(), size.get(2).asInt()};
    }

    @Override
    public void setSize(int sizeX, int sizeY, int sizeZ) {
        ListTag<IntTag> size = root.getListTag("size").asIntTagList();
        size.set(0, new IntTag(sizeX));
        size.set(1, new IntTag(sizeY));
        size.set(2, new IntTag(sizeZ));
        root.put("size", size);
    }

    @Override
    @Nullable
    public NbtBlock getBlock(int x, int y, int z) {
        for (CompoundTag block : getBlockList()) {
            ListTag<IntTag> positionTag = block.getListTag("pos").asIntTagList();
            int[] position = new int[3];
            position[0] = positionTag.get(0).asInt();
            position[1] = positionTag.get(1).asInt();
            position[2] = positionTag.get(2).asInt();
            if (position[0] == x && position[1] == y && position[2] == z) {
                CompoundTag state = palette.getState(block.getInt("state"));
                return new NbtBlock(block, state);
            }
        }
        return null;
    }

    @Override
    public void setBlock(int x, int y, int z, Block block) {
        ListTag<CompoundTag> blocks = getBlockList();
        for (CompoundTag block1 : blocks) {
            int[] position = block1.getIntArray("pos");
            if (position[0] == x && position[1] == y && position[2] == z) {
                blocks.set(blocks.indexOf(block1), (CompoundTag) block);
                setBlockList(blocks);
            }
        }
    }

    public ListTag<CompoundTag> getBlockList() {
        return root.getListTag("blocks").asCompoundTagList();
    }

    public void setBlockList(ListTag<CompoundTag> blocks) {
        root.put("blocks", blocks);
    }

    @Override
    public NbtPalette getPalette() {
        return palette;
    }

    @Override
    public void setPalette(Palette palette) {
        this.palette = (NbtPalette) palette;
        root.put("palette", ((NbtPalette) palette).getData());
    }

    @Override
    public ListTag<ListTag<?>> getPaletteList() {
        return root.getListTag("palettes").asListTagList();
    }

    @Override
    public void setPaletteList(ListTag<ListTag<?>> palettes) {
        root.put("palettes", palettes);
    }

    @Override
    public NbtPalette getPaletteListEntry(int index) {
        return new NbtPalette(getPaletteList().get(index).asCompoundTagList());
    }

    @Override
    public void setPaletteListEntry(int index, Palette palette) {
        getPaletteList().set(index, ((NbtPalette) palette).getData());
    }

    @Override
    public boolean hasPaletteList() {
        return root.containsKey("palettes");
    }

    @Override
    public void setActivePalette(int index) {
        palette = getPaletteListEntry(index);
    }

    public static class NbtBlock implements PaletteBlock {

        private static final Logger LOGGER = LogManager.getLogger(NbtBlock.class);

        private final CompoundTag blockTag;
        private final CompoundTag stateTag;

        @Contract(pure = true)
        public NbtBlock(CompoundTag blockTag, CompoundTag stateTag) {
            this.blockTag = blockTag;
            this.stateTag = stateTag;
        }

        @Override
        public int[] getPosition() {
            ListTag<IntTag> position = blockTag.getListTag("pos").asIntTagList();
            return new int[]{position.get(0).asInt(), position.get(1).asInt(), position.get(2).asInt()};
        }

        @Override
        public void setPosition(int x, int y, int z) {
            ListTag<IntTag> position = new ListTag<>(IntTag.class);
            position.add(new IntTag(x));
            position.add(new IntTag(y));
            position.add(new IntTag(z));
            blockTag.put("pos", position);
        }

        @Override
        public String getId() {
            return stateTag.getString("Name");
        }

        @Override
        public void setId(@NotNull String id) {
            if (!id.contains(":")) {
                id = "minecraft:" + id;
            }
            stateTag.putString("Name", id);
        }

        @Override
        public CompoundTag getProperties() {
            return PropertyUtils.byteToString(stateTag.getCompoundTag("Properties"));
        }

        @Override
        public void setProperties(CompoundTag properties) {
            if (properties != null && !properties.entrySet().isEmpty()) {
                stateTag.put("Properties", PropertyUtils.byteToString(properties));
            } else {
                stateTag.remove("Properties");
            }
        }

        @Override
        public String getPropertiesAsString() {
            String propertiesString = "{}";
            CompoundTag properties = getProperties() == null ? new CompoundTag() : getProperties();
            try {
                propertiesString = SNBTUtil.toSNBT(properties);
            } catch (IOException e) {
                LOGGER.log(Level.ERROR, e.getMessage());
            }
            return propertiesString;
        }

        @Override
        public void setPropertiesAsString(String propertiesString) throws IOException {
            CompoundTag properties = new CompoundTag();
            try {
                properties = (CompoundTag) SNBTUtil.fromSNBT(propertiesString);
            } catch (StringIndexOutOfBoundsException ignored) {
            }
            setProperties(properties);
        }

        @Override
        public CompoundTag getNbt() {
            if (blockTag.containsKey("nbt")) {
                return blockTag.getCompoundTag("nbt");
            } else {
                return new CompoundTag();
            }
        }

        @Override
        public void setNbt(CompoundTag nbt) {
            if (nbt != null && !nbt.entrySet().isEmpty()) {
                blockTag.put("nbt", nbt);
            } else {
                blockTag.remove("nbt");
            }
        }

        @Override
        public String getSnbt() {
            String snbt = "{}";
            CompoundTag nbt = getNbt() == null ? new CompoundTag() : getNbt();
            try {
                snbt = SNBTUtil.toSNBT(nbt);
            } catch (IOException e) {
                LOGGER.log(Level.ERROR, e.getMessage());
            }
            return snbt;
        }

        @Override
        public void setSnbt(String snbt) throws IOException {
            CompoundTag nbt = new CompoundTag();
            try {
                nbt = (CompoundTag) SNBTUtil.fromSNBT(snbt);
            } catch (StringIndexOutOfBoundsException ignored) {
            }
            setNbt(nbt);
        }

        @Override
        public int getStateIndex() {
            return blockTag.getInt("state");
        }

        @Override
        public void setStateIndex(int state) {
            blockTag.putInt("state", state);
        }
    }

    public static class NbtPalette implements Palette {

        private final ListTag<CompoundTag> palette;

        @Contract(pure = true)
        public NbtPalette(ListTag<CompoundTag> palette) {
            this.palette = palette;
        }

        @Override
        public ListTag<CompoundTag> getData() {
            return palette;
        }

        @Override
        public int size() {
            return palette.size();
        }

        @Override
        public CompoundTag getState(int index) {
            return palette.get(index);
        }

        @Override
        public void setState(int index, Object state) {
            palette.set(index, (CompoundTag) state);
        }
    }
}
