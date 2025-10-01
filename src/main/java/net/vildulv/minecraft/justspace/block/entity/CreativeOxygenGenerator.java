package net.vildulv.minecraft.justspace.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.vildulv.minecraft.justspace.BlockRegister;
import net.vildulv.minecraft.justspace.VentTracker;

public class CreativeOxygenGenerator extends BlockEntity implements OxygenGenerator {

    OxygenGeneratorData data = new OxygenGeneratorData();

    public static final DirectionProperty FACING;

    public CreativeOxygenGenerator(BlockPos pos, BlockState blockState) {
        super(BlockRegister.CREATIVE_OXYGEN_GENERATOR_BE.get(), pos, blockState);
        AbstractOxygenGenerator.resetForNextCalculation(data);
    }

    static {
        FACING = BlockStateProperties.HORIZONTAL_FACING;
    }


    protected BlockState rotate(BlockState state, Rotation rot) {
        return (BlockState)state.setValue(FACING, rot.rotate((Direction)state.getValue(FACING)));
    }

    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation((Direction)state.getValue(FACING)));
    }


    public static void tick(Level level, BlockPos pos, BlockState state, CreativeOxygenGenerator blockEntity) {
        AbstractOxygenGenerator.tick(level, pos, state, blockEntity);
    }

    @Override
    public OxygenGeneratorData getOxygenGeneratorData() {
        return data;
    }

    @Override
    public ServerLevel getServerLevel() {
        return super.level instanceof ServerLevel serverLevel ? serverLevel : null;
    }

    @Override
    public BlockPos getStartPos() {
        return  this.getBlockPos().relative(this.getBlockState().getValue(FACING));
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (!this.level.isClientSide && this.level instanceof ServerLevel serverLevel) {
            // VentTracker.registerVent(serverLevel, this);
        }
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        if (!this.level.isClientSide && this.level instanceof ServerLevel serverLevel) {
            VentTracker.unregisterVent(serverLevel, this);
        }
    }
    @Override
    public void setRemoved() {
        super.setRemoved();
        if (!this.level.isClientSide && this.level instanceof ServerLevel serverLevel) {
            VentTracker.unregisterVent(serverLevel, this);
        }
    }
}
