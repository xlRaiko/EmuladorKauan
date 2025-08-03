/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.items.interactions.wired.conditions;

import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredCondition;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredHighscore;
import com.eu.habbo.habbohotel.items.interactions.wired.WiredSettings;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.WiredConditionType;
import com.eu.habbo.habbohotel.wired.highscores.WiredHighscoreRow;
import com.eu.habbo.messages.ServerMessage;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WiredConditionEtcEtc
extends InteractionWiredCondition {
    public static final WiredConditionType type = WiredConditionType.NOT_ACTOR_IN_GROUP;

    public WiredConditionEtcEtc(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredConditionEtcEtc(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        if (roomUnit == null) {
            System.out.println("sabatneta");
            return false;
        }
        Habbo habbo = room.getHabbo(roomUnit);
        if (habbo == null) {
            System.out.println("sabatneta");
            return false;
        }
        try {
            String username = habbo.getHabboInfo().getUsername();
            HabboItem item = room.getFloorItems().stream().filter(habboItem -> {
                InteractionWiredHighscore highscore;
                return habboItem instanceof InteractionWiredHighscore && (highscore = (InteractionWiredHighscore)habboItem).getData() != null && !highscore.getData().isEmpty();
            }).findAny().orElse(null);
            if (item instanceof InteractionWiredHighscore) {
                InteractionWiredHighscore highscore = (InteractionWiredHighscore)item;
                List<WiredHighscoreRow> xd = this.obtenerValoresMaximos(highscore.getData());
                if ((xd.get(0).getUsers().contains(username) || xd.get(1).getUsers().contains(username)) && xd.get(0).getValue() == xd.get(1).getValue()) {
                    return true;
                }
                return xd.get(0).getUsers().contains(username);
            }
        }
        catch (Exception e) {
            System.out.println("ASDASDASDASD " + e.getMessage() + " asdasdasd " + String.valueOf(e.getCause()));
        }
        return false;
    }

    public List<WiredHighscoreRow> obtenerValoresMaximos(List<WiredHighscoreRow> data) {
        if (data == null || data.isEmpty()) {
            return new ArrayList<WiredHighscoreRow>();
        }
        ArrayList<WiredHighscoreRow> copiaOrdenada = new ArrayList<WiredHighscoreRow>(data);
        copiaOrdenada.sort((fila1, fila2) -> Integer.compare(fila2.getValue(), fila1.getValue()));
        return copiaOrdenada;
    }

    @Override
    public String getWiredData() {
        return "";
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException {
    }

    @Override
    public void onPickUp() {
    }

    @Override
    public WiredConditionType getType() {
        return type;
    }

    @Override
    public void serializeWiredData(ServerMessage message, Room room) {
        message.appendBoolean(false);
        message.appendInt(5);
        message.appendInt(0);
        message.appendInt(this.getBaseItem().getSpriteId());
        message.appendInt(this.getId());
        message.appendString("");
        message.appendInt(0);
        message.appendInt(0);
        message.appendInt(this.getType().code);
        message.appendInt(0);
        message.appendInt(0);
    }

    @Override
    public boolean saveData(WiredSettings settings) {
        return true;
    }
}

