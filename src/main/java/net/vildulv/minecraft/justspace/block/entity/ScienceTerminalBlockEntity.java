package net.vildulv.minecraft.justspace.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.vildulv.minecraft.justspace.BlockRegister;

public class ScienceTerminalBlockEntity extends TerminalBlockEntity {


    public ScienceTerminalBlockEntity(BlockPos pos, BlockState blockState) {
        super(BlockRegister.SCIENCE_TERMINAL_BE.get(), pos, blockState);
    }


}
