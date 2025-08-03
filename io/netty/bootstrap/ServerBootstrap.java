/*
 * Decompiled with CFR 0.152.
 */
package io.netty.bootstrap;

import io.netty.bootstrap.AbstractBootstrap;
import io.netty.bootstrap.ServerBootstrapConfig;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.util.AttributeKey;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class ServerBootstrap
extends AbstractBootstrap<ServerBootstrap, ServerChannel> {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(ServerBootstrap.class);
    private final Map<ChannelOption<?>, Object> childOptions = new LinkedHashMap();
    private final Map<AttributeKey<?>, Object> childAttrs = new ConcurrentHashMap();
    private final ServerBootstrapConfig config = new ServerBootstrapConfig(this);
    private volatile EventLoopGroup childGroup;
    private volatile ChannelHandler childHandler;

    public ServerBootstrap() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private ServerBootstrap(ServerBootstrap bootstrap) {
        super(bootstrap);
        this.childGroup = bootstrap.childGroup;
        this.childHandler = bootstrap.childHandler;
        Map<ChannelOption<?>, Object> map = bootstrap.childOptions;
        synchronized (map) {
            this.childOptions.putAll(bootstrap.childOptions);
        }
        this.childAttrs.putAll(bootstrap.childAttrs);
    }

    @Override
    public ServerBootstrap group(EventLoopGroup group) {
        return this.group(group, group);
    }

    public ServerBootstrap group(EventLoopGroup parentGroup, EventLoopGroup childGroup) {
        super.group(parentGroup);
        if (this.childGroup != null) {
            throw new IllegalStateException("childGroup set already");
        }
        this.childGroup = ObjectUtil.checkNotNull(childGroup, "childGroup");
        return this;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public <T> ServerBootstrap childOption(ChannelOption<T> childOption, T value) {
        ObjectUtil.checkNotNull(childOption, "childOption");
        Map<ChannelOption<?>, Object> map = this.childOptions;
        synchronized (map) {
            if (value == null) {
                this.childOptions.remove(childOption);
            } else {
                this.childOptions.put(childOption, value);
            }
        }
        return this;
    }

    public <T> ServerBootstrap childAttr(AttributeKey<T> childKey, T value) {
        ObjectUtil.checkNotNull(childKey, "childKey");
        if (value == null) {
            this.childAttrs.remove(childKey);
        } else {
            this.childAttrs.put(childKey, value);
        }
        return this;
    }

    public ServerBootstrap childHandler(ChannelHandler childHandler) {
        this.childHandler = ObjectUtil.checkNotNull(childHandler, "childHandler");
        return this;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    void init(Channel channel) {
        Map.Entry[] currentChildOptions;
        ServerBootstrap.setChannelOptions(channel, this.newOptionsArray(), logger);
        ServerBootstrap.setAttributes(channel, this.attrs0().entrySet().toArray(EMPTY_ATTRIBUTE_ARRAY));
        ChannelPipeline p = channel.pipeline();
        final EventLoopGroup currentChildGroup = this.childGroup;
        final ChannelHandler currentChildHandler = this.childHandler;
        Map<ChannelOption<?>, Object> map = this.childOptions;
        synchronized (map) {
            currentChildOptions = this.childOptions.entrySet().toArray(EMPTY_OPTION_ARRAY);
        }
        final Map.Entry[] currentChildAttrs = this.childAttrs.entrySet().toArray(EMPTY_ATTRIBUTE_ARRAY);
        p.addLast(new ChannelInitializer<Channel>(){

            @Override
            public void initChannel(final Channel ch) {
                final ChannelPipeline pipeline = ch.pipeline();
                ChannelHandler handler = ServerBootstrap.this.config.handler();
                if (handler != null) {
                    pipeline.addLast(handler);
                }
                ch.eventLoop().execute(new Runnable(){

                    @Override
                    public void run() {
                        pipeline.addLast(new ServerBootstrapAcceptor(ch, currentChildGroup, currentChildHandler, currentChildOptions, currentChildAttrs));
                    }
                });
            }
        });
    }

    @Override
    public ServerBootstrap validate() {
        super.validate();
        if (this.childHandler == null) {
            throw new IllegalStateException("childHandler not set");
        }
        if (this.childGroup == null) {
            logger.warn("childGroup is not set. Using parentGroup instead.");
            this.childGroup = this.config.group();
        }
        return this;
    }

    @Override
    public ServerBootstrap clone() {
        return new ServerBootstrap(this);
    }

    @Deprecated
    public EventLoopGroup childGroup() {
        return this.childGroup;
    }

    final ChannelHandler childHandler() {
        return this.childHandler;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    final Map<ChannelOption<?>, Object> childOptions() {
        Map<ChannelOption<?>, Object> map = this.childOptions;
        synchronized (map) {
            return ServerBootstrap.copiedMap(this.childOptions);
        }
    }

    final Map<AttributeKey<?>, Object> childAttrs() {
        return ServerBootstrap.copiedMap(this.childAttrs);
    }

    public final ServerBootstrapConfig config() {
        return this.config;
    }

    private static class ServerBootstrapAcceptor
    extends ChannelInboundHandlerAdapter {
        private final EventLoopGroup childGroup;
        private final ChannelHandler childHandler;
        private final Map.Entry<ChannelOption<?>, Object>[] childOptions;
        private final Map.Entry<AttributeKey<?>, Object>[] childAttrs;
        private final Runnable enableAutoReadTask;

        ServerBootstrapAcceptor(final Channel channel, EventLoopGroup childGroup, ChannelHandler childHandler, Map.Entry<ChannelOption<?>, Object>[] childOptions, Map.Entry<AttributeKey<?>, Object>[] childAttrs) {
            this.childGroup = childGroup;
            this.childHandler = childHandler;
            this.childOptions = childOptions;
            this.childAttrs = childAttrs;
            this.enableAutoReadTask = new Runnable(){

                @Override
                public void run() {
                    channel.config().setAutoRead(true);
                }
            };
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            final Channel child = (Channel)msg;
            child.pipeline().addLast(this.childHandler);
            AbstractBootstrap.setChannelOptions(child, this.childOptions, logger);
            AbstractBootstrap.setAttributes(child, this.childAttrs);
            try {
                this.childGroup.register(child).addListener(new ChannelFutureListener(){

                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (!future.isSuccess()) {
                            ServerBootstrapAcceptor.forceClose(child, future.cause());
                        }
                    }
                });
            }
            catch (Throwable t) {
                ServerBootstrapAcceptor.forceClose(child, t);
            }
        }

        private static void forceClose(Channel child, Throwable t) {
            child.unsafe().closeForcibly();
            logger.warn("Failed to register an accepted channel: {}", (Object)child, (Object)t);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            ChannelConfig config = ctx.channel().config();
            if (config.isAutoRead()) {
                config.setAutoRead(false);
                ctx.channel().eventLoop().schedule(this.enableAutoReadTask, 1L, TimeUnit.SECONDS);
            }
            ctx.fireExceptionCaught(cause);
        }
    }
}

