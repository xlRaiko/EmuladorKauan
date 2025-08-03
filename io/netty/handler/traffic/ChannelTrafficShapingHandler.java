/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.traffic;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.traffic.AbstractTrafficShapingHandler;
import io.netty.handler.traffic.TrafficCounter;
import java.util.ArrayDeque;
import java.util.concurrent.TimeUnit;

public class ChannelTrafficShapingHandler
extends AbstractTrafficShapingHandler {
    private final ArrayDeque<ToSend> messagesQueue = new ArrayDeque();
    private long queueSize;

    public ChannelTrafficShapingHandler(long writeLimit, long readLimit, long checkInterval, long maxTime) {
        super(writeLimit, readLimit, checkInterval, maxTime);
    }

    public ChannelTrafficShapingHandler(long writeLimit, long readLimit, long checkInterval) {
        super(writeLimit, readLimit, checkInterval);
    }

    public ChannelTrafficShapingHandler(long writeLimit, long readLimit) {
        super(writeLimit, readLimit);
    }

    public ChannelTrafficShapingHandler(long checkInterval) {
        super(checkInterval);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        TrafficCounter trafficCounter = new TrafficCounter(this, ctx.executor(), "ChannelTC" + ctx.channel().hashCode(), this.checkInterval);
        this.setTrafficCounter(trafficCounter);
        trafficCounter.start();
        super.handlerAdded(ctx);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        this.trafficCounter.stop();
        ChannelTrafficShapingHandler channelTrafficShapingHandler = this;
        synchronized (channelTrafficShapingHandler) {
            if (ctx.channel().isActive()) {
                for (ToSend toSend : this.messagesQueue) {
                    long size = this.calculateSize(toSend.toSend);
                    this.trafficCounter.bytesRealWriteFlowControl(size);
                    this.queueSize -= size;
                    ctx.write(toSend.toSend, toSend.promise);
                }
            } else {
                for (ToSend toSend : this.messagesQueue) {
                    if (!(toSend.toSend instanceof ByteBuf)) continue;
                    ((ByteBuf)toSend.toSend).release();
                }
            }
            this.messagesQueue.clear();
        }
        this.releaseWriteSuspended(ctx);
        this.releaseReadSuspended(ctx);
        super.handlerRemoved(ctx);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    void submitWrite(final ChannelHandlerContext ctx, Object msg, long size, long delay, long now, ChannelPromise promise) {
        ToSend newToSend;
        ChannelTrafficShapingHandler channelTrafficShapingHandler = this;
        synchronized (channelTrafficShapingHandler) {
            if (delay == 0L && this.messagesQueue.isEmpty()) {
                this.trafficCounter.bytesRealWriteFlowControl(size);
                ctx.write(msg, promise);
                return;
            }
            newToSend = new ToSend(delay + now, msg, promise);
            this.messagesQueue.addLast(newToSend);
            this.queueSize += size;
            this.checkWriteSuspend(ctx, delay, this.queueSize);
        }
        final long futureNow = newToSend.relativeTimeAction;
        ctx.executor().schedule(new Runnable(){

            @Override
            public void run() {
                ChannelTrafficShapingHandler.this.sendAllValid(ctx, futureNow);
            }
        }, delay, TimeUnit.MILLISECONDS);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void sendAllValid(ChannelHandlerContext ctx, long now) {
        ChannelTrafficShapingHandler channelTrafficShapingHandler = this;
        synchronized (channelTrafficShapingHandler) {
            ToSend newToSend = this.messagesQueue.pollFirst();
            while (newToSend != null) {
                if (newToSend.relativeTimeAction <= now) {
                    long size = this.calculateSize(newToSend.toSend);
                    this.trafficCounter.bytesRealWriteFlowControl(size);
                    this.queueSize -= size;
                } else {
                    this.messagesQueue.addFirst(newToSend);
                    break;
                }
                ctx.write(newToSend.toSend, newToSend.promise);
                newToSend = this.messagesQueue.pollFirst();
            }
            if (this.messagesQueue.isEmpty()) {
                this.releaseWriteSuspended(ctx);
            }
        }
        ctx.flush();
    }

    public long queueSize() {
        return this.queueSize;
    }

    private static final class ToSend {
        final long relativeTimeAction;
        final Object toSend;
        final ChannelPromise promise;

        private ToSend(long delay, Object toSend, ChannelPromise promise) {
            this.relativeTimeAction = delay;
            this.toSend = toSend;
            this.promise = promise;
        }
    }
}

