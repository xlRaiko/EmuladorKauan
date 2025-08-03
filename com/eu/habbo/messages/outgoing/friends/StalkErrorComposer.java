/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.friends;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class StalkErrorComposer
extends MessageComposer {
    public static final int NOT_IN_FRIEND_LIST = 0;
    public static final int FRIEND_OFFLINE = 1;
    public static final int FRIEND_NOT_IN_ROOM = 2;
    public static final int FRIEND_BLOCKED_STALKING = 3;
    private final int errorCode;

    public StalkErrorComposer(int errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(3048);
        this.response.appendInt(this.errorCode);
        return this.response;
    }

    public int getErrorCode() {
        return this.errorCode;
    }
}

