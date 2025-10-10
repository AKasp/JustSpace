package net.vildulv.minecraft.justspace;

import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.OutgoingChatMessage;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.vildulv.minecraft.justspace.compat.VsCompatibility;
import net.vildulv.minecraft.justspace.mixin.ServerGamePacketListenerImplAccessor;
import net.vildulv.minecraft.justspace.mixin.ServerPlayerAccessor;

import java.util.*;

import static net.vildulv.minecraft.justspace.justspace.SPACE_DIMENSION_KEY;

public class TeleportOnTick {

    private static final int LAND_TO_SPACE_CONVERSION_FACTOR = 256;
    private static final int LANDING_ZONE = 256;
    private static final int TELEPORT_HEIGHT = Config.TELEPORT_HEIGHT.getAsInt();


    public static void onEntityTick(PlayerTickEvent.Post event) {
        Entity entity = event.getEntity();

        if (entity.level().isClientSide && entity instanceof Player player) {
            showWarningMessage(player);
        }
        if (!isAllowedEntity(entity)) {
            return;
        }


        int triggerAtY = -0;// entity.level().getMinY() - 30;
        if (entity.getY() < triggerAtY && entity.yo < triggerAtY) {
            handleTeleportToPlanet(entity);
        }
        if (entity.getY() > TELEPORT_HEIGHT && entity.yo > TELEPORT_HEIGHT) {
            handleTeleportToSpace(entity);
        }

    }

    private static void showWarningMessage(Player player) {
        if (player.getY() < -20 && player.level().dimension() == SPACE_DIMENSION_KEY) {
            long tick = player.level().getGameTime();
            if (tick % 40 == 0) {
                PlayerChatMessage chatMessage = PlayerChatMessage.unsigned(player.getUUID(), "WARNING! Increased cosmical radiation! Ascend immediately or suffer fatal damage!");
                player.createCommandSourceStack().sendChatMessage(new OutgoingChatMessage.Player(chatMessage), false, ChatType.bind(ChatType.CHAT, player));
            }
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

            ServerLevel originalLevel = player.serverLevel();
            final Entity vehicle = player.getVehicle();
            List<Entity> entitiesToTeleport = getEntitiesToTeleport(entity);

            ResourceKey<Level> targetDimension = resolveTargetDimension(player);

            if (targetDimension == SPACE_DIMENSION_KEY) {
                return; // No valid planet found, stay in space
            }

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
                    var y = TELEPORT_HEIGHT-50;
                    var z = teleportedEntity.getZ();
                    if (targetDimension != SPACE_DIMENSION_KEY) {
                        Vec3 targetPos = spaceCoordToLandCoord(teleportedEntity.getPosition(0.0f), targetDimension);
                        x = targetPos.x;
                        z = targetPos.z;
                    }
                    ServerLevel level = teleportedEntity.level().getServer().getLevel(targetDimension);


                    Entity newEntity = teleportTo(level, teleportedEntity, x, y, z, teleportedEntity.getYRot(), teleportedEntity.getXRot());
                    teleportedEntities.put(teleportedEntity.getId(), newEntity);

                    if (teleportedEntity instanceof ServerPlayerAccessor playerAccessor) {
                        // Vanilla's AntiCheat is triggers on falling and teleports, even in Vanilla.
                        // So I'll just disable it until the player lands, so it doesn't look like it's my mod causing the issue.
                        playerAccessor.setIsChangingDimension(false);
                    }

                    originalLevel.getChunkSource().broadcast(teleportedEntity, new ClientboundTeleportEntityPacket(teleportedEntity));
                }
            });


            if (vehicle != null) {
                Entity newVehicle = teleportedEntities.get(vehicle.getId());
                player.startRiding(newVehicle, true);
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

                    Entity newEntity = teleportTo(level, teleportedEntity, targetPos.x, targetPos.y, targetPos.z, teleportedEntity.getYRot(), teleportedEntity.getXRot());
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
        double y = TELEPORT_HEIGHT-50;
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

        return true;
    }


    private static Entity teleportTo(ServerLevel level, Entity entity, double x, double y, double z, float yRot, float xRot) {
      /*
        } */
        boolean success = false;
        if (ModList.get().isLoaded("valkyrienskies")) {
            success = VsCompatibility.teleportToValkyrienSkies((ServerLevel) entity.level(), level, entity, x, y, z, yRot, xRot);
        }
        if (!success) {
            Vec3 relPos = new Vec3((double) 0.5F, (double) 0.0F, (double) 0.0F);
            Entity newEntity = entity.changeDimension(new DimensionTransition(level, new Vec3(x, y, z), relPos, 0, 0, DimensionTransition.DO_NOTHING));

     /*       if (entity instanceof ServerPlayer) {
                entity.teleportTo(level, x, y, z, Set.of(), entity.getYRot(), entity.getXRot());
                return entity;
            } else { */
            //Copy of entity.teleportTo needed to get new entity object
     /*           float f = Mth.clamp(xRot, -90.0F, 90.0F);
                if (level == entity.level()) {
                    entity.moveTo(x, y, z, yRot, f);
                    teleportPassengers(entity);
                    entity.setYHeadRot(yRot);
                    return entity;
                } else {
                    entity.unRide();
                    Entity newEntity = entity.getType().create(level);
                    if (newEntity == null) {
                        return null;
                    }

                    newEntity.restoreFrom(entity);
                    newEntity.moveTo(x, y, z, yRot, f);
                    newEntity.setYHeadRot(yRot);
                    newEntity.setRemoved(Entity.RemovalReason.CHANGED_DIMENSION);
                    level.addDuringTeleport(newEntity);
                    return newEntity;
                }
            } */
              /*  entity.teleportTo(level, x, y, z, Set.of(), entity.getYRot(), entity.getXRot());
                entity.gameEvent(GameEvent.TELEPORT);
            } */
            return newEntity;
        }
        return entity;
    }

    private static void teleportPassengers(Entity entity) {
        entity.getSelfAndPassengers().forEach((ride) -> {
            Iterator var1 = ride.getPassengers().iterator();

            while (var1.hasNext()) {
                Entity e = (Entity) var1.next();
                ride.positionRider(e);
            }

        });
    }
}
