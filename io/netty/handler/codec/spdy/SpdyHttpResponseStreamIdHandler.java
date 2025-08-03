/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.spdy;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.spdy.SpdyHttpHeaders;
import io.netty.handler.codec.spdy.SpdyRstStreamFrame;
import io.netty.util.ReferenceCountUtil;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

public class SpdyHttpResponseStreamIdHandler
extends MessageToMessageCodec<Object, HttpMessage> {
    private static final Integer NO_ID = -1;
    private final Queue<Integer> ids = new ArrayDeque<Integer>();

    @Override
    public boolean acceptInboundMessage(Object msg) throws Exception {
        return msg instanceof HttpMessage || msg instanceof SpdyRstStreamFrame;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, HttpMessage msg, List<Object> out) throws Exception {
        Integer id = this.ids.poll();
        if (id != null && id.intValue() != NO_ID.intValue() && !msg.headers().contains(SpdyHttpHeaders.Names.STREAM_ID)) {
            msg.headers().setInt(SpdyHttpHeaders.Names.STREAM_ID, id);
        }
        out.add(ReferenceCountUtil.retain(msg));
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {
        if (msg instanceof HttpMessage) {
            boolean contains = ((HttpMessage)msg).headers().contains(SpdyHttpHeaders.Names.STREAM_ID);
            if (!contains) {
                this.ids.add(NO_ID);
            } else {
                this.ids.add(((HttpMessage)msg).headers().getInt(SpdyHttpHeaders.Names.STREAM_ID));
            }
        } else if (msg instanceof SpdyRstStreamFrame) {
            this.ids.remove(((SpdyRstStreamFrame)msg).streamId());
        }
        out.add(ReferenceCountUtil.retain(msg));
    }
}

