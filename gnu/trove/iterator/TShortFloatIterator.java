/*
 * Decompiled with CFR 0.152.
 */
package gnu.trove.iterator;

import gnu.trove.iterator.TAdvancingIterator;

public interface TShortFloatIterator
extends TAdvancingIterator {
    public short key();

    public float value();

    public float setValue(float var1);
}

