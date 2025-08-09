package net.vildulv.minecraft.justspace;

import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.vildulv.minecraft.justspace.block.entity.OxygenGenerator;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class VentTracker {

    private record HistoricalAreas(Set<Vec3i> area, Set<OxygenGenerator> generators, int maxSize, boolean sealed) {
    }

    private static final Map<ServerLevel, Map<OxygenGenerator, HistoricalAreas>> VENT_POSITIONS = new ConcurrentHashMap<>();


    public static boolean canExpand(ServerLevel level, OxygenGenerator generator) {
        Map<OxygenGenerator, HistoricalAreas> levelMap = VENT_POSITIONS.get(level);
        if (levelMap == null) {
            // If there are no registered generators in this level, we can register the generator.
            VENT_POSITIONS.put(level, new ConcurrentHashMap<>());
            return false;
        }
        Set<Vec3i> area = generator.getArea();
        boolean intersectAny = false;

        //  removeExistingNeighbors(generator.getNeighbours(), levelMap);
        for (Map.Entry<OxygenGenerator, HistoricalAreas> entry : levelMap.entrySet()) {
            if (entry.getKey().equals(generator) || entry.getValue().generators.contains(generator) || generator.getNeighbours().contains(entry.getKey())) {
                // Skip the generator itself or if it is already registered in another area.
                continue;
            }
            HistoricalAreas historicalArea = entry.getValue();
            Set<Vec3i> oldArea = historicalArea.area();
            boolean intersects = oldArea.stream().anyMatch(area::contains);
            if (intersects) {
                intersectAny = true;
                // If the area of the generator intersects with another generator's area, handle the overlap.
                area.addAll(historicalArea.area);
                generator.addNeighbor(historicalArea.generators);
                generator.addNeighbor(Set.of(entry.getKey()));
                generator.increaseMaxSize(historicalArea.maxSize);
                entry.getKey().setSealed();
           //     System.out.println("Merging areas of " + generator + " with " + entry.getKey() + ". New total size: " + generator.getMaxSize());
            }
        }
        if (!intersectAny) {
            //If it does not intersect with any other generator's area, then it cant grow anymore and it can be stored in historical areas.
       //     System.out.println("No Merge. Register new area for " + generator + " with size: " + generator.getMaxSize());
            VENT_POSITIONS.computeIfAbsent(level, l -> new ConcurrentHashMap<>()).
                    put(generator, new HistoricalAreas(Set.copyOf(area), Set.copyOf(generator.getNeighbours()), generator.getMaxSize(), false));
        } else {
            removeExistingNeighbors(generator.getNeighbours(), levelMap);
        }
        return intersectAny;
    }

    public static void addVent(Level level, OxygenGenerator generator) {
        if (level instanceof ServerLevel serverLevel) {
        //    System.out.println("Fully sealed, Registering new area for " + generator + " with size: " + generator.getMaxSize());
            VENT_POSITIONS.computeIfAbsent(serverLevel, l -> new ConcurrentHashMap<>())
                    .put(generator, new HistoricalAreas(Set.copyOf(generator.getArea()), Set.copyOf(generator.getNeighbours()), generator.getMaxSize(), generator.isSealed()));

        }
    }

    private static void removeExistingNeighbors(Set<OxygenGenerator> neighbors, Map<OxygenGenerator, HistoricalAreas> levelMap) {
    //    System.out.println("Removing neighbors " + neighbors + " from level map: " + levelMap);
        for (OxygenGenerator neighbor : neighbors) {
       //     System.out.println("Removing neighbor " + neighbor + " from level map.");
            levelMap.remove(neighbor);
        }
    }

    public static void unregisterVent(ServerLevel level, OxygenGenerator generator) {
        Map<OxygenGenerator, HistoricalAreas> map = VENT_POSITIONS.get(level);
        if (map != null) {
            map.remove(generator);
        }
    }

    public static boolean isInBreathableArea(Level level, Vec3i pos) {
        Vec3i altPos = pos.west(); //Some strange thing with the way the position of the camera is calculated, so we check both the original and the west position.
        if (!(level instanceof ServerLevel)) {
            return false; // Only check breathable areas in server levels.
        }
        Map<OxygenGenerator, HistoricalAreas> levelMap = VENT_POSITIONS.get(level);
        if (levelMap == null) {
            return false;
        }
        for (HistoricalAreas historicalArea : levelMap.values()) {
            if (historicalArea.sealed && (historicalArea.area().contains(pos) || historicalArea.area().contains(altPos))) {
                return true; // If the position is in any of the historical areas, return true.
            }
        }
        System.out.println("No air " + levelMap);
        return false; // If no historical area contains the position, return false.
    }
}
