/*
 * Decompiled with CFR 0.152.
 */
package gnu.trove.iterator;

import gnu.trove.iterator.TAdvancingIterator;

public interface TByteDoubleIterator
extends TAdvancingIterator {
    public byte key();

    public double value();

    public double setValue(double var1);
}

