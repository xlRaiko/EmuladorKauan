/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionCrackable;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.items.interactions.games.freeze.InteractionFreezeBlock;
import com.eu.habbo.habbohotel.items.interactions.games.freeze.InteractionFreezeTile;
import com.eu.habbo.habbohotel.items.interactions.wired.WiredSettings;
import com.eu.habbo.habbohotel.items.interactions.wired.effects.WiredEffectToggleFurni;
import com.eu.habbo.habbohotel.items.interactions.wired.extra.WiredAddonNoAnimation;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.rooms.items.FloorItemOnRollerComposer;
import com.eu.habbo.messages.outgoing.rooms.items.FloorItemUpdateComposer;
import gnu.trove.set.hash.THashSet;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WiredEffectTeleportFurniToHabbo
extends InteractionWiredEffect {
    private static final Logger LOGGER = LoggerFactory.getLogger(WiredEffectToggleFurni.class);
    public static final WiredEffectType type = WiredEffectType.TOGGLE_STATE;
    private final THashSet<HabboItem> items = new THashSet();

    public WiredEffectTeleportFurniToHabbo(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredEffectTeleportFurniToHabbo(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
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
        message.appendInt(this.getDelay());
        if (this.requiresTriggeringUser()) {
            ArrayList invalidTriggers = new ArrayList();
            room.getRoomSpecialTypes().getTriggers(this.getX(), this.getY()).forEach(object -> {
                if (!object.isTriggeredByRoomUnit()) {
                    invalidTriggers.add(object.getBaseItem().getSpriteId());
                }
                return true;
            });
            message.appendInt(invalidTriggers.size());
            for (Integer i : invalidTriggers) {
                message.appendInt(i);
            }
        } else {
            message.appendInt(0);
        }
    }

    @Override
    public boolean saveData(WiredSettings settings, GameClient gameClient) {
        int count = settings.getFurniIds().length;
        if (count > Emulator.getConfig().getInt("hotel.wired.furni.selection.count")) {
            return false;
        }
        this.items.clear();
        for (int i = 0; i < count; ++i) {
            HabboItem item = Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId()).getHabboItem(settings.getFurniIds()[i]);
            if (item instanceof InteractionFreezeBlock || item instanceof InteractionFreezeTile || item instanceof InteractionCrackable) continue;
            this.items.add(item);
        }
        this.setDelay(settings.getDelay());
        return true;
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        if (roomUnit == null) {
            return false;
        }
        Habbo habbo = room.getHabbo(roomUnit);
        if (habbo == null) {
            return false;
        }
        Object triggerItem = null;
        THashSet<RoomTile> tilesToUpdate = new THashSet<RoomTile>();
        THashSet<HabboItem> itemsToRemove = new THashSet<HabboItem>();
        for (HabboItem item : this.items) {
            if (item == null || item.getRoomId() == 0 || item instanceof InteractionFreezeBlock || item instanceof InteractionFreezeTile) {
                itemsToRemove.add(item);
                continue;
            }
            THashSet<RoomTile> occupiedTiles = room.getLayout().getTilesAt(room.getLayout().getTile(item.getX(), item.getY()), item.getBaseItem().getWidth(), item.getBaseItem().getLength(), item.getRotation());
            if (!room.getLayout().fitsOnMap(room.getLayout().getTile(roomUnit.getX(), roomUnit.getY()), item.getBaseItem().getWidth(), item.getBaseItem().getLength(), item.getRotation())) continue;
            double stackHeight = room.getStackHeight(roomUnit.getX(), roomUnit.getY(), false, item);
            double offsetZ = stackHeight - item.getZ();
            boolean isAddon = room.getRoomSpecialTypes().hasExtraType(this.getX(), this.getY(), WiredAddonNoAnimation.class);
            if (!isAddon) {
                room.sendComposer(new FloorItemOnRollerComposer(item, null, room.getLayout().getTile(roomUnit.getX(), roomUnit.getY()), offsetZ, room).compose());
            } else {
                RoomTile a = room.getLayout().getTile(item.getX(), item.getY());
                item.onMove(room, room.getLayout().getTile(item.getX(), item.getY()), a);
                item.setX(a.x);
                item.setY(a.y);
                item.setZ(offsetZ);
                item.needsUpdate(true);
                room.sendComposer(new FloorItemUpdateComposer(item).compose());
            }
            THashSet<RoomTile> newTiles = room.getLayout().getTilesAt(room.getLayout().getTile(roomUnit.getX(), roomUnit.getY()), item.getBaseItem().getWidth(), item.getBaseItem().getLength(), item.getRotation());
            item.needsUpdate(true);
            tilesToUpdate.addAll(occupiedTiles);
            tilesToUpdate.addAll(newTiles);
        }
        this.items.removeAll(itemsToRemove);
        room.updateTiles(tilesToUpdate);
        room.scheduledTasks.add(() -> {
            for (RoomTile roomTile : tilesToUpdate) {
                room.updateBotsAt(roomTile.x, roomTile.y);
                room.updateHabbosAt(roomTile.x, roomTile.y);
            }
        });
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
        this.items.clear();
        String[] wiredData = set.getString("wired_data").split("\t");
        if (wiredData.length >= 1) {
            this.setDelay(Integer.parseInt(wiredData[0]));
        }
        if (wiredData.length == 2 && wiredData[1].contains(";")) {
            for (String s : wiredData[1].split(";")) {
                HabboItem item = room.getHabboItem(Integer.parseInt(s));
                if (item instanceof InteractionFreezeBlock || item instanceof InteractionFreezeTile || item instanceof InteractionCrackable || item == null) continue;
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
}

