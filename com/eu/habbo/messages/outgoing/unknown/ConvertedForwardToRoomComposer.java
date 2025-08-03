/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class ConvertedForwardToRoomComposer
extends MessageComposer {
    private final String unknownString1;
    private final int unknownInt1;

    public ConvertedForwardToRoomComposer(String unknownString1, int unknownInt1) {
        this.unknownString1 = unknownString1;
        this.unknownInt1 = unknownInt1;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(1331);
        this.response.appendString(this.unknownString1);
        this.response.appendInt(this.unknownInt1);
        return this.response;
    }

    public String getUnknownString1() {
        return this.unknownString1;
    }

    public int getUnknownInt1() {
        return this.unknownInt1;
    }
}

