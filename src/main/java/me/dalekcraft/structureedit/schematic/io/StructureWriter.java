package me.dalekcraft.structureedit.schematic.io;

import me.dalekcraft.structureedit.schematic.container.*;
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
                    CompoundTag blockStateTag = new CompoundTag();

                    blockStateTag.putString("Name", blockState.getId());

                    if (!blockState.getProperties().isEmpty()) {
                        CompoundTag propertiesTag = new CompoundTag();
                        blockState.getProperties().forEach(propertiesTag::putString);
                        blockStateTag.put("Properties", propertiesTag);
                    }

                    paletteTag.add(blockStateTag);
                }
                palettesTag.add(paletteTag);
            }

            root.put("palettes", palettesTag);
        } else {
            ListTag<CompoundTag> paletteTag = new ListTag<>(CompoundTag.class);
            for (BlockState blockState : palettes.get(0)) {
                CompoundTag blockStateTag = new CompoundTag();

                blockStateTag.putString("Name", blockState.getId());

                if (!blockState.getProperties().isEmpty()) {
                    CompoundTag propertiesTag = new CompoundTag();
                    blockState.getProperties().forEach(propertiesTag::putString);
                    blockStateTag.put("Properties", propertiesTag);
                }

                paletteTag.add(blockStateTag);
            }

            root.put("palette", paletteTag);
        }

        ListTag<CompoundTag> blocks = new ListTag<>(CompoundTag.class);
        for (int x = 0; x < size[0]; x++) {
            for (int y = 0; y < size[1]; y++) {
                for (int z = 0; z < size[2]; z++) {
                    Block block = schematic.getBlock(x, y, z);
                    if (block != null) {
                        CompoundTag blockTag = new CompoundTag();

                        blockTag.putInt("state", block.getBlockStateIndex());

                        ListTag<IntTag> position = new ListTag<>(IntTag.class);
                        position.addInt(x);
                        position.addInt(y);
                        position.addInt(z);
                        blockTag.put("pos", position);

                        BlockEntity blockEntity = block.getBlockEntity();
                        if (!blockEntity.isEmpty()) {
                            CompoundTag clone = blockEntity.getNbt().clone();
                            clone.putString("id", blockEntity.getId());
                            blockTag.put("nbt", clone);
                        }

                        blocks.add(blockTag);
                    }
                }
            }
        }
        root.put("blocks", blocks);

        List<Entity> entities = schematic.getEntities();
        if (entities != null) {
            ListTag<CompoundTag> entitiesTag = new ListTag<>(CompoundTag.class);
            for (Entity entity : entities) {
                CompoundTag entityTag = new CompoundTag();

                double[] position = entity.getPosition();
                ListTag<DoubleTag> positionTag = new ListTag<>(DoubleTag.class);
                positionTag.addDouble(position[0]);
                positionTag.addDouble(position[1]);
                positionTag.addDouble(position[2]);
                entityTag.put("pos", positionTag);

                int[] blockPosition = {(int) Math.floor(position[0]), (int) Math.floor(position[1]), (int) Math.floor(position[2])};
                ListTag<IntTag> blockPositionTag = new ListTag<>(IntTag.class);
                blockPositionTag.addInt(blockPosition[0]);
                blockPositionTag.addInt(blockPosition[1]);
                blockPositionTag.addInt(blockPosition[2]);
                entityTag.put("blockPos", blockPositionTag);

                String id = entity.getId();
                CompoundTag nbt = entity.getNbt().clone();

                nbt.put("Pos", positionTag);
                nbt.putString("id", id);

                entityTag.put("nbt", nbt);

                entitiesTag.add(entityTag);
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
