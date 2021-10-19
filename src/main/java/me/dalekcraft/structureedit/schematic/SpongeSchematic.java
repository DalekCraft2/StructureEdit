package me.dalekcraft.structureedit.schematic;

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

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

public class SpongeSchematic implements PaletteSchematic {

    public static final String EXTENSION = "schem";
    protected final NamedTag schematic;
    protected final CompoundTag root;

    public SpongeSchematic(@NotNull NamedTag schematic) throws IOException {
        this.schematic = schematic;
        root = initializeRoot();
    }

    @Contract("_ -> new")
    @NotNull
    public static SpongeSchematic getInstance(@NotNull NamedTag namedTag) throws IOException {
        if (namedTag.getTag() instanceof CompoundTag compoundTag) {
            if (compoundTag.containsKey("Schematic")) {
                int version = compoundTag.getCompoundTag("Schematic").getInt("Version");
                if (version == 3) {
                    return new SpongeV3Schematic(namedTag);
                }
                throw new IOException("Illegal schematic version " + version);
            } else {
                int version = compoundTag.getInt("Version");
                if (version == 1) {
                    return new SpongeSchematic(namedTag);
                } else if (version == 2) {
                    return new SpongeV2Schematic(namedTag);
                }
                throw new IOException("Illegal schematic version " + version);
            }
        } else {
            throw new IOException("Not a schematic file");
        }
    }

    protected CompoundTag initializeRoot() throws IOException {
        if (schematic.getTag() instanceof CompoundTag compoundTag) {
            return compoundTag;
        } else {
            throw new IOException("Not a schematic file");
        }
    }

    @Override
    public void saveTo(File file) throws IOException {
        NBTUtil.write(schematic, file);
    }

    @Override
    public NamedTag getData() {
        return schematic;
    }

    @Override
    public String getFormat() {
        return EXTENSION;
    }

    @Override
    public int @NotNull [] getSize() {
        return new int[]{root.getShort("Width"), root.getShort("Height"), root.getShort("Length")};
    }

    @Override
    public void setSize(int sizeX, int sizeY, int sizeZ) {
        root.putShort("Width", (short) sizeX);
        root.putShort("Height", (short) sizeY);
        root.putShort("Length", (short) sizeZ);
    }

    @Override
    @NotNull
    public SpongeBlock getBlock(int x, int y, int z) {
        int[] size = getSize();
        // index = x + (y * length * width) + (z * width)
        int index = x + (y * size[2] * size[0]) + (z * size[0]);
        int stateIndex = getBlockList().getValue()[index];
        String state = getPalette().getState(stateIndex);
        CompoundTag blockEntityTag = null;
        for (CompoundTag blockEntity : getBlockEntityList()) {
            if (blockEntity.containsKey("Pos")) { // Should always have the position key.
                IntArrayTag positionTag = blockEntity.getIntArrayTag("Pos");
                int[] position = positionTag.getValue();
                if (position[0] == x && position[1] == y && position[2] == z) {
                    blockEntityTag = blockEntity;
                    break;
                }
            }
        }
        return new SpongeBlock(state, blockEntityTag, new int[]{x, y, z});
    }

    @Contract(pure = true)
    @Override
    public void setBlock(int x, int y, int z, Block block) {
        // TODO This.
    }

    @Override
    @NotNull
    public SpongePalette getPalette() {
        return new SpongePalette(root.getCompoundTag("Palette"));
    }

    @Override
    public void setPalette(Palette palette) {
        root.put("Palette", ((SpongePalette) palette).getData());
    }

    public ByteArrayTag getBlockList() {
        return root.getByteArrayTag("BlockData");
    }

    public void setBlockList(ByteArrayTag blocks) {
        root.put("BlockData", blocks);
    }

    public ListTag<CompoundTag> getBlockEntityList() {
        return root.getListTag("TileEntities").asCompoundTagList();
    }

    public void setBlockEntityList(ListTag<CompoundTag> blockEntities) {
        root.put("TileEntities", blockEntities);
    }

    public int getVersion() {
        return root.getInt("Version");
    }

    public static class SpongePalette implements Palette {

        private final CompoundTag palette;

        @Contract(pure = true)
        public SpongePalette(CompoundTag palette) {
            this.palette = palette;
        }

        @Override
        public CompoundTag getData() {
            return palette;
        }

        @Override
        public int size() {
            return palette.size();
        }

        @Override
        public String getState(int index) {
            String state = null;
            for (String tagName : palette.keySet()) {
                if (palette.getInt(tagName) == index) {
                    state = tagName;
                }
            }
            return state;
        }

        @Override
        public void setState(int index, Object state) {
            for (Map.Entry<String, Tag<?>> tagEntry : palette.entrySet()) {
                if (((IntTag) tagEntry.getValue()).asInt() == index) {
                    String tagName = tagEntry.getKey();
                    palette.put((String) state, palette.remove(tagName));
                    break;
                }
            }
        }
    }

    public class SpongeBlock implements PaletteBlock {

