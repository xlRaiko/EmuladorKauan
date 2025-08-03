/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.rooms.pathfinding.impl;

public class Rotation {
    protected static final short[][] DIRECTIONS = new short[][]{{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
    protected static final short[][] DIAGONAL_DIRECTIONS = new short[][]{{1, 1}, {-1, -1}, {-1, 1}, {1, -1}};

    private Rotation() {
        throw new IllegalStateException("Utility class");
    }
}

