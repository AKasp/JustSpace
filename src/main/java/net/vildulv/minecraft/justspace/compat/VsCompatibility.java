package net.vildulv.minecraft.justspace.compat;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.core.api.world.ServerShipWorld;
import org.valkyrienskies.core.apigame.ShipTeleportData;
import org.valkyrienskies.core.apigame.VSCore;
import org.valkyrienskies.core.impl.game.ShipTeleportDataImpl;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.ValkyrienSkiesMod;
import org.valkyrienskies.mod.common.entity.ShipMountingEntity;

import java.util.List;
import java.util.Set;


public class VsCompatibility {

    public static boolean teleportToValkyrienSkies(ServerLevel currentLevel,
                                                   ServerLevel targetLevel,
                                                   Entity entity, double x, double y, double z, float yRot, float xRo) {
        VSCore vsCore = ValkyrienSkiesMod.getVsCore();

        Entity ship = entity.getVehicle();
        if (!(ship instanceof ShipMountingEntity)) {
            return false;
        }

        List<Entity> passengers = ship.getPassengers();
        final Ship shipMountedToData = VSGameUtilsKt.getShipObjectManagingPos(currentLevel, entity.getOnPos());
        if (shipMountedToData == null) {
            return false;
        }
        ServerShipWorld world = VSGameUtilsKt.getShipObjectWorld((ServerLevel) currentLevel);
        Vector3d position = new Vector3d(x, y, z);
        String dimensionId = VSGameUtilsKt.getDimensionId(targetLevel);
        Quaterniondc orientation = new org.joml.Quaterniond().rotateYXZ(Math.toRadians(yRot), Math.toRadians(xRo), 0);

        ShipTeleportData shipTeleportData = new ShipTeleportDataImpl(position, orientation, new Vector3d(0, 0, 0), new Vector3d(0, 0, 0), dimensionId, null, null);

        final LoadedServerShip shipMountedTo = (LoadedServerShip) shipMountedToData;

        vsCore.teleportShip(world, shipMountedTo, shipTeleportData);
        if (currentLevel != targetLevel) {
            MinecraftServer server = currentLevel.getServer();
            server.execute(() -> {
                for (Entity passenger : passengers) {
                    passenger.teleportTo(targetLevel, x, y, z, Set.of(), yRot, xRo);
                    passenger.startRiding(ship, true);
                }
            });
        }

        return true;
    }
}
