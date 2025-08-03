/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.incoming.customs;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import com.eu.habbo.messages.incoming.MessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClickFurnitureEvent
extends MessageHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClickFurnitureEvent.class);

    @Override
    public void handle() throws Exception {
        try {
            Room room = this.client.getHabbo().getRoomUnit().getRoom();
            if (room == null) {
                return;
            }
            int itemId = this.packet.readInt();
            HabboItem item = room.getHabboItem(itemId);
            if (item == null) {
                return;
            }
            WiredHandler.handle(WiredTriggerType.CLICK_FURNI, this.client.getHabbo().getRoomUnit(), room, new Object[]{item});
        }
        catch (Exception e) {
            LOGGER.error("Caught exception", e);
        }
    }
}

