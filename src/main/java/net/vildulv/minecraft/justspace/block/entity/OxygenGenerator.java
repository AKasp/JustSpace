package net.vildulv.minecraft.justspace.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.vildulv.minecraft.justspace.BlockRegister;
import net.vildulv.minecraft.justspace.VentTracker;
import net.vildulv.minecraft.justspace.block.AirVentBlock;
import net.vildulv.minecraft.justspace.justspace;

import java.util.*;


public class OxygenGenerator extends BlockEntity {

    private final static int MAX_AREA_SIZE = 1000; // Maximum area size to prevent infinite loops
    private Set<Vec3i> area = new HashSet<>();
    private final Stack<BlockPos> queue = new Stack<>();
    private boolean prevCheckStatus = false;
    private final Set<OxygenGenerator> currentNeighbors = new HashSet<>();
    private int currentMaxSize = MAX_AREA_SIZE;


    public OxygenGenerator(BlockPos pos, BlockState blockState) {
        super(BlockRegister.OXYGEN_GENERATOR_BE.get(), pos, blockState);
        resetForNextCalculation();
    }

    public Set<Vec3i> getArea() {
        return area;
    }

    public void setArea(Set<Vec3i> area) {
        this.area = area;
    }

    public void setSealed() {
        prevCheckStatus = true;
    }

    public boolean checkSealed() {
        if(area.size() >= getMaxSize()) {
            // If the area is too large, see if we can expand it with neighbor generators.
            // Else reset the area and set sealed status to false.
            if (VentTracker.canExpand((ServerLevel) level, this)) {
                // If we can expand the area with neighbors, return the previous check status.
                return prevCheckStatus;
            }
            prevCheckStatus = false;
            resetForNextCalculation();
            return prevCheckStatus;
        }
        if (queue.empty()) {
            if (!area.isEmpty()) {
                // If queue is empty and area is not, we have checked all positions. Set to sealed status to true and start a new check;
                prevCheckStatus = true;
                VentTracker.addVent(level, this);
                resetForNextCalculation();
                return prevCheckStatus;
            } else {
                queue.add(this.getBlockPos());
                return prevCheckStatus;
            }
        } else {
            BlockPos nextToCheck = queue.pop();
            for(Direction direction : Direction.values()) {
                BlockPos offsetPos = nextToCheck.relative(direction);
                if (area.contains(offsetPos)) {
                    continue; // Already checked this position
                }
                BlockState state = level.getBlockState(offsetPos);
                if (state.isAir() && !area.contains(offsetPos)) {
                    area.add(offsetPos);
                    queue.push(offsetPos);
                }
            }
            return prevCheckStatus;
        }
    }

    private void resetForNextCalculation() {
        area.clear();
        queue.clear();
        currentNeighbors.clear();
        currentMaxSize = MAX_AREA_SIZE;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, OxygenGenerator blockEntity) {
        if(level.isClientSide) return;
        if(level.dimension() != justspace.SPACE_DIMENSION_KEY) return; // Only run on the JustSpace dimension
        long ms = System.currentTimeMillis();
        boolean sealed = false;
        for(int n = 0; n < 10; n++) {
            sealed = blockEntity.checkSealed();
        }
       if(state.hasProperty(AirVentBlock.AIR_VENT_STATES_ENUM_PROPERTY)) {
           if (sealed) {
                level.setBlock(pos, state.setValue(AirVentBlock.AIR_VENT_STATES_ENUM_PROPERTY, AirVentBlock.AirVentStates.SEALED), 2);
              } else {
                level.setBlock(pos, state.setValue(AirVentBlock.AIR_VENT_STATES_ENUM_PROPERTY, AirVentBlock.AirVentStates.WORKING), 2);
           }
       }
   //    System.out.println("OxygenGenerator tick took " + (System.currentTimeMillis() - ms) + "ms, sealed: " + sealed +
    //           ", area size: " + blockEntity.area.size() +
       //        ", current max size: " + blockEntity.currentMaxSize +
        //       ", queue size: " + blockEntity.queue.size());
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

    public void addNeighbor(Set<OxygenGenerator> generators) {
      //  System.out.println("Adding neighbors to " + this + ": " + generators);
        currentNeighbors.addAll(generators);
      //  System.out.println("Current neighbors after adding: " + currentNeighbors);
        currentNeighbors.remove(this);
    }

    public int getMaxSize() {
        return currentMaxSize;
    }

    public void increaseMaxSize(int additionalSize) {
        currentMaxSize += additionalSize;
    }

    public Set<OxygenGenerator> getNeighbours() {
        return currentNeighbors;
    }

    public boolean isSealed() {
        return prevCheckStatus;
    }

}