        private static final Logger LOGGER = LogManager.getLogger(SpongeBlock.class);
        private CompoundTag blockEntityTag;
        private String state;
        private int[] position;

        @Contract(pure = true)
        public SpongeBlock(String state, CompoundTag blockEntityTag, int[] position) {
            this.state = state;
            this.blockEntityTag = blockEntityTag;
            this.position = position;
        }

        @Override
        public int[] getPosition() {
            return position;
        }

        @Override
        public void setPosition(int x, int y, int z) {
            // TODO Same as TardisBlock's to-do thing.
        }

        @Override
        public String getId() {
            int nameEndIndex = state.length();
            if (state.contains("[")) {
                nameEndIndex = state.indexOf('[');
            } else if (state.contains("{")) {
                nameEndIndex = state.indexOf('{');
            }
            return state.substring(0, nameEndIndex);
        }

        @Override
        public void setId(@NotNull String id) {
            if (!id.contains(":")) {
                id = "minecraft:" + id;
            }
            SpongePalette palette = getPalette();
            state = id + getPropertiesAsString();
            palette.setState(getStateIndex(), state);

            CompoundTag nbt = getNbt();
            if (nbt != null) {
                CompoundTag clone = nbt.clone();
                clone.remove("Id");
                clone.remove("Pos");
                if (clone.entrySet().isEmpty()) {
                    nbt.putString("Id", id);
                }
            }
        }

        @Override
        public CompoundTag getProperties() {
            String properties = getPropertiesAsString();
            String replaced = properties.replace('[', '{').replace(']', '}').replace('=', ':');
            CompoundTag tag = new CompoundTag();
            try {
                tag = (CompoundTag) SNBTUtil.fromSNBT(replaced);
            } catch (StringIndexOutOfBoundsException ignored) {
            } catch (IOException e) {
                LOGGER.log(Level.ERROR, e.getMessage());
            }
            return PropertyUtils.byteToString(tag);
        }

        @Override
        public void setProperties(CompoundTag properties) {
            String propertiesString = "";
            try {
                propertiesString = SNBTUtil.toSNBT(PropertyUtils.byteToString(properties));
            } catch (IOException e) {
                LOGGER.log(Level.ERROR, e.getMessage());
            }
            try {
                setPropertiesAsString(propertiesString);
            } catch (IOException e) {
                LOGGER.log(Level.ERROR, e.getMessage());
            }
        }

        @Override
        public String getPropertiesAsString() {
            return !state.substring(getId().length()).equals("") ? state.substring(getId().length()) : "[]";
        }

        @Override
        public void setPropertiesAsString(String propertiesString) throws IOException {
            SpongePalette palette = getPalette();
            String replaced = propertiesString.replace('[', '{').replace(']', '}').replace('=', ':').replace("\"", "");
            try {
                SNBTUtil.fromSNBT(replaced); // Check whether the SNBT is parsable
            } catch (StringIndexOutOfBoundsException ignored) {
            }
            state = getId() + propertiesString;
            palette.setState(getStateIndex(), state);
        }

        @Override
        public CompoundTag getNbt() {
            return blockEntityTag;
        }

        @Override
        public void setNbt(CompoundTag nbt) {
            ListTag<CompoundTag> blockEntityList = getBlockEntityList();
            for (CompoundTag block : blockEntityList) {
                if (block.containsKey("Pos")) {
                    IntArrayTag positionTag = block.getIntArrayTag("Pos");
                    int[] position = positionTag.getValue();
                    if (Arrays.equals(this.position, position)) {
                        if (nbt != null) {
                            CompoundTag clone = nbt.clone();
                            clone.remove("Id");
                            clone.remove("Pos");
                            if (clone.entrySet().isEmpty()) {
                                blockEntityTag = null;
                                blockEntityList.remove(blockEntityList.indexOf(block));
                            } else {
                                blockEntityTag = nbt;
                                blockEntityList.set(blockEntityList.indexOf(block), nbt);
                            }
                        } else {
                            blockEntityTag = null;
                            blockEntityList.remove(blockEntityList.indexOf(block));
                        }
                        return;
                    }
                }
            }
            if (nbt != null) {
                nbt.remove("Id");
                nbt.remove("Pos");
                if (!nbt.entrySet().isEmpty()) {
                    nbt.putString("Id", getId());
                    nbt.putIntArray("Pos", position);
                    blockEntityTag = nbt;
                    blockEntityList.add(nbt);
                }
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
            int[] size = getSize();
            int[] position = getPosition();
            // index = x + (y * length * width) + (z * width)
            int index = position[0] + (position[1] * size[2] * size[0]) + (position[2] * size[0]);
            return getBlockList().getValue()[index];
        }

        @Override
        public void setStateIndex(int state) {
            int[] size = getSize();
            int[] position = getPosition();
            // index = x + (y * length * width) + (z * width)
            int index = position[0] + (position[1] * size[2] * size[0]) + (position[2] * size[0]);
            getBlockList().getValue()[index] = (byte) state;
        }
    }
}
