package net.vildulv.minecraft.justspace;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.vildulv.minecraft.justspace.block.*;
import net.vildulv.minecraft.justspace.block.entity.*;

import java.util.function.Supplier;

public class BlockRegister {

    // Create a Deferred Register to hold Blocks which will all be registered under the "justspace" namespace
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(justspace.MODID);

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_REGISTER = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, justspace.MODID);

    public static final DeferredBlock<Block> CREATIVE_AIR_VENT = BLOCKS.registerBlock("creative_air_vent", CreativeAirVentBlock::new, BlockBehaviour.Properties.of()
            .mapColor(MapColor.STONE)
            .destroyTime(1F));

    public static final DeferredBlock<Block> POWERED_AIR_VENT = BLOCKS.registerBlock("powered_air_vent", PoweredAirVentBlock::new, BlockBehaviour.Properties.of()
            .mapColor(MapColor.STONE)
            .destroyTime(1F));

    public static final DeferredBlock<Block> BROKEN_AIR_VENT = BLOCKS.registerBlock("broken_air_vent", BrokenVentBlock::new, BlockBehaviour.Properties.of()
            .mapColor(MapColor.STONE)
            .destroyTime(1F)
    );
    public static final DeferredBlock<Block> CONTROL_DEVICE = BLOCKS.registerBlock("control_device", ControlDeviceBlock::new, BlockBehaviour.Properties.of()
            .mapColor(MapColor.STONE)
            .destroyTime(1F)
    );
    public static final DeferredBlock<Block> TAPE_DEVICE = BLOCKS.registerBlock("tape_device", TapeDeviceBlock::new, BlockBehaviour.Properties.of()
            .mapColor(MapColor.STONE)
            .destroyTime(1F)
    );
    public static final DeferredBlock<Block> GAUGE_DEVICE = BLOCKS.registerBlock("gauge_device", GaugeDeviceBlock::new, BlockBehaviour.Properties.of()
            .mapColor(MapColor.STONE)
            .destroyTime(1F)
    );
    public static final DeferredBlock<Block> PING_DEVICE = BLOCKS.registerBlock("ping_device", PingDeviceBlock::new, BlockBehaviour.Properties.of()
            .mapColor(MapColor.STONE)
            .destroyTime(1F)
    );
    public static final DeferredBlock<Block> COMMANDERS_TERMINAL_DEVICE = BLOCKS.registerBlock("commanders_terminal_device", CommandersTerminalDeviceBlock::new, BlockBehaviour.Properties.of()
            .mapColor(MapColor.STONE)
            .destroyTime(1F)
    );
    public static final DeferredBlock<Block> CAPTAINS_TERMINAL_DEVICE = BLOCKS.registerBlock("captains_terminal_device", CaptainsTerminalDeviceBlock::new, BlockBehaviour.Properties.of()
            .mapColor(MapColor.STONE)
            .destroyTime(1F)
    );
    public static final DeferredBlock<Block> SCIENCE_TERMINAL_DEVICE = BLOCKS.registerBlock("science_terminal_device", ScienceTerminalDeviceBlock::new, BlockBehaviour.Properties.of()
            .mapColor(MapColor.STONE)
            .destroyTime(1F)
    );
    public static final DeferredBlock<Block> ALIEN_INSCRIPTION = BLOCKS.registerSimpleBlock("alien_inscription",
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .requiresCorrectToolForDrops()
                    .strength(50.0F, 1200.0F)
    );

    //Block Entities

    public static final Supplier<BlockEntityType<CreativeOxygenGenerator>> CREATIVE_OXYGEN_GENERATOR_BE = BLOCK_ENTITY_REGISTER.register("creative_oxygen_generator",
            () -> BlockEntityType.Builder.of(CreativeOxygenGenerator::new, CREATIVE_AIR_VENT.get()).build(null));

    public static final Supplier<BlockEntityType<PoweredOxygenGenerator>> POWERED_OXYGEN_GENERATOR_BE = BLOCK_ENTITY_REGISTER.register("powered_oxygen_generator",
            () -> BlockEntityType.Builder.of(PoweredOxygenGenerator::new, POWERED_AIR_VENT.get()).build(null));


    public static final Supplier<BlockEntityType<CommandersTerminalBlockEntity>> COMMANDERS_TERMINAL_BE = BLOCK_ENTITY_REGISTER.register("commanders_terminal_block_entity",
            () -> BlockEntityType.Builder.of(CommandersTerminalBlockEntity::new, COMMANDERS_TERMINAL_DEVICE.get()).build(null));
    public static final Supplier<BlockEntityType<CaptainsTerminalBlockEntity>> CAPTAINS_TERMINAL_BE = BLOCK_ENTITY_REGISTER.register("captains_terminal_block_entity",
            () -> BlockEntityType.Builder.of(CaptainsTerminalBlockEntity::new, CAPTAINS_TERMINAL_DEVICE.get()).build(null));
    public static final Supplier<BlockEntityType<ScienceTerminalBlockEntity>> SCIENCE_TERMINAL_BE = BLOCK_ENTITY_REGISTER.register("science_terminal_block_entity",
            () -> BlockEntityType.Builder.of(ScienceTerminalBlockEntity::new, SCIENCE_TERMINAL_DEVICE.get()).build(null));



    //TODO add if create is installed
    public static  DeferredBlock<Block> KINETIC_AIR_VENT;

    //TODO add if create is installed
    public static  Supplier<BlockEntityType<KineticOxygenGenerator>>  KINETIC_OXYGEN_GENERATOR_BE;


    static{
        if (ModList.get().isLoaded("create")) {
            KINETIC_AIR_VENT = BLOCKS.registerBlock("kinetic_air_vent", KineticAirVentBlock::new, BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .destroyTime(1F));
            KINETIC_OXYGEN_GENERATOR_BE = BLOCK_ENTITY_REGISTER.register("kinetic_oxygen_generator",
                    () -> BlockEntityType.Builder.of(KineticOxygenGenerator::new, KINETIC_AIR_VENT.get()).build(null));
        }
    };

    //Capabilities

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                POWERED_OXYGEN_GENERATOR_BE.get(), PoweredOxygenGenerator::getEnergyStorageCapability);
    }


}
