package me.dalekcraft.structureedit.schematic.io;

import me.dalekcraft.structureedit.schematic.container.Block;
import me.dalekcraft.structureedit.schematic.container.BlockState;
import me.dalekcraft.structureedit.schematic.container.Entity;
import me.dalekcraft.structureedit.schematic.container.Schematic;
import net.querz.nbt.io.NBTOutputStream;
import net.querz.nbt.io.NamedTag;
import net.querz.nbt.tag.*;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class StructureWriter extends NbtSchematicWriter {

    private final NBTOutputStream outputStream;

    public StructureWriter(NBTOutputStream outputStream) {
        this.outputStream = Objects.requireNonNull(outputStream);
    }

    @Override
    public void write(Schematic schematic) throws IOException {
        CompoundTag root = new CompoundTag();

        root.putInt("DataVersion", schematic.getDataVersion());

        int[] size = schematic.getSize();
        ListTag<IntTag> sizeTag = new ListTag<>(IntTag.class);
        sizeTag.addInt(size[0]);
        sizeTag.addInt(size[1]);
        sizeTag.addInt(size[2]);
        root.put("size", sizeTag);

        List<? extends List<BlockState>> palettes = schematic.getBlockPalettes();
        if (palettes.size() > 1) {
            ListTag<ListTag<?>> palettesTag = new ListTag<>(ListTag.class);
            for (List<BlockState> palette : palettes) {
                ListTag<CompoundTag> paletteTag = new ListTag<>(CompoundTag.class);
                for (BlockState blockState : palette) {
                    CompoundTag blockStateObject = new CompoundTag();

                    blockStateObject.putString("Name", blockState.getId());

                    if (!blockState.getProperties().isEmpty()) {
                        CompoundTag propertiesTag = new CompoundTag();
                        blockState.getProperties().forEach(propertiesTag::putString);
                        blockStateObject.put("Properties", propertiesTag);
                    }

                    paletteTag.add(blockStateObject);
                }
                palettesTag.add(paletteTag);
            }

            root.put("palettes", palettesTag);
        } else {
            ListTag<CompoundTag> paletteTag = new ListTag<>(CompoundTag.class);
            for (BlockState blockState : palettes.get(0)) {
                CompoundTag blockStateObject = new CompoundTag();

                blockStateObject.putString("Name", blockState.getId());

                if (!blockState.getProperties().isEmpty()) {
                    CompoundTag propertiesTag = new CompoundTag();
                    blockState.getProperties().forEach(propertiesTag::putString);
                    blockStateObject.put("Properties", propertiesTag);
                }

                paletteTag.add(blockStateObject);
            }

            root.put("palette", paletteTag);
        }

        ListTag<CompoundTag> blocks = new ListTag<>(CompoundTag.class);
        for (int x = 0; x < size[0]; x++) {
            for (int y = 0; y < size[1]; y++) {
                for (int z = 0; z < size[2]; z++) {
                    Block block = schematic.getBlock(x, y, z);
                    if (block != null) {
                        CompoundTag blockObject = new CompoundTag();

                        blockObject.putInt("state", block.getBlockStateIndex());

                        ListTag<IntTag> position = new ListTag<>(IntTag.class);
                        position.addInt(x);
                        position.addInt(y);
                        position.addInt(z);
                        blockObject.put("pos", position);

                        if (block.getNbt().size() != 0) {
                            blockObject.put("nbt", block.getNbt());
                        }

                        blocks.add(blockObject);
                    }
                }
            }
        }
        root.put("blocks", blocks);

        List<Entity> entities = schematic.getEntities();
        if (entities != null) {
            ListTag<CompoundTag> entitiesTag = new ListTag<>(CompoundTag.class);
            for (Entity entity : entities) {
                CompoundTag entityObject = new CompoundTag();

                double[] position = entity.getPosition();
                ListTag<DoubleTag> positionTag = new ListTag<>(DoubleTag.class);
                positionTag.addDouble(position[0]);
                positionTag.addDouble(position[1]);
                positionTag.addDouble(position[2]);
                entityObject.put("pos", positionTag);

                int[] blockPosition = {(int) Math.floor(position[0]), (int) Math.floor(position[1]), (int) Math.floor(position[2])};
                ListTag<IntTag> blockPositionTag = new ListTag<>(IntTag.class);
                blockPositionTag.addInt(blockPosition[0]);
                blockPositionTag.addInt(blockPosition[1]);
                blockPositionTag.addInt(blockPosition[2]);
                entityObject.put("blockPos", blockPositionTag);

                String id = entity.getId();
                CompoundTag nbt = entity.getNbt();

                nbt.put("Pos", positionTag);
                nbt.putString("id", id);

                entityObject.put("nbt", nbt);

                entitiesTag.add(entityObject);
            }

            root.put("entities", entitiesTag);
        }

        NamedTag namedTag = new NamedTag("", root);

        outputStream.writeTag(namedTag, Tag.DEFAULT_MAX_DEPTH);
    }

    @Override
    public void close() throws IOException {
        outputStream.close();
    }
}
