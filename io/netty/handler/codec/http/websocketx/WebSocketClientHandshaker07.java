/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.WebSocket07FrameDecoder;
import io.netty.handler.codec.http.websocketx.WebSocket07FrameEncoder;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketFrameDecoder;
import io.netty.handler.codec.http.websocketx.WebSocketFrameEncoder;
import io.netty.handler.codec.http.websocketx.WebSocketHandshakeException;
import io.netty.handler.codec.http.websocketx.WebSocketUtil;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.URI;

public class WebSocketClientHandshaker07
extends WebSocketClientHandshaker {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(WebSocketClientHandshaker07.class);
    public static final String MAGIC_GUID = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
    private String expectedChallengeResponseString;
    private final boolean allowExtensions;
    private final boolean performMasking;
    private final boolean allowMaskMismatch;

    public WebSocketClientHandshaker07(URI webSocketURL, WebSocketVersion version, String subprotocol, boolean allowExtensions, HttpHeaders customHeaders, int maxFramePayloadLength) {
        this(webSocketURL, version, subprotocol, allowExtensions, customHeaders, maxFramePayloadLength, true, false);
    }

    public WebSocketClientHandshaker07(URI webSocketURL, WebSocketVersion version, String subprotocol, boolean allowExtensions, HttpHeaders customHeaders, int maxFramePayloadLength, boolean performMasking, boolean allowMaskMismatch) {
        this(webSocketURL, version, subprotocol, allowExtensions, customHeaders, maxFramePayloadLength, performMasking, allowMaskMismatch, 10000L);
    }

    public WebSocketClientHandshaker07(URI webSocketURL, WebSocketVersion version, String subprotocol, boolean allowExtensions, HttpHeaders customHeaders, int maxFramePayloadLength, boolean performMasking, boolean allowMaskMismatch, long forceCloseTimeoutMillis) {
        this(webSocketURL, version, subprotocol, allowExtensions, customHeaders, maxFramePayloadLength, performMasking, allowMaskMismatch, forceCloseTimeoutMillis, false);
    }

    WebSocketClientHandshaker07(URI webSocketURL, WebSocketVersion version, String subprotocol, boolean allowExtensions, HttpHeaders customHeaders, int maxFramePayloadLength, boolean performMasking, boolean allowMaskMismatch, long forceCloseTimeoutMillis, boolean absoluteUpgradeUrl) {
        super(webSocketURL, version, subprotocol, customHeaders, maxFramePayloadLength, forceCloseTimeoutMillis, absoluteUpgradeUrl);
        this.allowExtensions = allowExtensions;
        this.performMasking = performMasking;
        this.allowMaskMismatch = allowMaskMismatch;
    }

    @Override
    protected FullHttpRequest newHandshakeRequest() {
        String expectedSubprotocol;
        URI wsURL = this.uri();
        byte[] nonce = WebSocketUtil.randomBytes(16);
        String key = WebSocketUtil.base64(nonce);
        String acceptSeed = key + MAGIC_GUID;
        byte[] sha1 = WebSocketUtil.sha1(acceptSeed.getBytes(CharsetUtil.US_ASCII));
        this.expectedChallengeResponseString = WebSocketUtil.base64(sha1);
        if (logger.isDebugEnabled()) {
            logger.debug("WebSocket version 07 client handshake key: {}, expected response: {}", (Object)key, (Object)this.expectedChallengeResponseString);
        }
        DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, this.upgradeUrl(wsURL), Unpooled.EMPTY_BUFFER);
        HttpHeaders headers = request.headers();
        if (this.customHeaders != null) {
            headers.add(this.customHeaders);
            if (!headers.contains(HttpHeaderNames.HOST)) {
                headers.set((CharSequence)HttpHeaderNames.HOST, (Object)WebSocketClientHandshaker07.websocketHostValue(wsURL));
            }
        } else {
            headers.set((CharSequence)HttpHeaderNames.HOST, (Object)WebSocketClientHandshaker07.websocketHostValue(wsURL));
        }
        headers.set((CharSequence)HttpHeaderNames.UPGRADE, (Object)HttpHeaderValues.WEBSOCKET).set((CharSequence)HttpHeaderNames.CONNECTION, (Object)HttpHeaderValues.UPGRADE).set((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_KEY, (Object)key);
        if (!headers.contains(HttpHeaderNames.SEC_WEBSOCKET_ORIGIN)) {
            headers.set((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_ORIGIN, (Object)WebSocketClientHandshaker07.websocketOriginValue(wsURL));
        }
        if ((expectedSubprotocol = this.expectedSubprotocol()) != null && !expectedSubprotocol.isEmpty()) {
            headers.set((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_PROTOCOL, (Object)expectedSubprotocol);
        }
        headers.set((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_VERSION, (Object)this.version().toAsciiString());
        return request;
    }

    @Override
    protected void verify(FullHttpResponse response) {
        HttpResponseStatus status = HttpResponseStatus.SWITCHING_PROTOCOLS;
        HttpHeaders headers = response.headers();
        if (!response.status().equals(status)) {
            throw new WebSocketHandshakeException("Invalid handshake response getStatus: " + response.status());
        }
        String upgrade = headers.get(HttpHeaderNames.UPGRADE);
        if (!HttpHeaderValues.WEBSOCKET.contentEqualsIgnoreCase(upgrade)) {
            throw new WebSocketHandshakeException("Invalid handshake response upgrade: " + upgrade);
        }
        if (!headers.containsValue(HttpHeaderNames.CONNECTION, HttpHeaderValues.UPGRADE, true)) {
            throw new WebSocketHandshakeException("Invalid handshake response connection: " + headers.get(HttpHeaderNames.CONNECTION));
        }
        String accept = headers.get(HttpHeaderNames.SEC_WEBSOCKET_ACCEPT);
        if (accept == null || !accept.equals(this.expectedChallengeResponseString)) {
            throw new WebSocketHandshakeException(String.format("Invalid challenge. Actual: %s. Expected: %s", accept, this.expectedChallengeResponseString));
        }
    }

    @Override
    protected WebSocketFrameDecoder newWebsocketDecoder() {
        return new WebSocket07FrameDecoder(false, this.allowExtensions, this.maxFramePayloadLength(), this.allowMaskMismatch);
    }

    @Override
    protected WebSocketFrameEncoder newWebSocketEncoder() {
        return new WebSocket07FrameEncoder(this.performMasking);
    }

    @Override
    public WebSocketClientHandshaker07 setForceCloseTimeoutMillis(long forceCloseTimeoutMillis) {
        super.setForceCloseTimeoutMillis(forceCloseTimeoutMillis);
        return this;
    }
}

