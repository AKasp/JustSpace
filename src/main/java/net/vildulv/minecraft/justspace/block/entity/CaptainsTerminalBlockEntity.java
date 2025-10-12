package net.vildulv.minecraft.justspace.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.vildulv.minecraft.justspace.BlockRegister;
import net.vildulv.minecraft.justspace.block.util.loggenerator.CommandersLogGenerator;

public class CaptainsTerminalBlockEntity extends TerminalBlockEntity {


    public CaptainsTerminalBlockEntity(BlockPos pos, BlockState blockState) {
        super(BlockRegister.CAPTAINS_TERMINAL_BE.get(), pos, blockState);
    }


    @Override
    protected String getText() {
        return CommandersLogGenerator.generateSpaceShipLog();
    }
}
