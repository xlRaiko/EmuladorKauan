/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.items.interactions.wired.extra;

import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredExtra;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class WiredExtraUnseen
extends InteractionWiredExtra {
    List<InteractionWiredEffect> unseenEffects = new ArrayList<InteractionWiredEffect>();

    public WiredExtraUnseen(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredExtraUnseen(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        return false;
    }

    @Override
    public String getWiredData() {
        return null;
    }

    @Override
    public void serializeWiredData(ServerMessage message, Room room) {
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException {
    }

    @Override
    public void onPickUp() {
        this.unseenEffects.clear();
    }

    @Override
    public void onWalk(RoomUnit roomUnit, Room room, Object[] objects) throws Exception {
    }

    @Override
    public void onMove(Room room, RoomTile oldLocation, RoomTile newLocation) {
        super.onMove(room, oldLocation, newLocation);
        this.unseenEffects.clear();
    }

    public InteractionWiredEffect getUnseenEffect(List<InteractionWiredEffect> effects) {
        InteractionWiredEffect effect;
        if (this.unseenEffects.isEmpty()) {
            this.unseenEffects.addAll(effects);
            this.unseenEffects.sort(Comparator.comparing(HabboItem::getZ));
        }
        try {
            effect = this.unseenEffects.get(0);
            this.unseenEffects.remove(0);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        return effect;
    }
}

