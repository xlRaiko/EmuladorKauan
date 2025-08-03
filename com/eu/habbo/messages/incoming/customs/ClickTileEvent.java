/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.incoming.customs;

import com.eu.habbo.habbohotel.items.interactions.InteractionTileClick;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import com.eu.habbo.messages.incoming.MessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClickTileEvent
extends MessageHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClickTileEvent.class);

    @Override
    public void handle() throws Exception {
        try {
            Room room = this.client.getHabbo().getRoomUnit().getRoom();
            if (room == null || room.getLayout() == null) {
                return;
            }
            int x = this.packet.readInt();
            int y = this.packet.readInt();
            RoomTile tile = room.getLayout().getTile((short)x, (short)y);
            if (tile == null) {
                return;
            }
            room.getItemsAt(tile).stream().filter(items -> items instanceof InteractionTileClick).findAny().ifPresent(item -> WiredHandler.handle(WiredTriggerType.CLICK_TILE, this.client.getHabbo().getRoomUnit(), room, new Object[]{item}));
        }
        catch (Exception e) {
            LOGGER.error("Caught exception", e);
        }
    }
}

