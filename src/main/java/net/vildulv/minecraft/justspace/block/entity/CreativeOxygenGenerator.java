package net.vildulv.minecraft.justspace.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.vildulv.minecraft.justspace.BlockRegister;

public class CreativeOxygenGenerator extends AbstractOxygenGenerator {


    public CreativeOxygenGenerator(BlockPos pos, BlockState blockState) {
        super(BlockRegister.CREATIVE_OXYGEN_GENERATOR_BE.get(), pos, blockState);
        resetForNextCalculation();
    }


    public static void tick(Level level, BlockPos pos, BlockState state, PoweredOxygenGenerator blockEntity) {
        AbstractOxygenGenerator.tick(level, pos, state, blockEntity);
    }
}
