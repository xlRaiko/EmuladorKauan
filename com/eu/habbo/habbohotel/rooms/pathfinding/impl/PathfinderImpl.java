/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.rooms.pathfinding.impl;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomLayout;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomTileState;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.rooms.pathfinding.Pathfinder;
import com.eu.habbo.habbohotel.rooms.pathfinding.impl.AdjacentTileFinder;
import com.eu.habbo.habbohotel.rooms.pathfinding.impl.PathFinderException;
import com.eu.habbo.habbohotel.rooms.pathfinding.impl.PathfinderContext;
import com.eu.habbo.habbohotel.rooms.pathfinding.impl.TileValidator;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class PathfinderImpl
implements Pathfinder {
    private static final int CACHED_TIMEOUT_MS = Emulator.getConfig().getInt("pathfinder.execution_time.milli", 25);
    private static final boolean CACHED_TIMEOUT_ENABLED = Emulator.getConfig().getBoolean("pathfinder.max_execution_time.enabled", false);
    private static final long CACHED_TIMEOUT_NANOS = (long)CACHED_TIMEOUT_MS * 1000000L;
    private final Room room;
    private double maximumStepHeight;
    private boolean allowFalling;
    private final boolean retroStyleDiagonals;

    public PathfinderImpl(Room room, double maximumStepHeight, boolean allowFalling, boolean retroStyleDiagonals) {
        this.room = room;
        this.maximumStepHeight = maximumStepHeight;
        this.allowFalling = allowFalling;
        this.retroStyleDiagonals = retroStyleDiagonals;
    }

    @Override
    public CompletableFuture<Deque<RoomTile>> findPathAsync(RoomTile oldTile, RoomTile newTile, RoomTile goalLocation, RoomUnit roomUnit) {
        return CompletableFuture.supplyAsync(() -> this.findPath(oldTile, newTile, goalLocation, roomUnit)).exceptionally(error -> {
            throw new RuntimeException(new PathFinderException("Failed to find path", (Throwable)error));
        });
    }

    @Override
    public Deque<RoomTile> findPath(RoomTile oldTile, RoomTile newTile, RoomTile goalLocation, RoomUnit roomUnit) {
        return this.findPath(oldTile, newTile, goalLocation, roomUnit, false);
    }

    private boolean processCurrent(PathfinderContext context, RoomTile current, PriorityQueue<RoomTile> openList, HashSet<RoomTile> closedList) {
        if (current.getX() == context.getNewTile().getX() && current.getY() == context.getNewTile().getY()) {
            return true;
        }
        TileValidator.swapList(current, closedList, openList);
        Set<RoomTile> adjacentNodes = AdjacentTileFinder.getAdjacent(context, current, context.getNewTile(), context.getRoomUnit(), context.isCanMoveDiagonally(), this.retroStyleDiagonals);
        adjacentNodes.forEach(currentAdj -> this.processAdjacent(context, (RoomTile)currentAdj, closedList, current, openList));
        return false;
    }

    private void processAdjacent(PathfinderContext context, RoomTile currentAdj, HashSet<RoomTile> closedList, RoomTile current, PriorityQueue<RoomTile> openList) {
        if (closedList.contains(currentAdj)) {
            return;
        }
        if (context.getRoomUnit().canOverrideTile(currentAdj)) {
            AdjacentTileFinder.updateAdj(context, currentAdj, current, openList);
            return;
        }
        if (currentAdj.getState() == RoomTileState.BLOCKED || (currentAdj.getState() == RoomTileState.SIT || currentAdj.getState() == RoomTileState.LAY) && !currentAdj.equals(context.getGoalLocation())) {
            TileValidator.swapList(currentAdj, closedList, openList);
            return;
        }
        if (this.isInvalidHeight(context, currentAdj, current) || TileValidator.isAnyUnitAt(this.room, context, currentAdj, closedList, openList)) {
            return;
        }
        AdjacentTileFinder.calculateCost(context, currentAdj, current, openList);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Deque<RoomTile> findPath(RoomTile oldTile, RoomTile newTile, RoomTile goalLocation, RoomUnit roomUnit, boolean isWalkthroughRetry) {
        if (this.room == null || !this.room.isLoaded() || oldTile == null || newTile == null || oldTile.equals(newTile) || newTile.getState() == RoomTileState.INVALID) {
            return new LinkedList<RoomTile>();
        }
        long startTime = CACHED_TIMEOUT_ENABLED ? System.nanoTime() : 0L;
        int iterationCount = 0;
        PriorityQueue<RoomTile> openList = new PriorityQueue<RoomTile>(Comparator.comparingInt(RoomTile::getfCosts));
        HashSet<RoomTile> closedList = new HashSet<RoomTile>();
        openList.add(oldTile.copy());
        PathfinderContext context = PathfinderContext.buildContext(this.room, newTile, goalLocation, roomUnit, isWalkthroughRetry);
        try {
            LinkedList<RoomTile> linkedList;
            while (!openList.isEmpty()) {
                if (CACHED_TIMEOUT_ENABLED && (++iterationCount & 0x3F) == 0 && System.nanoTime() - startTime > CACHED_TIMEOUT_NANOS) {
                    linkedList = new LinkedList();
                    return linkedList;
                }
                RoomTile current = openList.poll();
                if (current == null) break;
                if (!this.processCurrent(context, current, openList, closedList)) continue;
                Deque<RoomTile> deque = this.tracePath(AdjacentTileFinder.findTile(context, oldTile.getX(), oldTile.getY()), current);
                return deque;
            }
            if (context.isAllowWalkthrough() && !isWalkthroughRetry) {
                linkedList = this.findPath(oldTile, newTile, goalLocation, roomUnit, true);
                return linkedList;
            }
            linkedList = new LinkedList();
            return linkedList;
        }
        finally {
            openList.clear();
            closedList.clear();
            if (context.getTileCache() != null) {
                context.getTileCache().clear();
            }
        }
    }

    private boolean isInvalidHeight(PathfinderContext context, RoomTile currentAdj, RoomTile current) {
        double height = currentAdj.getStackHeight() - current.getStackHeight();
        return !this.allowFalling && height < -this.maximumStepHeight || currentAdj.getState() == RoomTileState.OPEN && height > this.maximumStepHeight && this.findPathAroundAdjacentTile(context, currentAdj, current, height);
    }

    private boolean findPathAroundAdjacentTile(PathfinderContext context, RoomTile currentAdj, RoomTile current, double height) {
        PriorityQueue<RoomTile> adjacentTiles = new PriorityQueue<RoomTile>(Comparator.comparingDouble(tile -> Math.abs(tile.getStackHeight() - current.getStackHeight())));
        adjacentTiles.addAll(AdjacentTileFinder.getAdjacent(context, current, context.getNewTile(), context.getRoomUnit(), context.isCanMoveDiagonally(), this.retroStyleDiagonals));
        RoomTile intermediateTile = adjacentTiles.peek();
        if (intermediateTile == null || Math.abs(height) > this.maximumStepHeight) {
            return true;
        }
        currentAdj.setPrevious(intermediateTile);
        intermediateTile.setPrevious(current);
        return false;
    }

    public Deque<RoomTile> tracePath(RoomTile start, RoomTile goal) {
        ArrayDeque<RoomTile> path = new ArrayDeque<RoomTile>();
        RoomLayout layout = this.room.getLayout();
        if (start == null) {
            return path;
        }
        RoomTile curr = goal;
        while (curr != null) {
            path.addFirst(layout.getTile(curr.getX(), curr.getY()));
            if ((curr = curr.getPrevious()) == null || !curr.equals(start)) continue;
            return path;
        }
        return path;
    }

    @Override
    public boolean isAllowFalling() {
        return this.allowFalling;
    }

    @Override
    public void setAllowFalling(boolean allow) {
        this.allowFalling = allow;
    }

    @Override
    public double getMaxStepHeight() {
        return this.maximumStepHeight;
    }

    @Override
    public void setMaxStepHeight(double value) {
        this.maximumStepHeight = value;
    }
}

