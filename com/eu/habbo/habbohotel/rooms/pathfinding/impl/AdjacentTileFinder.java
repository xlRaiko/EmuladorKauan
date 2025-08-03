/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.rooms.pathfinding.impl;

import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomTileState;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.rooms.pathfinding.impl.PathfinderContext;
import com.eu.habbo.habbohotel.rooms.pathfinding.impl.Rotation;
import com.eu.habbo.habbohotel.rooms.pathfinding.impl.TileValidator;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class AdjacentTileFinder {
    AdjacentTileFinder() {
    }

    public static Set<RoomTile> getAdjacent(PathfinderContext context, RoomTile node, RoomTile nextTile, RoomUnit unit, boolean canMoveDiagonally, boolean retroStyleDiagonals) {
        short x = node.getX();
        short y = node.getY();
        HashSet<RoomTile> adj = new HashSet<RoomTile>();
        AdjacentTileFinder.addDirectionAdjacent(context, node, nextTile, unit, x, y, adj);
        if (canMoveDiagonally) {
            AdjacentTileFinder.addDiagonalAdjacent(context, node, nextTile, unit, x, y, adj, retroStyleDiagonals);
        }
        return adj;
    }

    public static void addDirectionAdjacent(PathfinderContext context, RoomTile node, RoomTile nextTile, RoomUnit unit, short x, short y, Set<RoomTile> adj) {
        for (short[] direction : Rotation.DIRECTIONS) {
            RoomTile temp;
            short newX = (short)(x + direction[0]);
            short newY = (short)(y + direction[1]);
            if (TileValidator.isOutOfBounds(context.getRoom().getLayout(), newX, newY) || (temp = AdjacentTileFinder.findTile(context, newX, newY)) == null) continue;
            AdjacentTileFinder.addAdjacent(node, nextTile, unit, temp, adj, false);
        }
    }

    public static void addDiagonalAdjacent(PathfinderContext context, RoomTile node, RoomTile nextTile, RoomUnit unit, short x, short y, Set<RoomTile> adj, boolean retroStyleDiagonals) {
        for (short[] direction : Rotation.DIAGONAL_DIRECTIONS) {
            RoomTile temp;
            short newX = (short)(x + direction[0]);
            short newY = (short)(y + direction[1]);
            if (TileValidator.isOutOfBounds(context.getRoom().getLayout(), newX, newY) || !retroStyleDiagonals && AdjacentTileFinder.isBlockedDiagonal(context, x, y, newX, newY) || !TileValidator.isWalkableOrGoal(context, temp = AdjacentTileFinder.findTile(context, newX, newY))) continue;
            AdjacentTileFinder.addAdjacent(node, nextTile, unit, temp, adj, true);
        }
    }

    public static boolean isBlockedDiagonal(PathfinderContext context, short x, short y, short newX, short newY) {
        RoomTile offX = AdjacentTileFinder.findTile(context, newX, y);
        RoomTile offY = AdjacentTileFinder.findTile(context, x, newY);
        return offX == null || offY == null || !offX.isWalkable() && !offY.isWalkable();
    }

    private static void addAdjacent(RoomTile node, RoomTile nextTile, RoomUnit unit, RoomTile temp, Set<RoomTile> adj, boolean isDiagonal) {
        if (temp != null && (temp.getState() != RoomTileState.SIT || nextTile.getStackHeight() - node.getStackHeight() <= 2.0) && AdjacentTileFinder.canWalkOn(temp, unit)) {
            temp.isDiagonally(isDiagonal);
            adj.add(temp);
        }
    }

    private static boolean canWalkOn(RoomTile tile, RoomUnit unit) {
        return tile != null && (tile.getState() != RoomTileState.BLOCKED && tile.getState() != RoomTileState.INVALID || unit.canOverrideTile(tile));
    }

    public static RoomTile findTile(PathfinderContext context, short x, short y) {
        long key = AdjacentTileFinder.generateTileKey(x, y);
        Map<Long, RoomTile> tileCache = context.getTileCache();
        RoomTile tile = tileCache.get(key);
        if (tile != null) {
            return tile;
        }
        tile = context.getRoom().getLayout().getTile(x, y);
        if (tile == null) {
            return null;
        }
        tile = tile.copy();
        tileCache.put(key, tile);
        return tile;
    }

    private static long generateTileKey(short x, short y) {
        return (long)x << 32 | (long)y & 0xFFFFFFFFL;
    }

    public static void calculateCost(PathfinderContext context, RoomTile currentAdj, RoomTile current, PriorityQueue<RoomTile> openList) {
        if (!openList.contains(currentAdj)) {
            AdjacentTileFinder.updateAdj(context, currentAdj, current, openList);
            return;
        }
        if (currentAdj.getgCosts() > AdjacentTileFinder.calculateGCosts(currentAdj, current)) {
            currentAdj.setPrevious(current);
            currentAdj.setgCosts(current, AdjacentTileFinder.getCost(currentAdj));
        }
    }

    public static int calculateGCosts(RoomTile tile, RoomTile previousRoomTile) {
        if (tile.isDiagonally()) {
            return previousRoomTile.getgCosts() + 11;
        }
        return previousRoomTile.getgCosts() + 10;
    }

    public static void updateAdj(PathfinderContext context, RoomTile currentAdj, RoomTile current, PriorityQueue<RoomTile> openList) {
        currentAdj.setPrevious(current);
        RoomTile tile = AdjacentTileFinder.findTile(context, context.getNewTile().getX(), context.getNewTile().getY());
        if (tile == null) {
            return;
        }
        currentAdj.sethCosts(tile, AdjacentTileFinder.getCost(current));
        currentAdj.setgCosts(current, AdjacentTileFinder.getCost(currentAdj));
        openList.add(currentAdj);
    }

    private static int getCost(RoomTile tile) {
        return tile.isDiagonally() ? 11 : 10;
    }
}

