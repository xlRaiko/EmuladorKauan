/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http2;

import io.netty.handler.codec.http2.Http2Headers;
import io.netty.handler.codec.http2.Http2StreamFrame;

public interface Http2HeadersFrame
extends Http2StreamFrame {
    public Http2Headers headers();

    public int padding();

    public boolean isEndStream();
}

