/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.ssl;

import io.netty.handler.ssl.ReferenceCountedOpenSslEngine;

interface OpenSslEngineMap {
    public ReferenceCountedOpenSslEngine remove(long var1);

    public void add(ReferenceCountedOpenSslEngine var1);

    public ReferenceCountedOpenSslEngine get(long var1);
}

