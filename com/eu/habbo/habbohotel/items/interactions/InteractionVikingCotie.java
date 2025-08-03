/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionDefault;
import com.eu.habbo.habbohotel.rooms.Room;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionVikingCotie
extends InteractionDefault {
    public InteractionVikingCotie(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public InteractionVikingCotie(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) throws Exception {
        int state;
        if (this.getExtradata().isEmpty()) {
            this.setExtradata("0");
        }
        if (client != null && client.getHabbo().getHabboInfo().getId() == this.getUserId() && (client.getHabbo().getRoomUnit().getEffectId() == 172 || client.getHabbo().getRoomUnit().getEffectId() == 173) && (state = Integer.valueOf(this.getExtradata()).intValue()) < 5) {
            this.setExtradata("" + ++state);
            room.updateItem(this);
            if (state == 5) {
                AchievementManager.progressAchievement(client.getHabbo(), Emulator.getGameEnvironment().getAchievementManager().getAchievement("ViciousViking"));
            }
        }
    }

    @Override
    public boolean allowWiredResetState() {
        return false;
    }
}

