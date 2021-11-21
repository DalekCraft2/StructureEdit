package me.dalekcraft.structureedit.schematic;

import me.dalekcraft.structureedit.exception.ValidationException;
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

public class McEditSchematic implements Schematic {

    public static final String EXTENSION = "schematic";
    private final NamedTag schematic;
    private final CompoundTag root;


    public McEditSchematic(@NotNull NamedTag schematic) throws ValidationException, UnsupportedOperationException {
        if (true) {
            throw new UnsupportedOperationException("MCEdit schematics are not yet supported!");
        }
        this.schematic = schematic;
        if (!schematic.getName().equals("Schematic")) {
            throw new ValidationException("Root tag name is not \"Schematic\"");
        }
        if (!(schematic.getTag() instanceof CompoundTag compoundTag)) {
            throw new ValidationException("Root tag is not an instance of " + CompoundTag.class.getSimpleName());
        }
        root = compoundTag;
        validate();
    }

    @Override
    public void validate() throws ValidationException {
        String currentKey = "Width";
        Class<?> expectedType = ShortTag.class;
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

        currentKey = "Materials";
        expectedType = StringTag.class;
        if (!root.containsKey("Materials")) {
            throw new ValidationException(expectedType.getSimpleName() + " " + currentKey + " is missing");
        }
        if (!expectedType.isInstance(root.get("Materials"))) {
            throw new ValidationException("Key " + currentKey + " is not an instance of " + expectedType.getSimpleName());
        }
        String materials = root.getString("Materials");
        if (!materials.equals("Classic") && !materials.equals("Pocket") && !materials.equals("Alpha")) {
            throw new ValidationException(expectedType.getSimpleName() + " " + currentKey + " is not \"Classic\", \"Pocket\", or \"Alpha\"");
        }

        currentKey = "Blocks";
        expectedType = ByteArrayTag.class;
        if (!root.containsKey("Blocks")) {
            throw new ValidationException(expectedType.getSimpleName() + " " + currentKey + " is missing");
        }
        if (!expectedType.isInstance(root.get("Blocks"))) {
            throw new ValidationException("Key " + currentKey + " is not an instance of " + expectedType.getSimpleName());
        }

        if (root.containsKey("AddBlocks")) {
            currentKey = "AddBlocks";
            if (!expectedType.isInstance(root.get("AddBlocks"))) {
                throw new ValidationException("Key " + currentKey + " is not an instance of " + expectedType.getSimpleName());
            }
        }

        currentKey = "Data";
        if (!root.containsKey("Data")) {
            throw new ValidationException(expectedType.getSimpleName() + " " + currentKey + " is missing");
        }
        if (!expectedType.isInstance(root.get("Data"))) {
            throw new ValidationException("Key " + currentKey + " is not an instance of " + expectedType.getSimpleName());
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
            }
        }

        if (root.containsKey("TileEntities")) {
            currentKey = "TileEntities";
            expectedType = ListTag.class;
            if (!expectedType.isInstance(root.get("TileEntities"))) {
                throw new ValidationException("Key " + currentKey + " is not an instance of " + expectedType.getSimpleName());
            }
            ListTag<? extends Tag<?>> tileEntityList = root.getListTag("TileEntities");
            for (int i = 0; i < tileEntityList.size(); i++) {
                currentKey = "TileEntities[" + i + "]";
                expectedType = CompoundTag.class;
                if (!expectedType.isInstance(tileEntityList.get(i))) {
                    throw new ValidationException("Key " + currentKey + " is not an instance of " + expectedType.getSimpleName());
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
    public Object getData() {
        return schematic;
    }

    @Contract(pure = true)
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
        CompoundTag tag = (CompoundTag) schematic.getTag();
        tag.putShort("Width", (short) sizeX);
        tag.putShort("Height", (short) sizeY);
        tag.putShort("Length", (short) sizeZ);
    }

    @Override
    public Block getBlock(int x, int y, int z) {
        int[] size = getSize();
        // index = x + (y * length * width) + (z * width)
        int index = x + y * size[2] * size[0] + z * size[0];
        int blockId = getBlockList().getValue()[index];
        CompoundTag blockEntityTag = null;
        for (CompoundTag blockEntity : getBlockEntityList()) {
            if (blockEntity.containsKey("x") && blockEntity.containsKey("y") && blockEntity.containsKey("z")) {
                int[] position = {blockEntity.getInt("x"), blockEntity.getInt("y"), blockEntity.getInt("z")};
                if (position[0] == x && position[1] == y && position[2] == z) {
                    blockEntityTag = blockEntity;
                    break;
                }
            }
        }
        return new McEditBlock(blockEntityTag, new int[]{x, y, z});
    }

    @Override
    public void setBlock(int x, int y, int z, Block block) {

    }

    public ByteArrayTag getBlockList() {
        return root.getByteArrayTag("Blocks");
    }

    public void setBlockList(ByteArrayTag blocks) {
        root.put("Blocks", blocks);
    }

    public ListTag<CompoundTag> getBlockEntityList() {
        return root.getListTag("TileEntities").asCompoundTagList();
    }

    public void setBlockEntityList(ListTag<CompoundTag> blockEntities) {
        root.put("TileEntities", blockEntities);
    }

    public class McEditBlock implements Block {

        private static final Logger LOGGER = LogManager.getLogger();

        private final CompoundTag blockEntityTag;
        private final int[] position;

        public McEditBlock(CompoundTag blockEntityTag, int[] position) {
            this.blockEntityTag = blockEntityTag;
            this.position = position;
        }

        @Override
        public int[] getPosition() {
            return position;
        }

        @Override
        public void setPosition(int x, int y, int z) {

        }

        @Override
        public String getId() {
            return "minecraft:missing";
        }

        @Override
        public void setId(String id) {

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

        }

        @Override
        public String getPropertiesAsString() {
            return "[]";
        }

        @Override
        public void setPropertiesAsString(String propertiesString) throws IOException {

        }

        @Override
        public CompoundTag getNbt() {
            return blockEntityTag;
        }

        @Override
        public void setNbt(CompoundTag nbt) {

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

        }
    }
}
