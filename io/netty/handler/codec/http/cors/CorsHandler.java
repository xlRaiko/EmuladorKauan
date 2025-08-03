/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http.cors;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.cors.CorsConfig;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.Collections;
import java.util.List;

public class CorsHandler
extends ChannelDuplexHandler {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(CorsHandler.class);
    private static final String ANY_ORIGIN = "*";
    private static final String NULL_ORIGIN = "null";
    private CorsConfig config;
    private HttpRequest request;
    private final List<CorsConfig> configList;
    private boolean isShortCircuit;

    public CorsHandler(CorsConfig config) {
        this(Collections.singletonList(ObjectUtil.checkNotNull(config, "config")), config.isShortCircuit());
    }

    public CorsHandler(List<CorsConfig> configList, boolean isShortCircuit) {
        ObjectUtil.checkNonEmpty(configList, "configList");
        this.configList = configList;
        this.isShortCircuit = isShortCircuit;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            this.request = (HttpRequest)msg;
            String origin = this.request.headers().get(HttpHeaderNames.ORIGIN);
            this.config = this.getForOrigin(origin);
            if (CorsHandler.isPreflightRequest(this.request)) {
                this.handlePreflight(ctx, this.request);
                return;
            }
            if (this.isShortCircuit && origin != null && this.config == null) {
                CorsHandler.forbidden(ctx, this.request);
                return;
            }
        }
        ctx.fireChannelRead(msg);
    }

    private void handlePreflight(ChannelHandlerContext ctx, HttpRequest request) {
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.OK, true, true);
        if (this.setOrigin(response)) {
            this.setAllowMethods(response);
            this.setAllowHeaders(response);
            this.setAllowCredentials(response);
            this.setMaxAge(response);
            this.setPreflightHeaders(response);
        }
        if (!response.headers().contains(HttpHeaderNames.CONTENT_LENGTH)) {
            response.headers().set((CharSequence)HttpHeaderNames.CONTENT_LENGTH, (Object)HttpHeaderValues.ZERO);
        }
        ReferenceCountUtil.release(request);
        CorsHandler.respond(ctx, request, response);
    }

    private void setPreflightHeaders(HttpResponse response) {
        response.headers().add(this.config.preflightResponseHeaders());
    }

    private CorsConfig getForOrigin(String requestOrigin) {
        for (CorsConfig corsConfig : this.configList) {
            if (corsConfig.isAnyOriginSupported()) {
                return corsConfig;
            }
            if (corsConfig.origins().contains(requestOrigin)) {
                return corsConfig;
            }
            if (!corsConfig.isNullOriginAllowed() && !NULL_ORIGIN.equals(requestOrigin)) continue;
            return corsConfig;
        }
        return null;
    }

    private boolean setOrigin(HttpResponse response) {
        String origin = this.request.headers().get(HttpHeaderNames.ORIGIN);
        if (origin != null && this.config != null) {
            if (NULL_ORIGIN.equals(origin) && this.config.isNullOriginAllowed()) {
                CorsHandler.setNullOrigin(response);
                return true;
            }
            if (this.config.isAnyOriginSupported()) {
                if (this.config.isCredentialsAllowed()) {
                    this.echoRequestOrigin(response);
                    CorsHandler.setVaryHeader(response);
                } else {
                    CorsHandler.setAnyOrigin(response);
                }
                return true;
            }
            if (this.config.origins().contains(origin)) {
                CorsHandler.setOrigin(response, origin);
                CorsHandler.setVaryHeader(response);
                return true;
            }
            logger.debug("Request origin [{}]] was not among the configured origins [{}]", (Object)origin, (Object)this.config.origins());
        }
        return false;
    }

    private void echoRequestOrigin(HttpResponse response) {
        CorsHandler.setOrigin(response, this.request.headers().get(HttpHeaderNames.ORIGIN));
    }

    private static void setVaryHeader(HttpResponse response) {
        response.headers().set((CharSequence)HttpHeaderNames.VARY, (Object)HttpHeaderNames.ORIGIN);
    }

    private static void setAnyOrigin(HttpResponse response) {
        CorsHandler.setOrigin(response, ANY_ORIGIN);
    }

    private static void setNullOrigin(HttpResponse response) {
        CorsHandler.setOrigin(response, NULL_ORIGIN);
    }

    private static void setOrigin(HttpResponse response, String origin) {
        response.headers().set((CharSequence)HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, (Object)origin);
    }

    private void setAllowCredentials(HttpResponse response) {
        if (this.config.isCredentialsAllowed() && !response.headers().get(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN).equals(ANY_ORIGIN)) {
            response.headers().set((CharSequence)HttpHeaderNames.ACCESS_CONTROL_ALLOW_CREDENTIALS, (Object)"true");
        }
    }

    private static boolean isPreflightRequest(HttpRequest request) {
        HttpHeaders headers = request.headers();
        return HttpMethod.OPTIONS.equals(request.method()) && headers.contains(HttpHeaderNames.ORIGIN) && headers.contains(HttpHeaderNames.ACCESS_CONTROL_REQUEST_METHOD);
    }

    private void setExposeHeaders(HttpResponse response) {
        if (!this.config.exposedHeaders().isEmpty()) {
            response.headers().set((CharSequence)HttpHeaderNames.ACCESS_CONTROL_EXPOSE_HEADERS, this.config.exposedHeaders());
        }
    }

    private void setAllowMethods(HttpResponse response) {
        response.headers().set((CharSequence)HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS, this.config.allowedRequestMethods());
    }

    private void setAllowHeaders(HttpResponse response) {
        response.headers().set((CharSequence)HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS, this.config.allowedRequestHeaders());
    }

    private void setMaxAge(HttpResponse response) {
        response.headers().set((CharSequence)HttpHeaderNames.ACCESS_CONTROL_MAX_AGE, (Object)this.config.maxAge());
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        HttpResponse response;
        if (this.config != null && this.config.isCorsSupportEnabled() && msg instanceof HttpResponse && this.setOrigin(response = (HttpResponse)msg)) {
            this.setAllowCredentials(response);
            this.setExposeHeaders(response);
        }
        ctx.write(msg, promise);
    }

    private static void forbidden(ChannelHandlerContext ctx, HttpRequest request) {
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.FORBIDDEN, ctx.alloc().buffer(0));
        response.headers().set((CharSequence)HttpHeaderNames.CONTENT_LENGTH, (Object)HttpHeaderValues.ZERO);
        ReferenceCountUtil.release(request);
        CorsHandler.respond(ctx, request, response);
    }

    private static void respond(ChannelHandlerContext ctx, HttpRequest request, HttpResponse response) {
        boolean keepAlive = HttpUtil.isKeepAlive(request);
        HttpUtil.setKeepAlive(response, keepAlive);
        ChannelFuture future = ctx.writeAndFlush(response);
        if (!keepAlive) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }
}

