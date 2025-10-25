package net.vildulv.minecraft.justspace.mixin;

import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.vildulv.minecraft.justspace.justspace;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({FireworkRocketItem.class})
public abstract class SpaceFireworkOverrides {



    @Inject(method = "use", at = @At("RETURN"), remap = false, cancellable = true)
    protected void use(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        if (!player.onGround() && level.dimension() == justspace.SPACE_DIMENSION_KEY && !player.isFallFlying()) {
            ItemStack itemstack = player.getItemInHand(hand);
            if (!level.isClientSide) {
                FireworkRocketEntity fireworkrocketentity = new FireworkRocketEntity(level, itemstack, player);
                level.addFreshEntity(fireworkrocketentity);
                itemstack.consume(1, player);
                Item item = (Item) (Object) this;
                player.awardStat(Stats.ITEM_USED.get(item));
            }
            Vec3 vec3 = player.getDeltaMovement();
            player.setDeltaMovement(vec3.x, (double)1, vec3.z);
            player.hasImpulse = true;

            cir.setReturnValue(InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide()));
        } else {
            cir.setReturnValue(InteractionResultHolder.pass(player.getItemInHand(hand)));
        }
    }

}
