package net.vildulv.minecraft.justspace;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.vildulv.minecraft.justspace.mixin.ServerGamePacketListenerImplAccessor;
import net.vildulv.minecraft.justspace.mixin.ServerPlayerAccessor;

import java.util.*;

public class TeleportOnTick {

    private static final int LAND_TO_SPACE_CONVERSION_FACTOR = 256;
    private static final int LANDING_ZONE = 256;

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
        if (entity.getY() < triggerAtY && entity.yo < triggerAtY) {
            handleTeleportToPlanet(entity);
        }
        if (entity.getY() > 300 && entity.yo > 300) {
            handleTeleportToSpace(entity);
        }
    }

    private static void handleTeleportToPlanet(Entity entity) {
        if (entity.level().dimension() != SPACE_DIMENSION_KEY) {
            return;
        }
        if (entity instanceof ServerPlayer player) {
            if (isTeleporting(player)) {
                return; // Don't teleport if already teleporting
            }
            final Entity vehicle = player.getVehicle();
            List<Entity> entitiesToTeleport = getEntitiesToTeleport(entity);

            ResourceKey<Level> targetDimension = resolveTargetDimension(player);

            //Needed to keep track of the new entities created if we change dimensions.
            Map<Integer, Entity> teleportedEntities = new HashMap<>();
            entitiesToTeleport.forEach(teleportedEntity -> {
                if (isAllowedEntity(teleportedEntity) &&
                        teleportedEntity.level().dimension() == SPACE_DIMENSION_KEY) {
                    if (teleportedEntity instanceof ServerPlayerAccessor playerAccessor) {
                        playerAccessor.setIsChangingDimension(true);
                    }

                    System.out.println("Player teleport");
                    var x = teleportedEntity.getX();
                    var y = 250;
                    var z = teleportedEntity.getZ();
                    if (targetDimension != SPACE_DIMENSION_KEY) {
                        Vec3 targetPos = spaceCoordToLandCoord(teleportedEntity.getPosition(0.0f), targetDimension);
                        x = targetPos.x;
                        z = targetPos.z;
                    }
                    ServerLevel level = teleportedEntity.level().getServer().getLevel(targetDimension);

                    Entity newEntity = teleportedEntity.teleport(new TeleportTransition(level, new Vec3(x, y, z), Vec3.ZERO, teleportedEntity.getYRot(), teleportedEntity.getXRot(), Set.of(), TeleportTransition.DO_NOTHING));
                    teleportedEntities.put(teleportedEntity.getId(), newEntity);


                    if (teleportedEntity instanceof ServerPlayerAccessor playerAccessor) {
                        // Vanilla's AntiCheat is triggers on falling and teleports, even in Vanilla.
                        // So I'll just disable it until the player lands, so it doesn't look like it's my mod causing the issue.
                        playerAccessor.setIsChangingDimension(false);
                    }
                }
            });

            if (vehicle != null) {
                Entity newVehicle = teleportedEntities.get(vehicle.getId());
                player.startRiding(newVehicle);
            }
        }
    }

    private static ResourceKey<Level> resolveTargetDimension(ServerPlayer player) {
        double x = player.getX();
        double z = player.getZ();
        Map<String, Config.PlanetRecord> planets = Config.PARSED_PLANETS;
        for (Config.PlanetRecord planet : planets.values()) {
            if (Math.abs(planet.x() - x) < LANDING_ZONE && Math.abs(planet.z() - z) < LANDING_ZONE) {
                return planet.dimensionKey();
            }
        }
        return SPACE_DIMENSION_KEY; // Default to space if no planet found
    }

    private static void handleTeleportToSpace(Entity entity) {
        if (entity.level().dimension() == SPACE_DIMENSION_KEY) {
            return;
        }
        if (entity instanceof ServerPlayer player) {
            if (isTeleporting(player)) {
                return; // Don't teleport if already teleporting
            }
            final Entity vehicle = player.getVehicle();
            List<Entity> entitiesToTeleport = getEntitiesToTeleport(entity);

            ResourceKey<Level> currentDimension = player.level().dimension();
            Map<Integer, Entity> teleportedEntities = new HashMap<>();
            entitiesToTeleport.forEach(teleportedEntity -> {
                if (isAllowedEntity(teleportedEntity) &&
                        teleportedEntity.level().dimension() != SPACE_DIMENSION_KEY) {
                    if (teleportedEntity instanceof ServerPlayerAccessor playerAccessor) {
                        playerAccessor.setIsChangingDimension(true);
                    }

                    System.out.println("Player teleport");
                    Vec3 targetPos = landCoordToSpaceCoord(teleportedEntity.getPosition(0.0f), currentDimension);

                    ServerLevel level = teleportedEntity.level().getServer().getLevel(SPACE_DIMENSION_KEY);
                    Entity newEntity = teleportedEntity.teleport(new TeleportTransition(level, new Vec3(targetPos.x, targetPos.y, targetPos.z), Vec3.ZERO, teleportedEntity.getYRot(), teleportedEntity.getXRot(), Set.of(), TeleportTransition.DO_NOTHING));
                    teleportedEntities.put(teleportedEntity.getId(), newEntity);
                    if (teleportedEntity instanceof ServerPlayerAccessor playerAccessor) {
                        // Vanilla's AntiCheat is triggers on falling and teleports, even in Vanilla.
                        // So I'll just disable it until the player lands, so it doesn't look like it's my mod causing the issue.
                        playerAccessor.setIsChangingDimension(false);
                    }
                }
            });

            if (vehicle != null) {
                Entity newVehicle = teleportedEntities.get(vehicle.getId());
                player.startRiding(newVehicle);
            }
        }
    }

    private static boolean isTeleporting(ServerPlayer player) {
        ServerGamePacketListenerImplAccessor accessor = (ServerGamePacketListenerImplAccessor) player.connection;
        return accessor.getAwaitingPositionFromClient() != null;
    }

    private static List<Entity> getEntitiesToTeleport(Entity entity) {
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
        return entitiesToTeleport;
    }

    private static Vec3 spaceCoordToLandCoord(Vec3 spaceCoord, ResourceKey<Level> dimension) {
        Config.PlanetRecord planetRecord = getPlanetRecord(dimension);
        double x = (spaceCoord.x - planetRecord.x()) * LAND_TO_SPACE_CONVERSION_FACTOR;
        double y = 250;
        double z = (spaceCoord.z - planetRecord.z()) * LAND_TO_SPACE_CONVERSION_FACTOR;
        return new Vec3(x, y, z);
    }

    private static Vec3 landCoordToSpaceCoord(Vec3 landCoord, ResourceKey<Level> dimension) {
        Config.PlanetRecord planetRecord = getPlanetRecord(dimension);
        double x = landCoord.x / LAND_TO_SPACE_CONVERSION_FACTOR + planetRecord.x();
        double y = 50;
        double z = landCoord.z / LAND_TO_SPACE_CONVERSION_FACTOR + planetRecord.z();
        return new Vec3(x, y, z);
    }

    private static Config.PlanetRecord getPlanetRecord(ResourceKey<Level> dimension) {
        Map<String, Config.PlanetRecord> planets = Config.PARSED_PLANETS;
        for (Config.PlanetRecord planet : planets.values()) {
            if (planet.dimensionKey().equals(dimension)) {
                return planet;
            }
        }
        return null; // or throw an exception if preferred
    }

    private static boolean isAllowedEntity(Entity entity) {
        if (entity.level().isClientSide) {
            return false;
        }

        if (entity instanceof LivingEntity) {
            return true;
        }
        return false;
    }
}
