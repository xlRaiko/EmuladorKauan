/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.items.interactions.wired.extra;

public class WiredAutoPaga {
    private final int user_id;
    private final long timestamp;
    private volatile boolean wins;
    private final int room;

    public WiredAutoPaga(int id, long timestamp, boolean win, int room) {
        this.user_id = id;
        this.timestamp = timestamp;
        this.wins = win;
        this.room = room;
    }

    public int getUserID() {
        return this.user_id;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public boolean isWins() {
        return this.wins;
    }

    public int getRoom() {
        return this.room;
    }
}

