package net.vildulv.minecraft.justspace.mixin;

import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.fluids.FluidType;
import net.vildulv.minecraft.justspace.justspace;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Entity.class})
public class InWaterOverride {

/*
    @Inject(method = "isInWater", at = @At("RETURN"), remap = false, cancellable = true)
    protected void isInWater(CallbackInfoReturnable<Boolean> cir) {
        Entity entity = (Entity) (Object) this;
        if (entity.level().dimension() == justspace.SPACE_DIMENSION_KEY) {
      //      cir.setReturnValue(true); // Override gravity to 0 in the space dimension
        }
    }

*/

    @Inject(method = "getEyeInFluidType", at = @At("RETURN"), remap = false, cancellable = true)
    protected void getEyeInFluidType(CallbackInfoReturnable<FluidType> cir) {
        Entity entity = (Entity) (Object) this;
        if (entity.level().dimension() == justspace.SPACE_DIMENSION_KEY) {
            cir.setReturnValue(justspace.ETHER_FLUID.value());
        }
    }
}
