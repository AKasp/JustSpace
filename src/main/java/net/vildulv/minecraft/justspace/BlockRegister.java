package net.vildulv.minecraft.justspace;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.vildulv.minecraft.justspace.block.AirVentBlock;
import net.vildulv.minecraft.justspace.block.entity.OxygenGenerator;

import java.util.Set;
import java.util.function.Supplier;

public class BlockRegister {

    // Create a Deferred Register to hold Blocks which will all be registered under the "justspace" namespace
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(justspace.MODID);

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_REGISTER = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, justspace.MODID);

    public static final DeferredBlock<Block> AIR_VENT = BLOCKS.registerBlock("air_vent", AirVentBlock::new, BlockBehaviour.Properties.of()
            .mapColor(MapColor.STONE)
            .randomTicks());


    public static final Supplier<BlockEntityType<OxygenGenerator>> OXYGEN_GENERATOR_BE = BLOCK_ENTITY_REGISTER.register("oxygen_generator",
            () -> (new BlockEntityType<>(OxygenGenerator::new, Set.of(AIR_VENT.get()))));


}
