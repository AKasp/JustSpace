package net.vildulv.minecraft.justspace.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;

public class PingDeviceBlock extends HorizontalDirectionalBlock {

    public static final MapCodec<PingDeviceBlock> CODEC = simpleCodec(PingDeviceBlock::new);

    @Override
    public MapCodec<PingDeviceBlock> codec() {
        return CODEC;
    }

    public PingDeviceBlock(Properties properties) {
        super(properties);
    }


    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return (BlockState) this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{FACING});
    }
}
