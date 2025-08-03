/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.items.interactions.wired.conditions;

import com.eu.habbo.habbohotel.games.Game;
import com.eu.habbo.habbohotel.games.GameTeamColors;
import com.eu.habbo.habbohotel.games.wired.WiredGame;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.wired.conditions.WiredConditionSuperWired;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import java.sql.ResultSet;
import java.sql.SQLException;

public class WiredConditionTeamGreenCountN
extends WiredConditionSuperWired {
    public WiredConditionTeamGreenCountN(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredConditionTeamGreenCountN(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        int xd;
        Game TeamManager = room.getGame(WiredGame.class);
        if (TeamManager == null) {
            return false;
        }
        try {
            xd = Integer.parseInt(this.key);
        }
        catch (NumberFormatException ignored) {
            return false;
        }
        return TeamManager.getTeam(GameTeamColors.GREEN).getMembers().size() <= xd;
    }
}

