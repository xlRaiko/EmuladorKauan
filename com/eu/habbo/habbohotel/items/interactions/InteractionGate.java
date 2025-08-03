/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.messages.ServerMessage;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionGate
extends HabboItem {
    public InteractionGate(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public InteractionGate(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void serializeExtradata(ServerMessage serverMessage) {
        serverMessage.appendInt(this.isLimited() ? 256 : 0);
        serverMessage.appendString(this.getExtradata());
        super.serializeExtradata(serverMessage);
    }

    @Override
    public boolean canWalkOn(RoomUnit roomUnit, Room room, Object[] objects) {
        return true;
    }

    @Override
    public boolean isWalkable() {
        return this.getExtradata().equals("1");
    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) throws Exception {
        boolean executedByWired;
        boolean bl = executedByWired = objects.length >= 2 && objects[1] instanceof WiredEffectType && objects[1] == WiredEffectType.TOGGLE_STATE;
        if (client != null && !room.hasRights(client.getHabbo()) && !executedByWired) {
            return;
        }
        for (RoomTile tile : room.getLayout().getTilesAt(room.getLayout().getTile(this.getX(), this.getY()), this.getBaseItem().getWidth(), this.getBaseItem().getLength(), this.getRotation())) {
            if (!room.hasHabbosAt(tile.x, tile.y)) continue;
            return;
        }
        if (this.getExtradata().length() == 0) {
            this.setExtradata("0");
        }
        this.setExtradata("" + (Integer.parseInt(this.getExtradata()) + 1) % 2);
        room.updateTile(room.getLayout().getTile(this.getX(), this.getY()));
        this.needsUpdate(true);
        room.updateItemState(this);
        super.onClick(client, room, new Object[]{"TOGGLE_OVERRIDE"});
    }

    @Override
    public void onWalk(RoomUnit roomUnit, Room room, Object[] objects) throws Exception {
        super.onWalkOn(roomUnit, room, objects);
    }

    @Override
    public void onWalkOn(RoomUnit roomUnit, Room room, Object[] objects) throws Exception {
        super.onWalkOn(roomUnit, room, objects);
    }

    @Override
    public void onWalkOff(RoomUnit roomUnit, Room room, Object[] objects) throws Exception {
        super.onWalkOff(roomUnit, room, objects);
    }

    @Override
    public boolean allowWiredResetState() {
        return true;
    }

    @Override
    public boolean isUsable() {
        return true;
    }
}

