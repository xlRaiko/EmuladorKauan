/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionTeleport;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionTeleportTile
extends InteractionTeleport {
    public InteractionTeleportTile(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public InteractionTeleportTile(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean canWalkOn(RoomUnit roomUnit, Room room, Object[] objects) {
        return true;
    }

    @Override
    public boolean isWalkable() {
        return true;
    }

    @Override
    public void onWalkOn(RoomUnit roomUnit, Room room, Object[] objects) throws Exception {
        Habbo habbo;
        if (roomUnit != null && this.canWalkOn(roomUnit, room, objects) && (habbo = room.getHabbo(roomUnit)) != null) {
            if (!this.canUseTeleport(habbo.getClient(), room)) {
                return;
            }
            if (!habbo.getRoomUnit().isTeleporting) {
                habbo.getRoomUnit().setGoalLocation(habbo.getRoomUnit().getCurrentLocation());
                this.startTeleport(room, habbo, 1000);
            }
        }
    }
}

