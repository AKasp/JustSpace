package net.vildulv.minecraft.justspace;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import net.vildulv.minecraft.justspace.mixin.ServerGamePacketListenerImplAccessor;
import net.vildulv.minecraft.justspace.mixin.ServerPlayerAccessor;

import java.util.ArrayList;
import java.util.Set;

public class TeleportOnTick {


    public static final ResourceKey<Level> SPACE_DIMENSION_KEY = ResourceKey.create(
            Registries.DIMENSION,
            ResourceLocation.fromNamespaceAndPath(justspace.MODID, "space")
    );

    public static void onEntityTick(PlayerTickEvent.Post event) {
        Entity entity = event.getEntity();
        if (!isAllowedEntity(entity)) {
            return;
        }

        int triggerAtY = entity.level().getMinY() - 30;
        boolean shouldTeleport = (entity.getY() < triggerAtY && entity.yo < triggerAtY) ||
                (entity.getY() > 400 && entity.yo > 400);

        boolean isTeleporting = false;
        if (entity instanceof ServerPlayer player) {
            ServerGamePacketListenerImplAccessor accessor = (ServerGamePacketListenerImplAccessor) player.connection;
            isTeleporting = accessor.getAwaitingPositionFromClient() != null;
        }
     /*   CompoundTag persistentData = Balm.getHooks().getPersistentData(entity);
        if (entity.onGround()) {
            persistentData.putLong("LastGroundedPos", entity.blockPosition().asLong());
        } */

        if (shouldTeleport && !isTeleporting  && fireForgivingVoidEvent(entity)) {
            if (entity instanceof LivingEntity livingEntity) {
                // applyFallThroughVoidEffects(livingEntity);
            }

            final var entitiesToTeleport = new ArrayList<Entity>();
            entitiesToTeleport.add(entity);
            if (entity.isVehicle()) {
                entitiesToTeleport.addAll(entity.getPassengers());
                entity.ejectPassengers();
            }

            final var vehicle = entity.getVehicle();
            if (vehicle != null) {
                entitiesToTeleport.add(vehicle);
                entity.stopRiding();
            }

            entitiesToTeleport.forEach(teleportedEntity -> {
                if (isAllowedEntity(teleportedEntity)) {
                    if (teleportedEntity instanceof ServerPlayerAccessor player) {
                        player.setIsChangingDimension(true);
                    }

                    System.out.println("Player teleport");
                    // final var teleportedEntityData = Balm.getHooks().getPersistentData(teleportedEntity);
                    //  final var returnToGrounded = ForgivingVoidConfig.getActive().returnToLastGrounded;
                    //   final var lastGroundedPos = teleportedEntityData.getLong("LastGroundedPos").map(BlockPos::of).orElseGet(teleportedEntity::blockPosition);
                    final var x = teleportedEntity.getX(); //returnToGrounded ? lastGroundedPos.getX() + 0.5f : teleportedEntity.getX();
                    var y = 350;
                    final var z = teleportedEntity.getZ(); // returnToGrounded ? lastGroundedPos.getZ() + 0.5f : teleportedEntity.getZ();
                    if (teleportedEntity.getY() < 400) {
                        teleportedEntity.teleportTo(x, y, z);
                    } else {
                        y = 50;
                        ServerLevel level  = teleportedEntity.level().getServer().getLevel(SPACE_DIMENSION_KEY);
                        teleportedEntity.teleportTo(level, x, y, z,
                                Set.of(),
                                teleportedEntity.getYRot(), teleportedEntity.getXRot(),
                                true);
                    }

                    // teleportedEntityData.putBoolean("ForgivingVoidIsFalling", true);
                }
            });

            if (vehicle != null) {
                entity.startRiding(vehicle);
            }
        }  /* else if (persistentData.getBooleanOr("ForgivingVoidIsFalling", false)) {
            // LivingFallEvent is not called when the player falls into water or is flying, so reset it manually - and give no damage at all.
            if (hasLanded(entity) || isOrMayFly(entity)) {
                persistentData.putBoolean("ForgivingVoidIsFalling", false);
                if (entity instanceof ServerPlayerAccessor player) {
                    player.setIsChangingDimension(false);
                }
                return;
            } */

        if (entity instanceof ServerPlayerAccessor player) {
            // Vanilla's AntiCheat is triggers on falling and teleports, even in Vanilla.
            // So I'll just disable it until the player lands, so it doesn't look like it's my mod causing the issue.
            player.setIsChangingDimension(true);
            //  }
        }
    }


    private static boolean fireForgivingVoidEvent(Entity entity) {
        //    ForgivingVoidFallThroughEvent event = new ForgivingVoidFallThroughEvent(entity);
        //     Balm.getEvents().fireEvent(event);
        //    return !event.isCanceled();
        return true;
    }

    private static boolean hasLanded(Entity entity) {
        if (entity.onGround() || entity.isInWater() || entity.isInLava()) {
            return true;
        }
        return false;
        // final var landedOnState = entity.level().getBlockState(entity.blockPosition());
        //  return FALL_CATCHING_BLOCKS.contains(landedOnState.getBlock());
    }

    private static boolean isOrMayFly(Entity entity) {
        if (!(entity instanceof Player player)) {
            return false;
        }

        return player.getAbilities().flying || player.getAbilities().mayfly;
    }


    private static boolean isAllowedEntity(Entity entity) {
        if (entity.level().isClientSide) {
            return false;
        }

        if (entity instanceof Player) {
            return true;
        }
        return false;
    }
}
