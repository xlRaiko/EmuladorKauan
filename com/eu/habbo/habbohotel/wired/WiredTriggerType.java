/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.wired;

public enum WiredTriggerType {
    SAY_SOMETHING(0),
    WALKS_ON_FURNI(1),
    ANTI_AFK(1),
    WALKS_OFF_FURNI(2),
    AT_GIVEN_TIME(3),
    STATE_CHANGED(4),
    PERIODICALLY(6),
    ENTER_ROOM(7),
    TO_WALK(7),
    EXIT_ROOM(7),
    GAME_STARTS(8),
    GAME_ENDS(9),
    SCORE_ACHIEVED(10),
    COLLISION(11),
    PERIODICALLY_LONG(12),
    BOT_REACHED_STF(13),
    BOT_REACHED_AVTR(14),
    SAY_COMMAND(0),
    TAG(7),
    CLICK_TO_USER(7),
    IDLES(11),
    UNIDLES(11),
    CUSTOM(13),
    STARTS_DANCING(11),
    STOPS_DANCING(11),
    CLICK_FURNI(4),
    CLICK_TILE(4),
    STATE_FURNI(19);

    public final int code;

    private WiredTriggerType(int code) {
        this.code = code;
    }
}

