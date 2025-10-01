package net.vildulv.minecraft.justspace.block.entity;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.vildulv.minecraft.justspace.BlockRegister;
import net.vildulv.minecraft.justspace.VentTracker;
import net.vildulv.minecraft.justspace.block.CreativeAirVentBlock;

public class KineticOxygenGenerator extends KineticBlockEntity implements OxygenGenerator {

    OxygenGeneratorData data = new OxygenGeneratorData();

    public static final DirectionProperty FACING;


    public KineticOxygenGenerator(BlockPos pos, BlockState blockState) {
        super(BlockRegister.KINETIC_OXYGEN_GENERATOR_BE.get(), pos, blockState);
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


    public static void tick(Level level, BlockPos pos, BlockState state, PoweredOxygenGenerator blockEntity) {
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
    public void remove() {
        if (!this.level.isClientSide && this.level instanceof ServerLevel serverLevel) {
            VentTracker.unregisterVent(serverLevel, this);
        }
    }



    @Override
    public void tick() {
        super.tick();
        if (getSpeed() == 0) {
            if (getBlockState().hasProperty(CreativeAirVentBlock.AIR_VENT_STATES_ENUM_PROPERTY) && level instanceof ServerLevel serverLevel) {
                if (getBlockState().getValue(CreativeAirVentBlock.AIR_VENT_STATES_ENUM_PROPERTY) != CreativeAirVentBlock.AirVentStates.OFFLINE) {
                    level.setBlock(getBlockPos(), getBlockState().setValue(CreativeAirVentBlock.AIR_VENT_STATES_ENUM_PROPERTY, CreativeAirVentBlock.AirVentStates.OFFLINE), 2);
                    VentTracker.unregisterVent(serverLevel, this);
                    AbstractOxygenGenerator.resetForNextCalculation(data);
                }
            }
            return;
        }
        AbstractOxygenGenerator.tick(level, getBlockPos(), getBlockState(), this);
    }


}
