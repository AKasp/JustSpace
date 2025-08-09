package net.vildulv.minecraft.justspace.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.vildulv.minecraft.justspace.justspace;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({LivingEntity.class, ItemEntity.class, FallingBlockEntity.class})
public abstract class GravityOverrides {

    @Inject(method = "getDefaultGravity", at = @At("RETURN"), remap = false, cancellable = true)
    protected void getDefaultGravity(CallbackInfoReturnable<Double> cir) {
        Entity entity = (Entity) (Object) this;
        if (entity.level().dimension() == justspace.SPACE_DIMENSION_KEY) {
            cir.setReturnValue(0.005); // Override gravity to 0 in the space dimension
        }
    }
}
