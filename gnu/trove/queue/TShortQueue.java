/*
 * Decompiled with CFR 0.152.
 */
package gnu.trove.queue;

import gnu.trove.TShortCollection;

public interface TShortQueue
extends TShortCollection {
    public short element();

    public boolean offer(short var1);

    public short peek();

    public short poll();
}

