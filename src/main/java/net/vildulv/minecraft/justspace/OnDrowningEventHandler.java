package net.vildulv.minecraft.justspace;

import net.minecraft.core.Vec3i;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDrownEvent;

public class OnDrowningEventHandler {

    @SubscribeEvent
    public void onLivingDrownEvent(LivingDrownEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().dimension() == justspace.SPACE_DIMENSION_KEY &&
        entity.isAlive() && !entity.canBreatheUnderwater() && !MobEffectUtil.hasWaterBreathing(entity) && !entity.getType().is(justspace.SPACE_MOBS)) {
            Vec3 pos = entity.getEyePosition();
            if (VentTracker.isInBreathableArea(entity.level(), new Vec3i((int)pos.x, (int)pos.y, (int)pos.z))) {
                // If the entity is in a breathable area, allow breathing
                event.setDrowning(false);
                return;
            }
            event.setDrowning(true);
            event.setDamageAmount(2);
            event.setBubbleCount(0);
        }
    }
}
