package net.vildulv.minecraft.justspace.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.vildulv.minecraft.justspace.block.entity.CommandersTerminalBlockEntity;
import net.vildulv.minecraft.justspace.block.util.loggenerator.CommandersLogGenerator;

import javax.annotation.Nullable;

import static net.vildulv.minecraft.justspace.block.entity.PoweredOxygenGenerator.FACING;

public class CommandersTerminalDeviceBlock extends BaseEntityBlock implements EntityBlock {

    public static final MapCodec<CommandersTerminalDeviceBlock> CODEC = simpleCodec(CommandersTerminalDeviceBlock::new);

    @Override
    public MapCodec<CommandersTerminalDeviceBlock> codec() {
        return CODEC;
    }

    public CommandersTerminalDeviceBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return (BlockState) this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{FACING});
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }


    @Override
    protected BlockState rotate(BlockState state, Rotation rot) {
        return (BlockState) state.setValue(FACING, rot.rotate((Direction) state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation((Direction) state.getValue(FACING)));
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        CommandersTerminalBlockEntity terminalBlockEntity = new CommandersTerminalBlockEntity(blockPos, blockState);
        return terminalBlockEntity;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide) {
            if (level.getBlockEntity(pos) instanceof CommandersTerminalBlockEntity terminalBlockEntity) {
                player.openMenu(new SimpleMenuProvider(terminalBlockEntity, Component.literal("Terminal")), pos);

            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.CONSUME;
    }
}
