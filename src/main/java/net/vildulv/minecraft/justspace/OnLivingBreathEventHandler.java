package net.vildulv.minecraft.justspace;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingBreatheEvent;

public class OnLivingBreathEventHandler {

    @SubscribeEvent
    public void onLivingBreathEvent(LivingBreatheEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().dimension() == TeleportOnTick.SPACE_DIMENSION_KEY &&
                entity.isAlive() && !entity.canBreatheUnderwater()) {
            // Decrease air supply by 5 every tick
            event.setCanBreathe(false);
            event.setConsumeAirAmount(5);
        }
    }
}
