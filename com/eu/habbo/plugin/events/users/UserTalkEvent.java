/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.plugin.events.users;

import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatType;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.plugin.events.users.UserEvent;

public class UserTalkEvent
extends UserEvent {
    public final RoomChatMessage chatMessage;
    public final RoomChatType chatType;

    public UserTalkEvent(Habbo habbo, RoomChatMessage chatMessage, RoomChatType chatType) {
        super(habbo);
        this.chatMessage = chatMessage;
        this.chatType = chatType;
    }
}

