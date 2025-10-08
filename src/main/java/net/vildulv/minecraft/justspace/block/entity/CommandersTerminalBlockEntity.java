package net.vildulv.minecraft.justspace.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.vildulv.minecraft.justspace.BlockRegister;


public class CommandersTerminalBlockEntity extends TerminalBlockEntity {


    public CommandersTerminalBlockEntity(BlockPos pos, BlockState blockState) {
        super(BlockRegister.COMMANDERS_TERMINAL_BE.get(), pos, blockState);
    }

}
