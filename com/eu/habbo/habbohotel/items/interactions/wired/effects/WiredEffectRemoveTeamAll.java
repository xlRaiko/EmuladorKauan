/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.habbohotel.games.Game;
import com.eu.habbo.habbohotel.games.wired.WiredGame;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.wired.effects.WiredEffectLeaveTeam;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

public class WiredEffectRemoveTeamAll
extends WiredEffectLeaveTeam {
    public WiredEffectRemoveTeamAll(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredEffectRemoveTeamAll(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        Collection<Habbo> collection = room.getHabbos();
        synchronized (collection) {
            for (Habbo habbos : room.getHabbos()) {
                if (habbos == null || habbos.getHabboInfo().getCurrentGame() == null) continue;
                Game game = room.getGame(habbos.getHabboInfo().getCurrentGame());
                if (game == null) {
                    game = room.getGameOrCreate(WiredGame.class);
                }
                if (game == null) continue;
                room.giveEffectByPass(habbos, 0, -1);
                game.removeHabbo(habbos);
            }
        }
        return true;
    }
}

