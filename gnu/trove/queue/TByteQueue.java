/*
 * Decompiled with CFR 0.152.
 */
package gnu.trove.queue;

import gnu.trove.TByteCollection;

public interface TByteQueue
extends TByteCollection {
    public byte element();

    public boolean offer(byte var1);

    public byte peek();

    public byte poll();
}

