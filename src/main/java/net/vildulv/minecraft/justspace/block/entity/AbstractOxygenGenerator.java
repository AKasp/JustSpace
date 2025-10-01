package net.vildulv.minecraft.justspace.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.vildulv.minecraft.justspace.VentTracker;
import net.vildulv.minecraft.justspace.block.CreativeAirVentBlock;
import net.vildulv.minecraft.justspace.justspace;

import java.util.Set;

import static net.vildulv.minecraft.justspace.block.entity.OxygenGenerator.MAX_AREA_SIZE;


public class AbstractOxygenGenerator {


    public static boolean checkSealed(OxygenGenerator oxygenGenerator) {
        OxygenGenerator.OxygenGeneratorData data = oxygenGenerator.getOxygenGeneratorData();
        ServerLevel level = oxygenGenerator.getServerLevel();
        if (data.area.size() >= data.getMaxSize()) {
            // If the area is too large, see if we can expand it with neighbor generators.
            // Else reset the area and set sealed status to false.
            if (VentTracker.canExpand(level, oxygenGenerator)) {
                // If we can expand the area with neighbors, return the previous check status.
                return data.prevCheckStatus;
            }
            data.prevCheckStatus = false;
            resetForNextCalculation(data);
            return data.prevCheckStatus;
        }
        if (data.queue.empty()) {
            if (!data.area.isEmpty()) {
                // If queue is empty and area is not, we have checked all positions. Set to sealed status to true and start a new check;
                data.prevCheckStatus = true;
                VentTracker.addVent(level, oxygenGenerator);
                resetForNextCalculation(data);
                return data.prevCheckStatus;
            } else {
                // If both queue and area is empty, we are starting a new check. Add pos in fron of this block as first position to check.
                BlockPos startPos = oxygenGenerator.getStartPos(); //  this.getBlockPos().relative(this.getBlockState().getValue(FACING));
                data.queue.add(startPos);
                return data.prevCheckStatus;
            }
        } else {
            BlockPos nextToCheck = data.queue.pop();
            for (Direction direction : Direction.values()) {
                BlockPos offsetPos = nextToCheck.relative(direction);
                if (data.area.contains(offsetPos)) {
                    continue; // Already checked this position
                }
                BlockState state = level.getBlockState(offsetPos);
                if (state.isAir() && !data.area.contains(offsetPos)) {
                    data.area.add(offsetPos);
                    data.queue.push(offsetPos);
                }
            }
            return data.prevCheckStatus;
        }
    }

    protected static void resetForNextCalculation(OxygenGenerator.OxygenGeneratorData data) {
        data.area.clear();
        data.queue.clear();
        data.currentNeighbors.clear();
        data.currentMaxSize = MAX_AREA_SIZE;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, OxygenGenerator oxygenGenerator) {
        if (level.isClientSide) return;
        if (level.dimension() != justspace.SPACE_DIMENSION_KEY) return; // Only run on the JustSpace dimension
        long ms = System.currentTimeMillis();
        boolean sealed = false;
        for (int n = 0; n < 10; n++) {
            sealed = checkSealed(oxygenGenerator);
        }
        if (state.hasProperty(CreativeAirVentBlock.AIR_VENT_STATES_ENUM_PROPERTY)) {
            if (sealed) {
                level.setBlock(pos, state.setValue(CreativeAirVentBlock.AIR_VENT_STATES_ENUM_PROPERTY, CreativeAirVentBlock.AirVentStates.SEALED), 2);
            } else {
                level.setBlock(pos, state.setValue(CreativeAirVentBlock.AIR_VENT_STATES_ENUM_PROPERTY, CreativeAirVentBlock.AirVentStates.WORKING), 2);
            }
        }
        //    System.out.println("OxygenGenerator tick took " + (System.currentTimeMillis() - ms) + "ms, sealed: " + sealed +
        //           ", area size: " + blockEntity.area.size() +
        //        ", current max size: " + blockEntity.currentMaxSize +
        //       ", queue size: " + blockEntity.queue.size());
    }


    public static void addNeighbor(Set<OxygenGenerator> generators, OxygenGenerator.OxygenGeneratorData data, OxygenGenerator oxygenGenerator) {
        //  System.out.println("Adding neighbors to " + this + ": " + generators);
        data.currentNeighbors.addAll(generators);
        //  System.out.println("Current neighbors after adding: " + currentNeighbors);
        data.currentNeighbors.remove(oxygenGenerator);
    }


}
