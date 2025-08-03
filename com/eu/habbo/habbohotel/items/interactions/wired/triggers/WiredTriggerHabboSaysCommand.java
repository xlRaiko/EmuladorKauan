/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.items.interactions.wired.triggers;

import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredTrigger;
import com.eu.habbo.habbohotel.items.interactions.wired.WiredSettings;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import com.eu.habbo.messages.ServerMessage;
import java.sql.ResultSet;
import java.sql.SQLException;

public class WiredTriggerHabboSaysCommand
extends InteractionWiredTrigger {
    private static final WiredTriggerType type = WiredTriggerType.SAY_COMMAND;
    private boolean ownerOnly = false;
    private String key = "";

    public WiredTriggerHabboSaysCommand(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredTriggerHabboSaysCommand(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        String message;
        Habbo habbo = room.getHabbo(roomUnit);
        return habbo != null && this.key.length() > 0 && stuff[0] instanceof String && (message = ((String)stuff[0]).replace(":", "")).equals(this.key);
    }

    @Override
    public String getWiredData() {
        return (this.ownerOnly ? "1" : "0") + "\t" + this.key;
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException {
        String[] data = set.getString("wired_data").split("\t");
        if (data.length == 2) {
            this.ownerOnly = data[0].equalsIgnoreCase("1");
            this.setKey(data[1]);
        }
    }

    @Override
    public void onPickUp() {
        this.ownerOnly = false;
        this.key = "";
    }

    @Override
    public WiredTriggerType getType() {
        return type;
    }

    @Override
    public void serializeWiredData(ServerMessage message, Room room) {
        message.appendBoolean(false);
        message.appendInt(5);
        message.appendInt(0);
        message.appendInt(this.getBaseItem().getSpriteId());
        message.appendInt(this.getId());
        message.appendString(this.key);
        message.appendInt(0);
        message.appendInt(1);
        message.appendInt(this.getType().code);
        message.appendInt(0);
        message.appendInt(0);
    }

    @Override
    public boolean saveData(WiredSettings settings) {
        this.ownerOnly = settings.getIntParams()[0] == 1;
        this.setKey(settings.getStringParam());
        return true;
    }

    private void setKey(String key) {
        if (key.contains(":")) {
            key = key.replaceAll(":", "");
        }
        this.key = key;
    }

    @Override
    public boolean isTriggeredByRoomUnit() {
        return true;
    }
}

