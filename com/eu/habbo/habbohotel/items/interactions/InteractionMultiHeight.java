/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.rooms.RoomUnitStatus;
import com.eu.habbo.habbohotel.rooms.RoomUnitType;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboGender;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.messages.ServerMessage;
import gnu.trove.set.hash.THashSet;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

public class InteractionMultiHeight
extends HabboItem {
    public InteractionMultiHeight(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    public InteractionMultiHeight(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
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
        return this.getBaseItem().allowWalk();
    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) throws Exception {
        super.onClick(client, room, objects);
        if (!(client == null || room.hasRights(client.getHabbo()) || objects.length >= 2 && objects[1] instanceof WiredEffectType && objects[1] == WiredEffectType.TOGGLE_STATE)) {
            return;
        }
        if (objects.length > 0 && objects[0] instanceof Integer && room != null) {
            HabboItem topItem = room.getTopItemAt(this.getX(), this.getY());
            if (topItem != null && !topItem.equals(this)) {
                return;
            }
            this.needsUpdate(true);
            if (this.getExtradata().length() == 0) {
                this.setExtradata("0");
            }
            if (this.getBaseItem().getMultiHeights().length > 0) {
                this.setExtradata("" + (Integer.parseInt(this.getExtradata()) + 1) % this.getBaseItem().getMultiHeights().length);
                this.needsUpdate(true);
                room.updateTiles(room.getLayout().getTilesAt(room.getLayout().getTile(this.getX(), this.getY()), this.getBaseItem().getWidth(), this.getBaseItem().getLength(), this.getRotation()));
                room.updateItemState(this);
            }
        }
    }

    public void updateUnitsOnItem(Room room) {
        THashSet<RoomTile> occupiedTiles = room.getLayout().getTilesAt(room.getLayout().getTile(this.getX(), this.getY()), this.getBaseItem().getWidth(), this.getBaseItem().getLength(), this.getRotation());
        for (RoomTile tile : occupiedTiles) {
            Collection<RoomUnit> unitsOnItem = room.getRoomUnitsAt(room.getLayout().getTile(tile.x, tile.y));
            THashSet updatedUnits = new THashSet();
            for (RoomUnit unit : unitsOnItem) {
                if (unit.hasStatus(RoomUnitStatus.MOVE) && unit.getGoal() != tile) continue;
                if (this.getBaseItem().allowSit() || unit.hasStatus(RoomUnitStatus.SIT)) {
                    unit.sitUpdate = true;
                    unit.statusUpdate(true);
                    continue;
                }
                unit.setZ(unit.getCurrentLocation().getStackHeight());
                unit.setPreviousLocationZ(unit.getZ());
                unit.statusUpdate(true);
            }
        }
    }

    @Override
    public void onWalk(RoomUnit roomUnit, Room room, Object[] objects) {
    }

    @Override
    public void onWalkOn(RoomUnit roomUnit, Room room, Object[] objects) throws Exception {
        Habbo habbo;
        super.onWalkOn(roomUnit, room, objects);
        if (roomUnit != null && (this.getBaseItem().getEffectF() > 0 || this.getBaseItem().getEffectM() > 0) && roomUnit.getRoomUnitType().equals((Object)RoomUnitType.USER) && (habbo = room.getHabbo(roomUnit)) != null) {
            if (habbo.getHabboInfo().getGender().equals((Object)HabboGender.M) && this.getBaseItem().getEffectM() > 0 && habbo.getRoomUnit().getEffectId() != this.getBaseItem().getEffectM()) {
                room.giveEffect(habbo, this.getBaseItem().getEffectM(), -1);
                return;
            }
            if (habbo.getHabboInfo().getGender().equals((Object)HabboGender.F) && this.getBaseItem().getEffectF() > 0 && habbo.getRoomUnit().getEffectId() != this.getBaseItem().getEffectF()) {
                room.giveEffect(habbo, this.getBaseItem().getEffectF(), -1);
            }
        }
    }

    @Override
    public void onWalkOff(RoomUnit roomUnit, Room room, Object[] objects) throws Exception {
        Habbo habbo;
        super.onWalkOff(roomUnit, room, objects);
        if (roomUnit != null && (this.getBaseItem().getEffectF() > 0 || this.getBaseItem().getEffectM() > 0) && roomUnit.getRoomUnitType().equals((Object)RoomUnitType.USER) && (habbo = room.getHabbo(roomUnit)) != null) {
            if (habbo.getHabboInfo().getGender().equals((Object)HabboGender.M) && this.getBaseItem().getEffectM() > 0) {
                room.giveEffect(habbo, 0, -1);
                return;
            }
            if (habbo.getHabboInfo().getGender().equals((Object)HabboGender.F) && this.getBaseItem().getEffectF() > 0) {
                room.giveEffect(habbo, 0, -1);
            }
        }
    }

    @Override
    public boolean allowWiredResetState() {
        return true;
    }
}

