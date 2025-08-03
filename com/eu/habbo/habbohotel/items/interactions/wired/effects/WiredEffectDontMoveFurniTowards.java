/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.items.interactions.wired.WiredSettings;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomLayout;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.threading.runnables.WiredCollissionRunnable;
import gnu.trove.set.hash.THashSet;
import java.sql.ResultSet;
import java.sql.SQLException;

public class WiredEffectDontMoveFurniTowards
extends InteractionWiredEffect {
    public static final WiredEffectType type = WiredEffectType.CHASE;
    private THashSet<HabboItem> items = new THashSet();

    public WiredEffectDontMoveFurniTowards(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredEffectDontMoveFurniTowards(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        this.items.removeIf(item -> Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId()).getHabboItem(item.getId()) == null);
        RoomLayout layout = room.getLayout();
        if (layout == null) {
            return true;
        }
        for (HabboItem item2 : this.items) {
            room.getHabbos().stream().filter(habbo -> habbo != null && item2.getOccupingTilesAround(layout).contains(habbo.getRoomUnit().getCurrentLocation())).forEach(habbo -> Emulator.getThreading().run(new WiredCollissionRunnable(habbo.getRoomUnit(), room, new Object[]{item2})));
        }
        return true;
    }

    @Override
    public String getWiredData() {
        StringBuilder wiredData = new StringBuilder(this.getDelay() + "\t");
        if (this.items != null && !this.items.isEmpty()) {
            for (HabboItem item : this.items) {
                wiredData.append(item.getId()).append(";");
            }
        }
        return wiredData.toString();
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException {
        this.items = new THashSet();
        String[] wiredData = set.getString("wired_data").split("\t");
        if (wiredData.length >= 1) {
            this.setDelay(Integer.parseInt(wiredData[0]));
        }
        if (wiredData.length == 2 && wiredData[1].contains(";")) {
            for (String s : wiredData[1].split(";")) {
                HabboItem item = room.getHabboItem(Integer.parseInt(s));
                if (item == null) continue;
                this.items.add(item);
            }
        }
    }

    @Override
    public void onPickUp() {
        this.items.clear();
        this.setDelay(0);
    }

    @Override
    public WiredEffectType getType() {
        return type;
    }

    @Override
    public void serializeWiredData(ServerMessage message, Room room) {
        THashSet<HabboItem> items = new THashSet<HabboItem>();
        for (HabboItem item : this.items) {
            if (item.getRoomId() == this.getRoomId() && Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId()).getHabboItem(item.getId()) != null) continue;
            items.add(item);
        }
        for (HabboItem item : items) {
            this.items.remove(item);
        }
        message.appendBoolean(Boolean.FALSE);
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
        message.appendInt(this.getDelay());
        message.appendInt(0);
    }

    @Override
    public boolean saveData(WiredSettings settings, GameClient gameClient) {
        this.items.clear();
        int count = settings.getFurniIds().length;
        for (int i = 0; i < count; ++i) {
            this.items.add(Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId()).getHabboItem(settings.getFurniIds()[i]));
        }
        this.setDelay(settings.getDelay());
        return true;
    }

    @Override
    protected long requiredCooldown() {
        return 495L;
    }
}

