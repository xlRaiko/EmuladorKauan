/*
 * Decompiled with CFR 0.152.
 */
package gnu.trove.iterator;

import gnu.trove.iterator.TAdvancingIterator;

public interface TFloatByteIterator
extends TAdvancingIterator {
    public float key();

    public byte value();

    public byte setValue(byte var1);
}

