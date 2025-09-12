package net.vildulv.minecraft.justspace.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
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
import net.vildulv.minecraft.justspace.block.entity.PoweredOxygenGenerator;

import javax.annotation.Nullable;

import static net.vildulv.minecraft.justspace.block.CreativeAirVentBlock.AIR_VENT_STATES_ENUM_PROPERTY;
import static net.vildulv.minecraft.justspace.block.entity.PoweredOxygenGenerator.FACING;

public class PoweredAirVentBlock extends BaseEntityBlock implements EntityBlock {
    private static final MapCodec<PoweredAirVentBlock> CODEC = simpleCodec(PoweredAirVentBlock::new);


    public MapCodec<PoweredAirVentBlock> codec() {
        return CODEC;
    }


    public PoweredAirVentBlock(Properties properties) {
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
        return new PoweredOxygenGenerator(blockPos, blockState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (blockEntityType == BlockRegister.POWERED_OXYGEN_GENERATOR_BE.get()) {
            return createTickerHelper(blockEntityType, BlockRegister.POWERED_OXYGEN_GENERATOR_BE.get(), PoweredOxygenGenerator::tick);
        }

        return null;

    }
}


