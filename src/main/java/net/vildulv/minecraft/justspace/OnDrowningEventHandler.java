package net.vildulv.minecraft.justspace;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDrownEvent;

public class OnDrowningEventHandler {

    @SubscribeEvent
    public void onLivingDrownEvent(LivingDrownEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().dimension() == TeleportOnTick.SPACE_DIMENSION_KEY &&
        entity.isAlive() && !entity.canBreatheUnderwater()) {
            event.setDrowning(true);
            event.setDamageAmount(2);
            event.setBubbleCount(0);
        }
    }
}
