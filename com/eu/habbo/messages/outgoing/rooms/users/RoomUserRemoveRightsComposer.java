/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.rooms.users;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class RoomUserRemoveRightsComposer
extends MessageComposer {
    private final Room room;
    private final int habboId;

    public RoomUserRemoveRightsComposer(Room room, int habboId) {
        this.room = room;
        this.habboId = habboId;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(84);
        this.response.appendInt(this.room.getId());
        this.response.appendInt(this.habboId);
        return this.response;
    }

    public Room getRoom() {
        return this.room;
    }

    public int getHabboId() {
        return this.habboId;
    }
}

