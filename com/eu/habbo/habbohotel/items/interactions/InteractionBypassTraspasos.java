/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionDefault;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionBypassTraspasos
extends InteractionDefault {
    public InteractionBypassTraspasos(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public InteractionBypassTraspasos(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean canOverrideTile(RoomUnit unit, Room room, RoomTile tile) {
        return true;
    }
}

