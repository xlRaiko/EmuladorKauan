/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.items.interactions.wired.triggers;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredTrigger;
import com.eu.habbo.habbohotel.items.interactions.wired.WiredSettings;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import com.eu.habbo.messages.ServerMessage;
import gnu.trove.set.hash.THashSet;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class WiredTriggerHabboAntiAfk
extends InteractionWiredTrigger {
    public static final WiredTriggerType type = WiredTriggerType.ANTI_AFK;
    public final THashSet<HabboItem> items = new THashSet();

    public WiredTriggerHabboAntiAfk(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredTriggerHabboAntiAfk(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        if (stuff.length >= 1 && stuff[0] instanceof HabboItem) {
            roomUnit.anti_afkTimer = 0;
            return this.items.contains(stuff[0]);
        }
        return false;
    }

    @Override
    public WiredTriggerType getType() {
        return type;
    }

    @Override
    public void serializeWiredData(ServerMessage message, Room room) {
        THashSet<HabboItem> items = new THashSet<HabboItem>();
        if (Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId()) == null) {
            items.addAll(this.items);
        } else {
            for (HabboItem item : this.items) {
                if (Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId()).getHabboItem(item.getId()) != null) continue;
                items.add(item);
            }
        }
        for (HabboItem item : items) {
            this.items.remove(item);
        }
        message.appendBoolean(false);
        message.appendInt(WiredHandler.MAXIMUM_FURNI_SELECTION);
        message.appendInt(this.items.size());
        for (HabboItem item : this.items) {
            message.appendInt(item.getId());
        }
        message.appendInt(this.getBaseItem().getSpriteId());
        message.appendInt(this.getId());
        message.appendString("");
        message.appendInt(0);
        message.appendInt(0);
        message.appendInt(this.getType().code);
        message.appendInt(0);
        message.appendInt(0);
    }

    @Override
    public boolean saveData(WiredSettings settings) {
        this.items.clear();
        int count = settings.getFurniIds().length;
        for (int i = 0; i < count; ++i) {
            this.items.add(Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId()).getHabboItem(settings.getFurniIds()[i]));
        }
        return true;
    }

    @Override
    public String getWiredData() {
        return WiredHandler.getGsonBuilder().create().toJson(new JsonData(this.items.stream().map(HabboItem::getId).collect(Collectors.toList())));
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException {
        block6: {
            String wiredData;
            block5: {
                this.items.clear();
                wiredData = set.getString("wired_data");
                if (!wiredData.startsWith("{")) break block5;
                JsonData data = WiredHandler.getGsonBuilder().create().fromJson(wiredData, JsonData.class);
                for (Integer id : data.itemIds) {
                    HabboItem item = room.getHabboItem(id);
                    if (item == null) continue;
                    this.items.add(item);
                }
                break block6;
            }
            if (wiredData.split(":").length < 3) break block6;
            super.setDelay(Integer.parseInt(wiredData.split(":")[0]));
            if (!wiredData.split(":")[2].equals("\t")) {
                for (String s : wiredData.split(":")[2].split(";")) {
                    if (s.isEmpty()) continue;
                    try {
                        HabboItem item = room.getHabboItem(Integer.parseInt(s));
                        if (item == null) continue;
                        this.items.add(item);
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                }
            }
        }
    }

    @Override
    public void onPickUp() {
        this.items.clear();
    }

    @Override
    public boolean isTriggeredByRoomUnit() {
        return true;
    }

    static class JsonData {
        List<Integer> itemIds;

        public JsonData(List<Integer> itemIds) {
            this.itemIds = itemIds;
        }
    }
}

