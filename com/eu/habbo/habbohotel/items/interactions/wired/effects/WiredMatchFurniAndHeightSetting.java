/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.items.interactions.wired.effects;

public class WiredMatchFurniAndHeightSetting {
    public final int itemId;
    public final String state;
    public final int rotation;
    public final int x;
    public final int y;
    public final double z;

    public WiredMatchFurniAndHeightSetting(int itemId, String state, int rotation, int x, int y, double z) {
        this.itemId = itemId;
        this.state = state.replace("\t\t\t", " ");
        this.rotation = rotation;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public String toString() {
        return this.toString(true);
    }

    public String toString(boolean includeState) {
        return this.itemId + "-" + (!this.state.isEmpty() && includeState ? this.state : " ") + "-" + this.rotation + "-" + this.x + "-" + this.y + "-" + this.z;
    }
}

