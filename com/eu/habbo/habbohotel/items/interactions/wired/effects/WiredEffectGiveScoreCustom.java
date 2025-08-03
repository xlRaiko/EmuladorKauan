/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredHighscore;
import com.eu.habbo.habbohotel.items.interactions.wired.WiredSettings;
import com.eu.habbo.habbohotel.items.interactions.wired.effects.WiredEffectGiveScore;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.highscores.WiredHighscoreDataEntry;
import com.eu.habbo.habbohotel.wired.highscores.WiredHighscoreManager;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.incoming.wired.WiredSaveException;
import gnu.trove.impl.hash.THashIterator;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class WiredEffectGiveScoreCustom
extends InteractionWiredEffect {
    public static final WiredEffectType type = WiredEffectType.LEAVE_TEAM;
    protected String message = "0";

    public WiredEffectGiveScoreCustom(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredEffectGiveScoreCustom(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void serializeWiredData(ServerMessage message, Room room) {
        message.appendBoolean(Boolean.FALSE);
        message.appendInt(0);
        message.appendInt(0);
        message.appendInt(this.getBaseItem().getSpriteId());
        message.appendInt(this.getId());
        message.appendString(this.message);
        message.appendInt(0);
        message.appendInt(0);
        message.appendInt(WiredEffectGiveScoreCustom.type.code);
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
    public boolean saveData(WiredSettings settings, GameClient gameClient) throws WiredSaveException {
        int delay = settings.getDelay();
        if (delay > Emulator.getConfig().getInt("hotel.wired.max_delay", 20)) {
            throw new WiredSaveException("Delay too long");
        }
        this.setDelay(delay);
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
        if (this.message.length() > 0) {
            int score = 1;
            HashMap data = new HashMap();
            try {
                HashMap allRanks;
                WiredHighscoreManager wiredHighscoreManager = Emulator.getGameEnvironment().getItemManager().getHighscoreManager();
                Field f = wiredHighscoreManager.getClass().getDeclaredField("data");
                f.setAccessible(true);
                data = allRanks = (HashMap)f.get(wiredHighscoreManager);
            }
            catch (Exception wiredHighscoreManager) {
                // empty catch block
            }
            List wiredHighscoreDataEntries = new ArrayList();
            int itemId = 0;
            Iterator tObjectHashIterator1 = room.getRoomSpecialTypes().getItemsOfType(InteractionWiredHighscore.class).iterator();
            if (((THashIterator)tObjectHashIterator1).hasNext()) {
                HabboItem item = (HabboItem)((THashIterator)tObjectHashIterator1).next();
                if (data.containsKey(item.getId())) {
                    wiredHighscoreDataEntries = (List)data.get(item.getId());
                }
                itemId = item.getId();
            }
            if (itemId == 0) {
                return false;
            }
            boolean hasEntry = false;
            ArrayList<WiredHighscoreDataEntry> newWiredHighscoreDataEntries = new ArrayList<WiredHighscoreDataEntry>();
            if (wiredHighscoreDataEntries.size() > 0) {
                for (WiredHighscoreDataEntry wiredHighscoreDataEntry : wiredHighscoreDataEntries) {
                    if (wiredHighscoreDataEntry.getUserIds().contains(habbo.getHabboInfo().getId())) {
                        hasEntry = true;
                        score += wiredHighscoreDataEntry.getScore();
                        continue;
                    }
                    newWiredHighscoreDataEntries.add(wiredHighscoreDataEntry);
                }
            }
            ArrayList<Integer> userIds = new ArrayList<Integer>();
            userIds.add(habbo.getHabboInfo().getId());
            WiredHighscoreDataEntry entry = new WiredHighscoreDataEntry(itemId, userIds, score, true, Emulator.getIntUnixTimestamp());
            newWiredHighscoreDataEntries.add(entry);
            try {
                WiredHighscoreManager wiredHighscoreManager = Emulator.getGameEnvironment().getItemManager().getHighscoreManager();
                Field f = wiredHighscoreManager.getClass().getDeclaredField("data");
                f.setAccessible(true);
                HashMap allRanks = (HashMap)f.get(wiredHighscoreManager);
                allRanks.put(itemId, newWiredHighscoreDataEntries);
            }
            catch (Exception wiredHighscoreManager) {
                // empty catch block
            }
            if (!hasEntry) {
                try {
                    connection = Emulator.getDatabase().getDataSource().getConnection();
                    try {
                        statement = connection.prepareStatement("INSERT INTO `items_highscore_data` (`item_id`, `user_ids`, `score`, `is_win`, `timestamp`) VALUES (?, ?, ?, ?, ?)");
                        try {
                            statement.setInt(1, itemId);
                            statement.setString(2, String.valueOf(habbo.getHabboInfo().getId()));
                            statement.setInt(3, score);
                            statement.setInt(4, 1);
                            statement.setInt(5, Emulator.getIntUnixTimestamp());
                            statement.executeUpdate();
                        }
                        finally {
                            if (statement != null) {
                                statement.close();
                            }
                        }
                    }
                    finally {
                        if (connection != null) {
                            connection.close();
                        }
                    }
                }
                catch (SQLException e) {
                    Emulator.getLogging().logSQLException(e);
                }
            } else {
                try {
                    connection = Emulator.getDatabase().getDataSource().getConnection();
                    try {
                        statement = connection.prepareStatement("UPDATE `items_highscore_data` SET `score` = ?, `timestamp` = ? WHERE `item_id` = ? AND `user_ids` = ? LIMIT 1");
                        try {
                            statement.setInt(1, score);
                            statement.setInt(2, Emulator.getIntUnixTimestamp());
                            statement.setInt(3, itemId);
                            statement.setString(4, String.valueOf(habbo.getHabboInfo().getId()));
                            statement.executeUpdate();
                        }
                        finally {
                            if (statement != null) {
                                statement.close();
                            }
                        }
                    }
                    finally {
                        if (connection != null) {
                            connection.close();
                        }
                    }
                }
                catch (SQLException e) {
                    Emulator.getLogging().logSQLException(e);
                }
            }
            Iterator tObjectHashIterator2 = room.getRoomSpecialTypes().getItemsOfType(InteractionWiredHighscore.class).iterator();
            if (((THashIterator)tObjectHashIterator2).hasNext()) {
                HabboItem item = (HabboItem)((THashIterator)tObjectHashIterator2).next();
                ((InteractionWiredHighscore)item).reloadData();
                room.updateItem(item);
            }
            return true;
        }
        return false;
    }

    @Override
    public String getWiredData() {
        return WiredHandler.getGsonBuilder().create().toJson(new JsonData(this.getDelay()));
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException {
        String wiredData = set.getString("wired_data");
        if (wiredData.startsWith("{")) {
            WiredEffectGiveScore.JsonData data = WiredHandler.getGsonBuilder().create().fromJson(wiredData, WiredEffectGiveScore.JsonData.class);
            this.setDelay(data.delay);
        } else {
            this.setDelay(0);
        }
    }

    @Override
    public void onPickUp() {
        this.setDelay(0);
    }

    @Override
    public WiredEffectType getType() {
        return type;
    }

    @Override
    public boolean requiresTriggeringUser() {
        return true;
    }

    static class JsonData {
        int delay;

        public JsonData(int delay) {
            this.delay = delay;
        }
    }
}

