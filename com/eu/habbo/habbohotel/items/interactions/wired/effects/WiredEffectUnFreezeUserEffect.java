/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredTrigger;
import com.eu.habbo.habbohotel.items.interactions.wired.WiredSettings;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.messages.ServerMessage;
import gnu.trove.procedure.TObjectProcedure;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class WiredEffectUnFreezeUserEffect
extends InteractionWiredEffect {
    public static final WiredEffectType type = WiredEffectType.SHOW_MESSAGE;
    protected String message = "";

    public WiredEffectUnFreezeUserEffect(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredEffectUnFreezeUserEffect(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void serializeWiredData(ServerMessage message, Room room) {
        message.appendBoolean(false);
        message.appendInt(0);
        message.appendInt(0);
        message.appendInt(this.getBaseItem().getSpriteId());
        message.appendInt(this.getId());
        message.appendString(this.message);
        message.appendInt(0);
        message.appendInt(0);
        message.appendInt(WiredEffectUnFreezeUserEffect.type.code);
        message.appendInt(this.getDelay());
        if (this.requiresTriggeringUser()) {
            final ArrayList invalidTriggers = new ArrayList();
            room.getRoomSpecialTypes().getTriggers(this.getX(), this.getY()).forEach(new TObjectProcedure<InteractionWiredTrigger>(){
                final /* synthetic */ WiredEffectUnFreezeUserEffect this$0;
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
        int delay;
        String message = settings.getStringParam();
        if (gameClient.getHabbo() == null || !gameClient.getHabbo().hasPermission(Permission.ACC_SUPERWIRED)) {
            message = Emulator.getGameEnvironment().getWordFilter().filter(message, null);
            message = message.substring(0, Math.min(message.length(), Emulator.getConfig().getInt("hotel.wired.message.max_length", 100)));
        }
        if ((delay = settings.getDelay()) > Emulator.getConfig().getInt("hotel.wired.max_delay", 20)) {
            System.out.println("Delay too long");
        }
        this.message = message;
        this.setDelay(delay);
        return true;
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        Habbo habbo;
        if (roomUnit != null && (habbo = room.getHabbo(roomUnit)) != null) {
            habbo.getRoomUnit().setCanWalk(true);
            habbo.whisper(Emulator.getTexts().getValue("wired.effect.userfreeze.descongelado"), RoomChatMessageBubbles.ALERT);
        }
        return false;
    }

    @Override
    public String getWiredData() {
        return WiredHandler.getGsonBuilder().create().toJson(new JsonData(this.message, this.getDelay()));
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException {
        String wiredData = set.getString("wired_data");
        if (wiredData.startsWith("{")) {
            JsonData data = WiredHandler.getGsonBuilder().create().fromJson(wiredData, JsonData.class);
            this.setDelay(data.delay);
            this.message = data.message;
        } else {
            this.message = "";
            if (wiredData.split("\t").length >= 2) {
                super.setDelay(Integer.valueOf(wiredData.split("\t")[0]));
                this.message = wiredData.split("\t")[1];
            }
            this.needsUpdate(true);
        }
    }

    @Override
    public void onPickUp() {
        this.message = "";
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
        String message;
        int delay;

        public JsonData(String message, int delay) {
            this.message = message;
            this.delay = delay;
        }
    }
}

