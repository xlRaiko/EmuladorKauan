/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util;

public interface ResourceLeakTracker<T> {
    public void record();

    public void record(Object var1);

    public boolean close(T var1);
}

