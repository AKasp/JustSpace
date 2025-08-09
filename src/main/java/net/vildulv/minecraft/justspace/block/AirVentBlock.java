package net.vildulv.minecraft.justspace.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.vildulv.minecraft.justspace.BlockRegister;
import net.vildulv.minecraft.justspace.block.entity.OxygenGenerator;

import javax.annotation.Nullable;

public class AirVentBlock extends BaseEntityBlock implements EntityBlock {
    public static final MapCodec<AirVentBlock> CODEC = simpleCodec(AirVentBlock::new);

    public enum AirVentStates implements StringRepresentable {
        WORKING("working"),
        SEALED("sealed"),
        OFFLINE("offline");

        private final String name;

        private AirVentStates(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }

        public String getSerializedName() {
            return this.name;
        }
    }

    public static final EnumProperty<AirVentStates> AIR_VENT_STATES_ENUM_PROPERTY = EnumProperty.create("air_vent_state", AirVentStates.class);


    public MapCodec<AirVentBlock> codec() {
        return CODEC;
    }


    public AirVentBlock(BlockBehaviour.Properties properties) {
        super(properties);

        this.registerDefaultState(stateDefinition.any()
                .setValue(AIR_VENT_STATES_ENUM_PROPERTY, AirVentStates.WORKING)
        );
    }


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        // this is where the properties are actually added to the state
        pBuilder.add(AIR_VENT_STATES_ENUM_PROPERTY);
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new OxygenGenerator(blockPos, blockState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return blockEntityType == BlockRegister.OXYGEN_GENERATOR_BE.get() ? createTickerHelper(blockEntityType, BlockRegister.OXYGEN_GENERATOR_BE.get(), OxygenGenerator::tick) : null;
    }
}


