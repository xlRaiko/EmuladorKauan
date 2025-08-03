/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionRoller;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomLayout;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.rooms.RoomUnitType;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboGender;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.messages.ServerMessage;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InteractionDefault
extends HabboItem {
    private static final Logger LOGGER = LoggerFactory.getLogger(InteractionDefault.class);

    public InteractionDefault(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public InteractionDefault(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void serializeExtradata(ServerMessage serverMessage) {
        serverMessage.appendInt(this.isLimited() ? 256 : 0);
        serverMessage.appendString(this.getExtradata());
        super.serializeExtradata(serverMessage);
    }

    @Override
    public boolean isWalkable() {
        return this.getBaseItem().allowWalk();
    }

    @Override
    public boolean canWalkOn(RoomUnit roomUnit, Room room, Object[] objects) {
        return true;
    }

    @Override
    public void onMove(Room room, RoomTile oldLocation, RoomTile newLocation) {
        super.onMove(room, oldLocation, newLocation);
        if (room.getItemsAt(oldLocation).stream().noneMatch(item -> item.getClass().isAssignableFrom(InteractionRoller.class))) {
            for (RoomUnit unit : room.getRoomUnits()) {
                if (!oldLocation.unitIsOnFurniOnTile(unit, this.getBaseItem()) || newLocation.unitIsOnFurniOnTile(unit, this.getBaseItem())) continue;
                try {
                    this.onWalkOff(unit, room, new Object[]{oldLocation, newLocation});
                }
                catch (Exception exception) {}
            }
        }
    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) throws Exception {
        if (room != null && (client == null || this.canToggle(client.getHabbo(), room) || objects.length >= 2 && objects[1] instanceof WiredEffectType && objects[1] == WiredEffectType.TOGGLE_STATE)) {
            super.onClick(client, room, objects);
            if (objects != null && objects.length > 0 && objects[0] instanceof Integer) {
                if (this.getExtradata().length() == 0) {
                    this.setExtradata("0");
                }
                if (this.getBaseItem().getStateCount() > 0) {
                    int currentState = 0;
                    try {
                        currentState = Integer.valueOf(this.getExtradata());
                    }
                    catch (NumberFormatException e) {
                        LOGGER.error("Incorrect extradata (" + this.getExtradata() + ") for item ID (" + this.getId() + ") of type (" + this.getBaseItem().getName() + ")");
                    }
                    this.setExtradata("" + (currentState + 1) % this.getBaseItem().getStateCount());
                    this.needsUpdate(true);
                    room.updateItemState(this);
                }
            }
        }
    }

    @Override
    public void onWalk(RoomUnit roomUnit, Room room, Object[] objects) throws Exception {
    }

    @Override
    public void onWalkOn(RoomUnit roomUnit, Room room, Object[] objects) throws Exception {
        super.onWalkOn(roomUnit, room, objects);
        if (roomUnit != null && (this.getBaseItem().getEffectF() > 0 || this.getBaseItem().getEffectM() > 0)) {
            Bot bot;
            if (roomUnit.getRoomUnitType().equals((Object)RoomUnitType.USER)) {
                Habbo habbo = room.getHabbo(roomUnit);
                if (habbo != null) {
                    if (habbo.getHabboInfo().getGender().equals((Object)HabboGender.M) && this.getBaseItem().getEffectM() > 0 && habbo.getRoomUnit().getEffectId() != this.getBaseItem().getEffectM()) {
                        room.giveEffect(habbo, this.getBaseItem().getEffectM(), -1);
                        return;
                    }
                    if (habbo.getHabboInfo().getGender().equals((Object)HabboGender.F) && this.getBaseItem().getEffectF() > 0 && habbo.getRoomUnit().getEffectId() != this.getBaseItem().getEffectF()) {
                        room.giveEffect(habbo, this.getBaseItem().getEffectF(), -1);
                    }
                }
            } else if (roomUnit.getRoomUnitType().equals((Object)RoomUnitType.BOT) && (bot = room.getBot(roomUnit)) != null) {
                if (bot.getGender().equals((Object)HabboGender.M) && this.getBaseItem().getEffectM() > 0 && roomUnit.getEffectId() != this.getBaseItem().getEffectM()) {
                    room.giveEffect(bot.getRoomUnit(), this.getBaseItem().getEffectM(), -1);
                    return;
                }
                if (bot.getGender().equals((Object)HabboGender.F) && this.getBaseItem().getEffectF() > 0 && roomUnit.getEffectId() != this.getBaseItem().getEffectF()) {
                    room.giveEffect(bot.getRoomUnit(), this.getBaseItem().getEffectF(), -1);
                }
            }
        }
    }

    @Override
    public void onWalkOff(RoomUnit roomUnit, Room room, Object[] objects) throws Exception {
        super.onWalkOff(roomUnit, room, objects);
        if (roomUnit != null && (this.getBaseItem().getEffectF() > 0 || this.getBaseItem().getEffectM() > 0)) {
            Bot bot;
            int nextEffectM = 0;
            int nextEffectF = 0;
            if (objects != null && objects.length == 2 && objects[0] instanceof RoomTile && objects[1] instanceof RoomTile) {
                RoomTile goalTile = (RoomTile)objects[0];
                HabboItem topItem = room.getTopItemAt(goalTile.x, goalTile.y, objects[0] != objects[1] ? this : null);
                if (topItem != null && (topItem.getBaseItem().getEffectM() == this.getBaseItem().getEffectM() || topItem.getBaseItem().getEffectF() == this.getBaseItem().getEffectF())) {
                    return;
                }
                if (topItem != null) {
                    nextEffectM = topItem.getBaseItem().getEffectM();
                    nextEffectF = topItem.getBaseItem().getEffectF();
                }
            }
            if (roomUnit.getRoomUnitType().equals((Object)RoomUnitType.USER)) {
                Habbo habbo = room.getHabbo(roomUnit);
                if (habbo != null) {
                    if (habbo.getHabboInfo().getGender().equals((Object)HabboGender.M) && this.getBaseItem().getEffectM() > 0) {
                        room.giveEffect(habbo, nextEffectM, -1);
                        return;
                    }
                    if (habbo.getHabboInfo().getGender().equals((Object)HabboGender.F) && this.getBaseItem().getEffectF() > 0) {
                        room.giveEffect(habbo, nextEffectF, -1);
                    }
                }
            } else if (roomUnit.getRoomUnitType().equals((Object)RoomUnitType.BOT) && (bot = room.getBot(roomUnit)) != null) {
                if (bot.getGender().equals((Object)HabboGender.M) && this.getBaseItem().getEffectM() > 0) {
                    room.giveEffect(roomUnit, nextEffectM, -1);
                    return;
                }
                if (bot.getGender().equals((Object)HabboGender.F) && this.getBaseItem().getEffectF() > 0) {
                    room.giveEffect(roomUnit, nextEffectF, -1);
                }
            }
        }
    }

    public boolean canToggle(Habbo habbo, Room room) {
        if (room.hasRights(habbo)) {
            return true;
        }
        if (!habbo.getHabboStats().isRentingSpace()) {
            return false;
        }
        HabboItem rentSpace = room.getHabboItem(habbo.getHabboStats().rentedItemId);
        return rentSpace != null && RoomLayout.squareInSquare(RoomLayout.getRectangle(rentSpace.getX(), rentSpace.getY(), rentSpace.getBaseItem().getWidth(), rentSpace.getBaseItem().getLength(), rentSpace.getRotation()), RoomLayout.getRectangle(this.getX(), this.getY(), this.getBaseItem().getWidth(), this.getBaseItem().getLength(), this.getRotation()));
    }

    @Override
    public boolean allowWiredResetState() {
        return true;
    }
}

