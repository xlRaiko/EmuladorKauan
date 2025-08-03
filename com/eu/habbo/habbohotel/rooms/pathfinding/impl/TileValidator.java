/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.rooms.pathfinding.impl;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomLayout;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.rooms.RoomUnitType;
import com.eu.habbo.habbohotel.rooms.pathfinding.impl.PathfinderContext;
import com.eu.habbo.habbohotel.users.Habbo;
import java.util.Collection;
import java.util.PriorityQueue;
import java.util.Set;

public class TileValidator {
    private TileValidator() {
        throw new IllegalStateException("Utility class");
    }

    public static boolean isWalkableOrGoal(PathfinderContext context, RoomTile temp) {
        return temp != null && (temp.isWalkable() || context.getGoalLocation().equals(temp));
    }

    public static boolean isOutOfBounds(RoomLayout layout, short x, short y) {
        return x < 0 || y < 0 || x >= layout.getMapSizeX() || y >= layout.getMapSizeY();
    }

    public static boolean isAnyUnitAt(Room room, PathfinderContext context, RoomTile currentAdj, Set<RoomTile> closedList, PriorityQueue<RoomTile> openList) {
        RoomUnit exception = null;
        if (context.getRoomUnit().getRoomUnitType().equals((Object)RoomUnitType.USER)) {
            Habbo habbo = room.getHabbo(context.getRoomUnit().getId());
            RoomUnit roomUnit = exception = habbo != null && habbo.getHabboInfo().getRiding() != null ? habbo.getHabboInfo().getRiding().getRoomUnit() : null;
        }
        if (TileValidator.isTileWalkable(room, context, currentAdj, exception)) {
            TileValidator.swapList(currentAdj, closedList, openList);
            return true;
        }
        return false;
    }

    public static void swapList(RoomTile currentAdj, Set<RoomTile> closedList, PriorityQueue<RoomTile> openList) {
        closedList.add(currentAdj);
        openList.remove(currentAdj);
    }

    private static boolean isTileWalkable(Room room, PathfinderContext context, RoomTile currentAdj, RoomUnit exception) {
        return TileValidator.hasBlockingUnits(room, currentAdj, exception) && context.getDoorTile().distance(currentAdj) > 2.0 && (!context.isWalkthroughRetry() || !context.isAllowWalkthrough() || currentAdj.equals(context.getGoalLocation()));
    }

    public static boolean hasBlockingUnits(Room room, RoomTile tile, RoomUnit exception) {
        Collection<RoomUnit> units = room.getRoomUnitsAt(tile);
        if (units.isEmpty()) {
            return false;
        }
        for (RoomUnit unit : units) {
            if (unit == exception) continue;
            return true;
        }
        return false;
    }
}

