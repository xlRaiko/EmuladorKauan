/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.rooms.pathfinding;

import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import java.util.Deque;
import java.util.concurrent.CompletableFuture;

public interface Pathfinder {
    public CompletableFuture<Deque<RoomTile>> findPathAsync(RoomTile var1, RoomTile var2, RoomTile var3, RoomUnit var4);

    public Deque<RoomTile> findPath(RoomTile var1, RoomTile var2, RoomTile var3, RoomUnit var4);

    public Deque<RoomTile> findPath(RoomTile var1, RoomTile var2, RoomTile var3, RoomUnit var4, boolean var5);

    public boolean isAllowFalling();

    public void setAllowFalling(boolean var1);

    public double getMaxStepHeight();

    public void setMaxStepHeight(double var1);
}

