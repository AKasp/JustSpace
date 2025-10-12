package net.vildulv.minecraft.justspace.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.vildulv.minecraft.justspace.BlockRegister;
import net.vildulv.minecraft.justspace.block.util.loggenerator.CommandersLogGenerator;


public class CommandersTerminalBlockEntity extends TerminalBlockEntity {


    public CommandersTerminalBlockEntity(BlockPos pos, BlockState blockState) {
        super(BlockRegister.COMMANDERS_TERMINAL_BE.get(), pos, blockState);
    }

    @Override
    protected String getText() {
        return CommandersLogGenerator.generateSpaceStationLog();
    }
}
