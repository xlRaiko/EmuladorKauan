/*
 * Decompiled with CFR 0.152.
 */
package gnu.trove.iterator;

import gnu.trove.iterator.TAdvancingIterator;

public interface TIntByteIterator
extends TAdvancingIterator {
    public int key();

    public byte value();

    public byte setValue(byte var1);
}

