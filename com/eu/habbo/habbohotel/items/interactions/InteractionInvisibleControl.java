/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionDefault;
import com.eu.habbo.habbohotel.rooms.Room;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionInvisibleControl
extends InteractionDefault {
    public InteractionInvisibleControl(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public InteractionInvisibleControl(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void onPlace(Room room) {
        super.onPlace(room);
        String extraData = !room.isHideWired() ? "1" : "0";
        this.setExtradata(extraData);
        room.updateItemState(this);
    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) throws Exception {
        if (client != null && this.canToggle(client.getHabbo(), room)) {
            String extraData;
            if (!room.isHideWired()) {
                extraData = "1";
                room.setHideWired(true);
            } else {
                extraData = "0";
                room.setHideWired(false);
            }
            this.setExtradata(extraData);
            room.updateItemState(this);
        }
    }
}

