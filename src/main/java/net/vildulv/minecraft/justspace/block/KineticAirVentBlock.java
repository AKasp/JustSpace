package net.vildulv.minecraft.justspace.block;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.vildulv.minecraft.justspace.BlockRegister;
import net.vildulv.minecraft.justspace.ItemRegister;
import net.vildulv.minecraft.justspace.block.entity.KineticOxygenGenerator;
import net.vildulv.minecraft.justspace.block.entity.PoweredOxygenGenerator;

import javax.annotation.Nullable;

import static net.vildulv.minecraft.justspace.block.CreativeAirVentBlock.AIR_VENT_STATES_ENUM_PROPERTY;
import static net.vildulv.minecraft.justspace.block.entity.PoweredOxygenGenerator.FACING;

public class KineticAirVentBlock extends KineticBlock implements EntityBlock, IBE<KineticOxygenGenerator> {
    private static final MapCodec<KineticAirVentBlock> CODEC = simpleCodec(KineticAirVentBlock::new);


    public MapCodec<KineticAirVentBlock> codec() {
        return CODEC;
    }


    public KineticAirVentBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(stateDefinition.any()
                .setValue(AIR_VENT_STATES_ENUM_PROPERTY, CreativeAirVentBlock.AirVentStates.OFFLINE)
        );
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return (BlockState) this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        // this is where the properties are actually added to the state
        pBuilder.add(AIR_VENT_STATES_ENUM_PROPERTY).add(FACING);
        ;
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new KineticOxygenGenerator(blockPos, blockState);
    }


    //Create specifics

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return Direction.Axis.Y;
    }


    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face == Direction.DOWN || face == Direction.UP;
    }


    @Override
    public Class<KineticOxygenGenerator> getBlockEntityClass() {
        return KineticOxygenGenerator.class;
    }

    @Override
    public BlockEntityType<? extends KineticOxygenGenerator> getBlockEntityType() {
        return BlockRegister.KINETIC_OXYGEN_GENERATOR_BE.get();
    }

}


