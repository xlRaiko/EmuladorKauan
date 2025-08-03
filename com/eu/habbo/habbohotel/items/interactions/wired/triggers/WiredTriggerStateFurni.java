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
import com.eu.habbo.habbohotel.wired.customs.WiredMatchFurniSetting;
import com.eu.habbo.messages.ServerMessage;
import gnu.trove.set.hash.THashSet;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class WiredTriggerStateFurni
extends InteractionWiredTrigger {
    public static final WiredTriggerType type = WiredTriggerType.STATE_FURNI;
    private final THashSet<WiredMatchFurniSetting> items = new THashSet();
    public int tipo = 0;

    public WiredTriggerStateFurni(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredTriggerStateFurni(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        Object object;
        if (stuff.length >= 1 && (object = stuff[0]) instanceof HabboItem) {
            HabboItem habboItem = (HabboItem)object;
            for (WiredMatchFurniSetting setting : this.items) {
                if (setting.itemId != habboItem.getId()) continue;
                if (this.tipo == 0) {
                    return setting.state.equalsIgnoreCase(habboItem.getExtradata());
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public String getWiredData() {
        this.refresh();
        StringBuilder data = new StringBuilder(this.items.size() + ":");
        if (this.items.isEmpty()) {
            data.append(";");
        } else {
            Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId());
            for (WiredMatchFurniSetting item : this.items) {
                HabboItem i;
                if (room == null || (i = room.getHabboItem(item.itemId)) == null) continue;
                data.append(item).append(";");
            }
        }
        data.append(":").append(this.tipo);
        return data.toString();
    }

    @Override
    public void serializeWiredData(ServerMessage message, Room room) {
        this.refresh();
        message.appendBoolean(false);
        message.appendInt(WiredHandler.MAXIMUM_FURNI_SELECTION);
        message.appendInt(this.items.size());
        for (WiredMatchFurniSetting item : this.items) {
            message.appendInt(item.itemId);
        }
        message.appendInt(this.getBaseItem().getSpriteId());
        message.appendInt(this.getId());
        message.appendString("");
        message.appendInt(1);
        message.appendInt(this.tipo);
        message.appendInt(0);
        message.appendInt(this.getType().code);
        message.appendInt(0);
        message.appendInt(0);
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException {
        String[] items;
        String[] data = set.getString("wired_data").split(":");
        for (String item : items = data[1].split(";")) {
            try {
                String[] stuff = item.split("-");
                if (stuff.length >= 7) {
                    this.items.add(new WiredMatchFurniSetting(Integer.parseInt(stuff[0]), stuff[1], Integer.parseInt(stuff[2]), Integer.parseInt(stuff[3]), Integer.parseInt(stuff[4]), Double.parseDouble(stuff[5] + "-" + stuff[6])));
                    continue;
                }
                if (stuff.length == 6) {
                    this.items.add(new WiredMatchFurniSetting(Integer.parseInt(stuff[0]), stuff[1], Integer.parseInt(stuff[2]), Integer.parseInt(stuff[3]), Integer.parseInt(stuff[4]), Double.parseDouble(stuff[5])));
                    continue;
                }
                if (stuff.length != 5) continue;
                this.items.add(new WiredMatchFurniSetting(Integer.parseInt(stuff[0]), stuff[1], Integer.parseInt(stuff[2]), Integer.parseInt(stuff[3]), Integer.parseInt(stuff[4])));
            }
            catch (Exception e) {
                Emulator.getLogging().logErrorLine(e);
            }
        }
    }

    @Override
    public void onPickUp() {
        this.items.clear();
    }

    @Override
    public WiredTriggerType getType() {
        return type;
    }

    @Override
    public boolean saveData(WiredSettings settings) {
        Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId());
        if (room == null) {
            return false;
        }
        int itemsCount = settings.getFurniIds().length;
        if (itemsCount > Emulator.getConfig().getInt("hotel.wired.furni.selection.count")) {
            return false;
        }
        ArrayList<WiredMatchFurniSetting> newSettings = new ArrayList<WiredMatchFurniSetting>();
        for (int i = 0; i < itemsCount; ++i) {
            int itemId = settings.getFurniIds()[i];
            HabboItem it = Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId()).getHabboItem(itemId);
            if (it == null) continue;
            newSettings.add(new WiredMatchFurniSetting(it.getId(), it.getExtradata(), it.getRotation(), it.getX(), it.getY()));
        }
        this.items.addAll(newSettings);
        this.tipo = settings.getIntParams()[0];
        return true;
    }

    private void refresh() {
        Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId());
        if (room != null && room.isLoaded()) {
            THashSet<WiredMatchFurniSetting> remove = new THashSet<WiredMatchFurniSetting>();
            for (WiredMatchFurniSetting setting : this.items) {
                HabboItem item = room.getHabboItem(setting.itemId);
                if (item != null) continue;
                remove.add(setting);
            }
            for (WiredMatchFurniSetting setting : remove) {
                this.items.remove(setting);
            }
        }
    }
}

