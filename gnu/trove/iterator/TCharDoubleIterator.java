/*
 * Decompiled with CFR 0.152.
 */
package gnu.trove.iterator;

import gnu.trove.iterator.TAdvancingIterator;

public interface TCharDoubleIterator
extends TAdvancingIterator {
    public char key();

    public double value();

    public double setValue(double var1);
}

