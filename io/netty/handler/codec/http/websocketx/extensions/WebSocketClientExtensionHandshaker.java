/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http.websocketx.extensions;

import io.netty.handler.codec.http.websocketx.extensions.WebSocketClientExtension;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionData;

public interface WebSocketClientExtensionHandshaker {
    public WebSocketExtensionData newRequestData();

    public WebSocketClientExtension handshakeExtension(WebSocketExtensionData var1);
}

