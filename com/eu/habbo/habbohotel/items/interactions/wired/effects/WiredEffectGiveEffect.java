/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.wired.effects.WiredEffectWhisper;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.rooms.RoomUnitType;
import com.eu.habbo.habbohotel.users.Habbo;
import java.sql.ResultSet;
import java.sql.SQLException;

public class WiredEffectGiveEffect
extends WiredEffectWhisper {
    public WiredEffectGiveEffect(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredEffectGiveEffect(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        int effectId;
        try {
            effectId = Integer.parseInt(this.message);
        }
        catch (Exception e) {
            return false;
        }
        if (effectId >= 0) {
            Habbo habbo;
            if (roomUnit.getRoomUnitType() == RoomUnitType.USER && (habbo = room.getHabbo(roomUnit)) != null && Emulator.getGameEnvironment().getPermissionsManager().isEffectBlocked(effectId, habbo.getHabboInfo().getRank().getId())) {
                habbo.whisper(Emulator.getTexts().getValue("commands.error.cmd_enable.not_allowed"), RoomChatMessageBubbles.ALERT);
                return true;
            }
            room.giveEffect(roomUnit, effectId, Integer.MAX_VALUE);
            return true;
        }
        return false;
    }
}

