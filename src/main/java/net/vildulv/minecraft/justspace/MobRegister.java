package net.vildulv.minecraft.justspace;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementType;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.vildulv.minecraft.justspace.mob.entity.SpaceZombieEntity;
import net.vildulv.minecraft.justspace.mob.entity.VoidMantaEntity;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;


@EventBusSubscriber(modid = justspace.MODID)
public class MobRegister {

    static final RandomSource RANDOM = RandomSource.create(982193453L);

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, justspace.MODID);

    public static final Supplier<EntityType<SpaceZombieEntity>> SPACE_ZOMBIE =
            ENTITY_TYPES.register("space_zombie", () -> EntityType.Builder.of(SpaceZombieEntity::new, MobCategory.MONSTER)
                    .sized(0.6f, 1.95f).build("justspace/space_zombie"));


    public static final Supplier<EntityType<VoidMantaEntity>> VOID_MANTA =
            ENTITY_TYPES.register("void_manta", () -> EntityType.Builder.of(VoidMantaEntity::new, MobCategory.CREATURE)
                    .sized(0.9F, 0.5F)
                    .eyeHeight(0.175F).
                    build("justspace/void_manta"));


    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }


    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(SPACE_ZOMBIE.get(), SpaceZombieEntity.createAttributes() .build());
        event.put(VOID_MANTA.get(), VoidMantaEntity.createAttributes() .build());
    }

    @SubscribeEvent
    public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(VOID_MANTA.get(), IN_SPACE, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                VoidMantaEntity::checkVoidMantaSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }

    private static final SpawnPlacementType IN_SPACE =  new SpawnPlacementType() {
        @Override
        public boolean isSpawnPositionOk(LevelReader levelReader, BlockPos blockPos, @Nullable EntityType<?> entityType) {
            return levelReader.getBlockState(blockPos).isAir();
        }
        @Override
        public BlockPos adjustSpawnPosition(LevelReader level, BlockPos pos) {
            if (pos.getY() <= 0) {
                pos = new BlockPos(pos.getX(), RANDOM.nextInt(100)+100, pos.getZ());
            }
            return pos;
        }
    };
}
