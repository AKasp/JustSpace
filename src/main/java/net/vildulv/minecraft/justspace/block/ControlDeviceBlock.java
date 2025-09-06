package net.vildulv.minecraft.justspace.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;

public class ControlDeviceBlock extends HorizontalDirectionalBlock {

    public static final MapCodec<ControlDeviceBlock> CODEC = simpleCodec(ControlDeviceBlock::new);

    @Override
    public MapCodec<ControlDeviceBlock> codec() {
        return CODEC;
    }

    public ControlDeviceBlock(Properties properties) {
        super(properties);
    }


    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return (BlockState) this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{FACING});
    }
}
