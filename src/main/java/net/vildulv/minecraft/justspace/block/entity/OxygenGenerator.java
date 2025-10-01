package net.vildulv.minecraft.justspace.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public interface OxygenGenerator {

    int MAX_AREA_SIZE = 1000; // Maximum area size to prevent infinite loops

    class OxygenGeneratorData {
        public Set<Vec3i> area = new HashSet<>();
        public final Stack<BlockPos> queue = new Stack<>();
        public boolean prevCheckStatus = false;
        public final Set<OxygenGenerator> currentNeighbors = new HashSet<>();
        public int currentMaxSize = MAX_AREA_SIZE;

        public void setSealed() {
            prevCheckStatus = true;
        }

        public Set<Vec3i> getArea() {
            return area;
        }

        public void setArea(Set<Vec3i> area) {
            this.area = area;
        }


        public int getMaxSize() {
            return currentMaxSize;
        }

        public void increaseMaxSize(int additionalSize) {
            currentMaxSize += additionalSize;
        }

        public Set<OxygenGenerator> getNeighbours() {
            return currentNeighbors;
        }

        public boolean isSealed() {
            return prevCheckStatus;
        }
    }

    OxygenGeneratorData getOxygenGeneratorData();

    ServerLevel getServerLevel();

    BlockPos getStartPos();
}
