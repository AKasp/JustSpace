package net.vildulv.minecraft.justspace.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.vildulv.minecraft.justspace.VentTracker;
import net.vildulv.minecraft.justspace.block.CreativeAirVentBlock;
import net.vildulv.minecraft.justspace.justspace;

import java.util.*;


public abstract class AbstractOxygenGenerator extends BlockEntity {
    public static final DirectionProperty FACING;

    private final static int MAX_AREA_SIZE = 1000; // Maximum area size to prevent infinite loops
    private Set<Vec3i> area = new HashSet<>();
    private final Stack<BlockPos> queue = new Stack<>();
    private boolean prevCheckStatus = false;
    private final Set<AbstractOxygenGenerator> currentNeighbors = new HashSet<>();
    private int currentMaxSize = MAX_AREA_SIZE;


    public AbstractOxygenGenerator(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
        resetForNextCalculation();
    }


    protected BlockState rotate(BlockState state, Rotation rot) {
        return (BlockState)state.setValue(FACING, rot.rotate((Direction)state.getValue(FACING)));
    }

    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation((Direction)state.getValue(FACING)));
    }

    static {
        FACING = BlockStateProperties.HORIZONTAL_FACING;
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
                // If both queue and area is empty, we are starting a new check. Add pos in fron of this block as first position to check.
                BlockPos startPos = this.getBlockPos().relative(this.getBlockState().getValue(FACING));
                queue.add(startPos);
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

    protected void resetForNextCalculation() {
        area.clear();
        queue.clear();
        currentNeighbors.clear();
        currentMaxSize = MAX_AREA_SIZE;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, AbstractOxygenGenerator blockEntity) {
        if(level.isClientSide) return;
        if(level.dimension() != justspace.SPACE_DIMENSION_KEY) return; // Only run on the JustSpace dimension
        long ms = System.currentTimeMillis();
        boolean sealed = false;
        for(int n = 0; n < 10; n++) {
            sealed = blockEntity.checkSealed();
        }
       if(state.hasProperty(CreativeAirVentBlock.AIR_VENT_STATES_ENUM_PROPERTY)) {
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

    public void addNeighbor(Set<AbstractOxygenGenerator> generators) {
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

    public Set<AbstractOxygenGenerator> getNeighbours() {
        return currentNeighbors;
    }

    public boolean isSealed() {
        return prevCheckStatus;
    }

}
