package net.vildulv.minecraft.justspace;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;

public class OnFalldamageEventHandler {

    @SubscribeEvent
    public void onLivingFallEvent(LivingFallEvent event) {
        if (event.getEntity().level().dimension() == justspace.SPACE_DIMENSION_KEY) {
            // In the space dimension, fall damage is reduced to 0
             event.setDamageMultiplier(0.0f);
            event.setCanceled(true); // Cancel the fall event to prevent any further processing
        }
    }
}
