/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.wired.customs;

public class WiredMatchFurniSetting {
    public final int itemId;
    public final String state;
    public final int rotation;
    public final int x;
    public final int y;
    public final double z;
    public final boolean useZ;

    public WiredMatchFurniSetting(int itemId, String state, int rotation, int x, int y) {
        this.itemId = itemId;
        this.state = state.replace("\t\t\t", " ").replace("~", "-");
        this.rotation = rotation;
        this.x = x;
        this.y = y;
        this.z = -1.0;
        this.useZ = false;
    }

    public WiredMatchFurniSetting(int itemId, String state, int rotation, int x, int y, double z) {
        this.itemId = itemId;
        this.state = state.replace("\t\t\t", " ").replace("~", "-");
        this.rotation = rotation;
        this.x = x;
        this.y = y;
        this.z = z;
        this.useZ = true;
    }

    public String toString() {
        return this.toString(true);
    }

    public String toString(boolean includeState) {
        if (this.useZ && this.z >= 0.0) {
            return this.itemId + "-" + (this.state.isEmpty() || !includeState ? " " : this.state.replace("-", "~")) + "-" + this.rotation + "-" + this.x + "-" + this.y + "-" + String.format("%f", this.z).replace(',', '.');
        }
        return this.itemId + "-" + (this.state.isEmpty() || !includeState ? " " : this.state.replace("-", "~")) + "-" + this.rotation + "-" + this.x + "-" + this.y;
    }
}

