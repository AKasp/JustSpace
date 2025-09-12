package net.vildulv.minecraft.justspace.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.vildulv.minecraft.justspace.BlockRegister;
import net.vildulv.minecraft.justspace.VentTracker;
import net.vildulv.minecraft.justspace.block.CreativeAirVentBlock;
import net.vildulv.minecraft.justspace.block.util.CustomEnergyStorage;

import javax.annotation.Nullable;

public class PoweredOxygenGenerator extends AbstractOxygenGenerator {
    private static final int ENERGY_CAPACITY = 1000;
    private static final int ENERGY_INPUT = 256;
    private static final int ENERGY_CONSUMPTION = 100;


    private final CustomEnergyStorage energy = new CustomEnergyStorage(ENERGY_CAPACITY, ENERGY_INPUT, 0, 0);

    public PoweredOxygenGenerator(net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState blockState) {
        super(BlockRegister.POWERED_OXYGEN_GENERATOR_BE.get(), pos, blockState);
    }

    public @Nullable IEnergyStorage getEnergyStorageCapability(@Nullable Direction side) {
        return energy;
    }

    public boolean isPowered() {
        return !energy.isEmpty();
    }

    public static void tick(Level level, BlockPos pos, BlockState state, PoweredOxygenGenerator blockEntity) {
        if (level instanceof ServerLevel serverLevel) {
            if (blockEntity.isPowered()) {
                blockEntity.energy.removeEnergy(ENERGY_CONSUMPTION);
                AbstractOxygenGenerator.tick(level, pos, state, blockEntity);
            } else {

                // If not powered, set the vent to offline and return.
                if (state.hasProperty(CreativeAirVentBlock.AIR_VENT_STATES_ENUM_PROPERTY)) {

                    if (state.getValue(CreativeAirVentBlock.AIR_VENT_STATES_ENUM_PROPERTY) != CreativeAirVentBlock.AirVentStates.OFFLINE) {
                        level.setBlock(pos, state.setValue(CreativeAirVentBlock.AIR_VENT_STATES_ENUM_PROPERTY, CreativeAirVentBlock.AirVentStates.OFFLINE), 2);
                        VentTracker.unregisterVent(serverLevel, blockEntity);
                        blockEntity.resetForNextCalculation();
                    }
                }
            }
        }


    }


}
