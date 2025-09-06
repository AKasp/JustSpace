package net.vildulv.minecraft.justspace;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.vildulv.minecraft.justspace.mob.entity.SpaceZombieEntity;

import java.util.function.Supplier;


@EventBusSubscriber(modid = justspace.MODID)
public class MobRegister {


    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, justspace.MODID);

    public static final Supplier<EntityType<SpaceZombieEntity>> SPACE_ZOMBIE =
            ENTITY_TYPES.register("space_zombie", () -> EntityType.Builder.of(SpaceZombieEntity::new, MobCategory.MONSTER)
                    .sized(0.6f, 1.95f).build("justspace/space_zombie"));


    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }


    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(SPACE_ZOMBIE.get(), SpaceZombieEntity.createAttributes() .build());
    }
}
