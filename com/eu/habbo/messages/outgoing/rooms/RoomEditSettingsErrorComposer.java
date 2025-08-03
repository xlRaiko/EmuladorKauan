/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.rooms;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class RoomEditSettingsErrorComposer
extends MessageComposer {
    public static final int PASSWORD_REQUIRED = 5;
    public static final int ROOM_NAME_MISSING = 7;
    public static final int ROOM_NAME_BADWORDS = 8;
    public static final int ROOM_DESCRIPTION_BADWORDS = 10;
    public static final int ROOM_TAGS_BADWWORDS = 11;
    public static final int RESTRICTED_TAGS = 12;
    public static final int TAGS_TOO_LONG = 13;
    private final int roomId;
    private final int errorCode;
    private final String info;

    public RoomEditSettingsErrorComposer(int roomId, int errorCode, String info) {
        this.roomId = roomId;
        this.errorCode = errorCode;
        this.info = info;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(1555);
        this.response.appendInt(this.roomId);
        this.response.appendInt(this.errorCode);
        this.response.appendString(this.info);
        return this.response;
    }

    public int getRoomId() {
        return this.roomId;
    }

    public int getErrorCode() {
        return this.errorCode;
    }

    public String getInfo() {
        return this.info;
    }
}

