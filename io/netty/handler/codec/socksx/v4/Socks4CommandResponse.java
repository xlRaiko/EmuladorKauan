/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.socksx.v4;

import io.netty.handler.codec.socksx.v4.Socks4CommandStatus;
import io.netty.handler.codec.socksx.v4.Socks4Message;

public interface Socks4CommandResponse
extends Socks4Message {
    public Socks4CommandStatus status();

    public String dstAddr();

    public int dstPort();
}

