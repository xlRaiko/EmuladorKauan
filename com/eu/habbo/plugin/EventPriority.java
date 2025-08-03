/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.plugin;

public enum EventPriority {
    LOWEST(0),
    LOW(1),
    NORMAL(2),
    HIGH(3),
    HIGHEST(4),
    MONITOR(5);

    private final int slot;

    private EventPriority(int slot) {
        this.slot = slot;
    }

    public int getSlot() {
        return this.slot;
    }
}

