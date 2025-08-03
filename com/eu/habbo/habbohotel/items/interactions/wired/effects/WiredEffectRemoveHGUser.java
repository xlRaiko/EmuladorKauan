/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredHighscore;
import com.eu.habbo.habbohotel.items.interactions.wired.effects.WiredEffectWhisper;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.highscores.WiredHighscoreManager;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class WiredEffectRemoveHGUser
extends WiredEffectWhisper {
    public WiredEffectRemoveHGUser(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredEffectRemoveHGUser(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        Habbo habbo = room.getHabbo(roomUnit);
        if (habbo == null) {
            return false;
        }
        HashMap data = new HashMap();
        try {
            HashMap allRanks;
            WiredHighscoreManager wiredHighscoreManager = Emulator.getGameEnvironment().getItemManager().getHighscoreManager();
            Field f = wiredHighscoreManager.getClass().getDeclaredField("data");
            f.setAccessible(true);
            data = allRanks = (HashMap)f.get(wiredHighscoreManager);
        }
        catch (Exception exception) {
            // empty catch block
        }
        for (HabboItem item : room.getRoomSpecialTypes().getItemsOfType(InteractionWiredHighscore.class)) {
            this.removeData(item, habbo.getHabboInfo().getId());
            data.remove(item.getId());
            ((InteractionWiredHighscore)item).reloadData();
            room.updateItem(item);
        }
        return true;
    }

    public void removeData(HabboItem item, int user_id) {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM `items_highscore_data` WHERE item_id = ? AND user_ids = ?");){
            statement.setInt(1, item.getId());
            statement.setInt(2, user_id);
            statement.executeUpdate();
        }
        catch (SQLException e) {
            Emulator.getLogging().logSQLException(e);
        }
    }
}

