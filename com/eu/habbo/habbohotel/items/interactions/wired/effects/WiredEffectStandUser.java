/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredTrigger;
import com.eu.habbo.habbohotel.items.interactions.wired.WiredSettings;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.messages.ServerMessage;
import gnu.trove.procedure.TObjectProcedure;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class WiredEffectStandUser
extends InteractionWiredEffect {
    public static final WiredEffectType type = WiredEffectType.RESET_TIMERS;
    private int delay = 0;

    public WiredEffectStandUser(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredEffectStandUser(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void serializeWiredData(ServerMessage message, Room room) {
        message.appendBoolean(false);
        message.appendInt(5);
        message.appendInt(0);
        message.appendInt(this.getBaseItem().getSpriteId());
        message.appendInt(this.getId());
        message.appendString("");
        message.appendInt(1);
        message.appendInt(this.getDelay());
        message.appendInt(0);
        message.appendInt(this.getType().code);
        message.appendInt(this.getDelay());
        if (this.requiresTriggeringUser()) {
            final ArrayList invalidTriggers = new ArrayList();
            room.getRoomSpecialTypes().getTriggers(this.getX(), this.getY()).forEach(new TObjectProcedure<InteractionWiredTrigger>(){
                final /* synthetic */ WiredEffectStandUser this$0;
                {
                    this.this$0 = this$0;
                }

                @Override
                public boolean execute(InteractionWiredTrigger object) {
                    if (!object.isTriggeredByRoomUnit()) {
                        invalidTriggers.add(object.getBaseItem().getSpriteId());
                    }
                    return true;
                }
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
        this.delay = settings.getDelay();
        this.setDelay(this.delay);
        return true;
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        Habbo habbo = room.getHabbo(roomUnit);
        if (habbo.getHabboInfo().getRiding() == null) {
            habbo.getHabboInfo().getCurrentRoom().makeStand(habbo);
        }
        return true;
    }

    @Override
    public String getWiredData() {
        return WiredHandler.getGsonBuilder().create().toJson(new JsonData(this.delay));
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException {
        String wiredData = set.getString("wired_data");
        if (wiredData.startsWith("{")) {
            JsonData data = WiredHandler.getGsonBuilder().create().fromJson(wiredData, JsonData.class);
            this.delay = data.delay;
        } else {
            try {
                if (!wiredData.equals("")) {
                    this.delay = Integer.parseInt(wiredData);
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        this.setDelay(this.delay);
    }

    @Override
    public void onPickUp() {
        this.delay = 0;
        this.setDelay(0);
    }

    @Override
    public WiredEffectType getType() {
        return type;
    }

    static class JsonData {
        int delay;

        public JsonData(int delay) {
            this.delay = delay;
        }
    }
}

