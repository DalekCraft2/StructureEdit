package me.dalekcraft.structureedit.schematic.io;

import com.sk89q.worldedit.internal.Constants;
import me.dalekcraft.structureedit.schematic.container.*;
import me.dalekcraft.structureedit.schematic.io.legacycompat.LegacyMapper;
import net.querz.nbt.io.NBTInputStream;
import net.querz.nbt.tag.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

// TODO Maybe add support for the various proprietary additions to this format, based on the Minecraft wiki's page for it.
public class McEditSchematicReader extends NbtSchematicReader {

    private static final Logger LOGGER = LogManager.getLogger();
    private final NBTInputStream inputStream;

    public McEditSchematicReader(NBTInputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public Schematic read() throws IOException, ValidationException {
        Schematic schematic = new Schematic();

        CompoundTag root = (CompoundTag) inputStream.readTag(Tag.DEFAULT_MAX_DEPTH).getTag();

        // Set the data version to 1.13.2's because that is what the schematic is converted to
        schematic.setDataVersion(Constants.DATA_VERSION_MC_1_13_2);

        // Create Sponge V3 metadata for WorldEdit if present
        if (/*root.containsKey("WEOriginX") || root.containsKey("WEOriginY") || root.containsKey("WEOriginZ") ||*/ //
                root.containsKey("WEOffsetX") || root.containsKey("WEOffsetY") || root.containsKey("WEOffsetZ")) {
            if (/*root.containsKey("WEOriginX") && root.containsKey("WEOriginY") && root.containsKey("WEOriginZ") &&*/ //
                    root.containsKey("WEOffsetX") && root.containsKey("WEOffsetY") && root.containsKey("WEOffsetZ")) {
                CompoundTag metadata = new CompoundTag();
                CompoundTag worldEditMeta = new CompoundTag();
                int offsetX = requireTag(root, "WEOffsetX", IntTag.class).asInt();
                int offsetY = requireTag(root, "WEOffsetY", IntTag.class).asInt();
                int offsetZ = requireTag(root, "WEOffsetZ", IntTag.class).asInt();
                int[] offset = {offsetX, offsetY, offsetZ};
                worldEditMeta.putIntArray("Offset", offset);
                metadata.put("WorldEdit", worldEditMeta);
                schematic.setMetadata(metadata);
            }
        }

        short sizeX = requireTag(root, "Width", ShortTag.class).asShort();
        short sizeY = requireTag(root, "Height", ShortTag.class).asShort();
        short sizeZ = requireTag(root, "Length", ShortTag.class).asShort();
        schematic.setSize(sizeX, sizeY, sizeZ);

        String materials = requireTag(root, "Materials", StringTag.class).getValue();
        if (!materials.equals("Classic") && !materials.equals("Pocket") && !materials.equals("Alpha")) {
            throw new ValidationException("Materials tag is not \"Classic\", \"Pocket\", or \"Alpha\"");
        }

        byte[] blockIds = requireTag(root, "Blocks", ByteArrayTag.class).getValue();
        byte[] addIds = new byte[0];
        byte[] blockData = requireTag(root, "Data", ByteArrayTag.class).getValue();
        short[] blocks = new short[blockIds.length]; // Have to later combine IDs

        // We support 4096 block IDs using the same method as vanilla Minecraft, where
        // the highest 4 bits are stored in a separate byte array.
        if (root.containsKey("AddBlocks")) {
            addIds = requireTag(root, "AddBlocks", ByteArrayTag.class).getValue();
        }

        // Combine the AddBlocks data with the first 8-bit block ID
        for (int index = 0; index < blockIds.length; index++) {
            if (index >> 1 >= addIds.length) { // No corresponding AddBlocks index
                blocks[index] = (short) (blockIds[index] & 0xFF);
            } else {
                if ((index & 1) == 0) {
                    blocks[index] = (short) (((addIds[index >> 1] & 0x0F) << 8) + (blockIds[index] & 0xFF));
                } else {
                    blocks[index] = (short) (((addIds[index >> 1] & 0xF0) << 4) + (blockIds[index] & 0xFF));
                }
            }
        }

        if (blocks.length != sizeX * sizeY * sizeZ) {
            throw new ValidationException("Blocks length is " + blocks.length + "; should be " + sizeX * sizeY * sizeZ);
        }

        for (int i = 0; i < blocks.length; i++) {
            // index = (y * length * width) + (z * width) + x
            int x = i % (sizeX * sizeZ) % sizeX;
            int y = i / (sizeX * sizeZ);
            int z = i % (sizeX * sizeZ) / sizeX;

            BlockState blockState = LegacyMapper.getInstance().getBlockFromLegacy(blocks[i], blockData[i]);
            if (blockState == null) {
                LOGGER.log(Level.DEBUG, "Could not find legacy block with ID of " + blocks[i] + ":" + blockData[i] + "; replacing with air");
                blockState = new BlockState("minecraft:air");
            }
            if (!schematic.getBlockPalette().contains(blockState)) {
                schematic.getBlockPalette().add(blockState);
            } else {
                blockState = schematic.getBlockState(schematic.getBlockPalette().indexOf(blockState));
            }

            schematic.setBlock(x, y, z, new Block(schematic.getBlockPalette().indexOf(blockState)));
        }

        ListTag<?> entities = optTag(root, "Entities", ListTag.class);
        if (entities != null) {
            for (int i = 0; i < entities.size(); i++) {
                CompoundTag entityTag = requireTag(entities, i, CompoundTag.class);

                ListTag<?> position = requireTag(entityTag, "Pos", ListTag.class);
                double x = requireTag(position, 0, DoubleTag.class).asDouble();
                double y = requireTag(position, 1, DoubleTag.class).asDouble();
                double z = requireTag(position, 2, DoubleTag.class).asDouble();

                String legacyId = requireTag(entityTag, "id", StringTag.class).getValue();
                String id = convertEntityId(legacyId);

                entityTag.remove("Pos");
                entityTag.remove("id");

                Entity entity = new Entity(id, entityTag);
                entity.setPosition(x, y, z);

                schematic.getEntities().add(entity);
            }
        }

        ListTag<?> tileEntities = optTag(root, "TileEntities", ListTag.class);
        if (tileEntities != null) {
            for (int i = 0; i < tileEntities.size(); i++) {
                CompoundTag tileEntity = requireTag(tileEntities, i, CompoundTag.class);

                int x = requireTag(tileEntity, "x", IntTag.class).asInt();
                int y = requireTag(tileEntity, "y", IntTag.class).asInt();
                int z = requireTag(tileEntity, "z", IntTag.class).asInt();

                String legacyId = requireTag(tileEntity, "id", StringTag.class).getValue();
                String id = convertBlockEntityId(legacyId);

                tileEntity.remove("x");
                tileEntity.remove("y");
                tileEntity.remove("z");
                tileEntity.remove("id");

                Block block = schematic.getBlock(x, y, z);
                if (block != null) {
                    block.setBlockEntity(new BlockEntity(id, tileEntity));
                }
            }
        }

        // Proprietary MCEdit-Unified biomes tag
        ByteArrayTag biomesTag = optTag(root, "Biomes", ByteArrayTag.class);
        if (biomesTag != null) {
            byte[] biomes = biomesTag.getValue();

            if (biomes.length != sizeX * sizeZ) {
                throw new ValidationException("Biomes length is " + biomes.length + "; should be " + sizeX * sizeZ);
            }

            for (int i = 0; i < biomes.length; i++) {
                // index = (z * width) + x
                int x = i % (sizeX * sizeZ) % sizeX;
                int z = i % (sizeX * sizeZ) / sizeX;

                for (int y = 0; y < sizeY; y++) {
                    BiomeState biomeState = LegacyMapper.getInstance().getBiomeFromLegacy(biomes[i]);
                    if (biomeState == null) {
                        LOGGER.log(Level.DEBUG, "Could not find legacy biome with ID of " + blocks[i] + ":" + blockData[i] + "; replacing with ocean");
                        biomeState = new BiomeState("minecraft:ocean");
                    }
                    if (!schematic.getBiomePalette().contains(biomeState)) {
                        schematic.getBiomePalette().add(biomeState);
                    } else {
                        biomeState = schematic.getBiomeState(schematic.getBiomePalette().indexOf(biomeState));
                    }

                    schematic.setBiome(x, y, z, new Biome(biomeState));
                }
            }
        }

        return schematic;
    }

    private String convertEntityId(String id) {
        return switch (id) {
            case "AreaEffectCloud" -> "area_effect_cloud";
            case "ArmorStand" -> "armor_stand";
            case "CaveSpider" -> "cave_spider";
            case "MinecartChest" -> "chest_minecart";
            case "DragonFireball" -> "dragon_fireball";
            case "ThrownEgg" -> "egg";
            case "EnderDragon" -> "ender_dragon";
            case "ThrownEnderpearl" -> "ender_pearl";
            case "FallingSand" -> "falling_block";
            case "FireworksRocketEntity" -> "fireworks_rocket";
            case "MinecartFurnace" -> "furnace_minecart";
            case "MinecartHopper" -> "hopper_minecart";
            case "EntityHorse" -> "horse";
            case "ItemFrame" -> "item_frame";
            case "LeashKnot" -> "leash_knot";
            case "LightningBolt" -> "lightning_bolt";
            case "LavaSlime" -> "magma_cube";
            case "MinecartRideable" -> "minecart";
            case "MushroomCow" -> "mooshroom";
            case "Ozelot" -> "ocelot";
            case "PolarBear" -> "polar_bear";
            case "ThrownPotion" -> "potion";
            case "ShulkerBullet" -> "shulker_bullet";
            case "SmallFireball" -> "small_fireball";
            case "MinecartSpawner" -> "spawner_minecart";
            case "SpectralArrow" -> "spectral_arrow";
            case "PrimedTnt" -> "tnt";
            case "MinecartTNT" -> "tnt_minecart";
            case "VillagerGolem" -> "villager_golem";
            case "WitherBoss" -> "wither";
            case "WitherSkull" -> "wither_skull";
            case "PigZombie" -> "zombie_pigman";
            case "XPOrb", "xp_orb" -> "experience_orb";
            case "ThrownExpBottle", "xp_bottle" -> "experience_bottle";
            case "EyeOfEnderSignal", "eye_of_ender_signal" -> "eye_of_ender";
            case "EnderCrystal", "ender_crystal" -> "end_crystal";
            case "fireworks_rocket" -> "firework_rocket";
            case "MinecartCommandBlock", "commandblock_minecart" -> "command_block_minecart";
            case "snowman" -> "snow_golem";
            case "villager_golem" -> "iron_golem";
            case "evocation_fangs" -> "evoker_fangs";
            case "evocation_illager" -> "evoker";
            case "vindication_illager" -> "vindicator";
            case "illusion_illager" -> "illusioner";
            default -> id;
        };
    }

    private String convertBlockEntityId(String id) {
        return switch (id) {
            case "Cauldron" -> "brewing_stand";
            case "Control" -> "command_block";
            case "DLDetector" -> "daylight_detector";
            case "Trap" -> "dispenser";
            case "EnchantTable" -> "enchanting_table";
            case "EndGateway" -> "end_gateway";
            case "AirPortal" -> "end_portal";
            case "EnderChest" -> "ender_chest";
            case "FlowerPot" -> "flower_pot";
            case "RecordPlayer" -> "jukebox";
            case "MobSpawner" -> "mob_spawner";
            case "Music", "noteblock" -> "note_block";
            case "Structure" -> "structure_block";
            case "Chest" -> "chest";
            case "Sign" -> "sign";
            case "Banner" -> "banner";
            case "Beacon" -> "beacon";
            case "Comparator" -> "comparator";
            case "Dropper" -> "dropper";
            case "Furnace" -> "furnace";
            case "Hopper" -> "hopper";
            case "Skull" -> "skull";
            default -> id;
        };
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
    }
}
