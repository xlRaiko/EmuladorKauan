/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionStackHelper;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.outgoing.rooms.items.FloorItemUpdateComposer;
import gnu.trove.set.hash.THashSet;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionFurniMoveHelper
extends InteractionStackHelper {
    public InteractionFurniMoveHelper(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public InteractionFurniMoveHelper(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void onMove(Room room, RoomTile oldLocation, RoomTile newLocation) {
        super.onMove(room, oldLocation, newLocation);
        short oldX = oldLocation.x;
        short oldY = oldLocation.y;
        short newX = newLocation.x;
        short newY = newLocation.y;
        int width = this.getBaseItem().getWidth();
        int length = this.getBaseItem().getLength();
        THashSet<RoomTile> oldOccupiedTiles = room.getLayout().getTilesAt(oldLocation, width, length, 0);
        for (RoomTile roomTile : oldOccupiedTiles) {
            THashSet<HabboItem> items = room.getItemsAt(roomTile);
            for (HabboItem roomItem : items) {
                if (roomItem.getId() == this.getId()) continue;
                int itemX = roomItem.getX() + (newX - oldX);
                int itemY = roomItem.getY() + (newY - oldY);
                roomItem.setX((short)itemX);
                roomItem.setY((short)itemY);
                roomItem.needsUpdate(true);
                Emulator.getThreading().run(roomItem);
                room.sendComposer(new FloorItemUpdateComposer(roomItem).compose());
            }
        }
    }
}

