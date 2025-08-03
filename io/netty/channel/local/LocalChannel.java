/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel.local;

import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelConfig;
import io.netty.channel.EventLoop;
import io.netty.channel.PreferHeapByteBufAllocator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.SingleThreadEventLoop;
import io.netty.channel.local.LocalAddress;
import io.netty.channel.local.LocalChannelRegistry;
import io.netty.channel.local.LocalServerChannel;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.SingleThreadEventExecutor;
import io.netty.util.internal.InternalThreadLocalMap;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.ConnectException;
import java.net.SocketAddress;
import java.nio.channels.AlreadyConnectedException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ConnectionPendingException;
import java.nio.channels.NotYetConnectedException;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class LocalChannel
extends AbstractChannel {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(LocalChannel.class);
    private static final AtomicReferenceFieldUpdater<LocalChannel, Future> FINISH_READ_FUTURE_UPDATER = AtomicReferenceFieldUpdater.newUpdater(LocalChannel.class, Future.class, "finishReadFuture");
    private static final ChannelMetadata METADATA = new ChannelMetadata(false);
    private static final int MAX_READER_STACK_DEPTH = 8;
    private final ChannelConfig config = new DefaultChannelConfig(this);
    final Queue<Object> inboundBuffer = PlatformDependent.newSpscQueue();
    private final Runnable readTask = new Runnable(){

        @Override
        public void run() {
            if (!LocalChannel.this.inboundBuffer.isEmpty()) {
                LocalChannel.this.readInbound();
            }
        }
    };
    private final Runnable shutdownHook = new Runnable(){

        @Override
        public void run() {
            LocalChannel.this.unsafe().close(LocalChannel.this.unsafe().voidPromise());
        }
    };
    private volatile State state;
    private volatile LocalChannel peer;
    private volatile LocalAddress localAddress;
    private volatile LocalAddress remoteAddress;
    private volatile ChannelPromise connectPromise;
    private volatile boolean readInProgress;
    private volatile boolean writeInProgress;
    private volatile Future<?> finishReadFuture;

    public LocalChannel() {
        super(null);
        this.config().setAllocator(new PreferHeapByteBufAllocator(this.config.getAllocator()));
    }

    protected LocalChannel(LocalServerChannel parent, LocalChannel peer) {
        super(parent);
        this.config().setAllocator(new PreferHeapByteBufAllocator(this.config.getAllocator()));
        this.peer = peer;
        this.localAddress = parent.localAddress();
        this.remoteAddress = peer.localAddress();
    }

    @Override
    public ChannelMetadata metadata() {
        return METADATA;
    }

    @Override
    public ChannelConfig config() {
        return this.config;
    }

    @Override
    public LocalServerChannel parent() {
        return (LocalServerChannel)super.parent();
    }

    @Override
    public LocalAddress localAddress() {
        return (LocalAddress)super.localAddress();
    }

    @Override
    public LocalAddress remoteAddress() {
        return (LocalAddress)super.remoteAddress();
    }

    @Override
    public boolean isOpen() {
        return this.state != State.CLOSED;
    }

    @Override
    public boolean isActive() {
        return this.state == State.CONNECTED;
    }

    @Override
    protected AbstractChannel.AbstractUnsafe newUnsafe() {
        return new LocalUnsafe();
    }

    @Override
    protected boolean isCompatible(EventLoop loop) {
        return loop instanceof SingleThreadEventLoop;
    }

    @Override
    protected SocketAddress localAddress0() {
        return this.localAddress;
    }

    @Override
    protected SocketAddress remoteAddress0() {
        return this.remoteAddress;
    }

    @Override
    protected void doRegister() throws Exception {
        if (this.peer != null && this.parent() != null) {
            final LocalChannel peer = this.peer;
            this.state = State.CONNECTED;
            peer.remoteAddress = this.parent() == null ? null : this.parent().localAddress();
            peer.state = State.CONNECTED;
            peer.eventLoop().execute(new Runnable(){

                @Override
                public void run() {
                    ChannelPromise promise = peer.connectPromise;
                    if (promise != null && promise.trySuccess()) {
                        peer.pipeline().fireChannelActive();
                    }
                }
            });
        }
        ((SingleThreadEventExecutor)((Object)this.eventLoop())).addShutdownHook(this.shutdownHook);
    }

    @Override
    protected void doBind(SocketAddress localAddress) throws Exception {
        this.localAddress = LocalChannelRegistry.register(this, this.localAddress, localAddress);
        this.state = State.BOUND;
    }

    @Override
    protected void doDisconnect() throws Exception {
        this.doClose();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void doClose() throws Exception {
        final LocalChannel peer = this.peer;
        State oldState = this.state;
        try {
            if (oldState != State.CLOSED) {
                ChannelPromise promise;
                if (this.localAddress != null) {
                    if (this.parent() == null) {
                        LocalChannelRegistry.unregister(this.localAddress);
                    }
                    this.localAddress = null;
                }
                this.state = State.CLOSED;
                if (this.writeInProgress && peer != null) {
                    this.finishPeerRead(peer);
                }
                if ((promise = this.connectPromise) != null) {
                    promise.tryFailure(new ClosedChannelException());
                    this.connectPromise = null;
                }
            }
            if (peer != null) {
                this.peer = null;
                EventLoop peerEventLoop = peer.eventLoop();
                final boolean peerIsActive = peer.isActive();
                try {
                    peerEventLoop.execute(new Runnable(){

                        @Override
                        public void run() {
                            peer.tryClose(peerIsActive);
                        }
                    });
                }
                catch (Throwable cause) {
                    logger.warn("Releasing Inbound Queues for channels {}-{} because exception occurred!", this, peer, cause);
                    if (peerEventLoop.inEventLoop()) {
                        peer.releaseInboundBuffers();
                    } else {
                        peer.close();
                    }
                    PlatformDependent.throwException(cause);
                }
            }
        }
        finally {
            if (oldState != null && oldState != State.CLOSED) {
                this.releaseInboundBuffers();
            }
        }
    }

    private void tryClose(boolean isActive) {
        if (isActive) {
            this.unsafe().close(this.unsafe().voidPromise());
        } else {
            this.releaseInboundBuffers();
        }
    }

    @Override
    protected void doDeregister() throws Exception {
        ((SingleThreadEventExecutor)((Object)this.eventLoop())).removeShutdownHook(this.shutdownHook);
    }

    private void readInbound() {
        Object received;
        RecvByteBufAllocator.Handle handle = this.unsafe().recvBufAllocHandle();
        handle.reset(this.config());
        ChannelPipeline pipeline = this.pipeline();
        while ((received = this.inboundBuffer.poll()) != null) {
            pipeline.fireChannelRead(received);
            if (handle.continueReading()) continue;
        }
        pipeline.fireChannelReadComplete();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void doBeginRead() throws Exception {
        if (this.readInProgress) {
            return;
        }
        Queue<Object> inboundBuffer = this.inboundBuffer;
        if (inboundBuffer.isEmpty()) {
            this.readInProgress = true;
            return;
        }
        InternalThreadLocalMap threadLocals = InternalThreadLocalMap.get();
        Integer stackDepth = threadLocals.localChannelReaderStackDepth();
        if (stackDepth < 8) {
            threadLocals.setLocalChannelReaderStackDepth(stackDepth + 1);
            try {
                this.readInbound();
            }
            finally {
                threadLocals.setLocalChannelReaderStackDepth(stackDepth);
            }
        }
        try {
            this.eventLoop().execute(this.readTask);
        }
        catch (Throwable cause) {
            logger.warn("Closing Local channels {}-{} because exception occurred!", this, this.peer, cause);
            this.close();
            this.peer.close();
            PlatformDependent.throwException(cause);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void doWrite(ChannelOutboundBuffer in) throws Exception {
        switch (this.state) {
            case OPEN: 
            case BOUND: {
                throw new NotYetConnectedException();
            }
            case CLOSED: {
                throw new ClosedChannelException();
            }
        }
        LocalChannel peer = this.peer;
        this.writeInProgress = true;
        try {
            Object msg;
            ClosedChannelException exception = null;
            while ((msg = in.current()) != null) {
                try {
                    if (peer.state == State.CONNECTED) {
                        peer.inboundBuffer.add(ReferenceCountUtil.retain(msg));
                        in.remove();
                        continue;
                    }
                    if (exception == null) {
                        exception = new ClosedChannelException();
                    }
                    in.remove(exception);
                }
                catch (Throwable cause) {
                    in.remove(cause);
                }
            }
        }
        finally {
            this.writeInProgress = false;
        }
        this.finishPeerRead(peer);
    }

    private void finishPeerRead(LocalChannel peer) {
        if (peer.eventLoop() == this.eventLoop() && !peer.writeInProgress) {
            this.finishPeerRead0(peer);
        } else {
            this.runFinishPeerReadTask(peer);
        }
    }

    private void runFinishPeerReadTask(final LocalChannel peer) {
        Runnable finishPeerReadTask = new Runnable(){

            @Override
            public void run() {
                LocalChannel.this.finishPeerRead0(peer);
            }
        };
        try {
            if (peer.writeInProgress) {
                peer.finishReadFuture = peer.eventLoop().submit(finishPeerReadTask);
            } else {
                peer.eventLoop().execute(finishPeerReadTask);
            }
        }
        catch (Throwable cause) {
            logger.warn("Closing Local channels {}-{} because exception occurred!", this, peer, cause);
            this.close();
            peer.close();
            PlatformDependent.throwException(cause);
        }
    }

    private void releaseInboundBuffers() {
        Object msg;
        assert (this.eventLoop() == null || this.eventLoop().inEventLoop());
        this.readInProgress = false;
        Queue<Object> inboundBuffer = this.inboundBuffer;
        while ((msg = inboundBuffer.poll()) != null) {
            ReferenceCountUtil.release(msg);
        }
    }

    private void finishPeerRead0(LocalChannel peer) {
        Future<?> peerFinishReadFuture = peer.finishReadFuture;
        if (peerFinishReadFuture != null) {
            if (!peerFinishReadFuture.isDone()) {
                this.runFinishPeerReadTask(peer);
                return;
            }
            FINISH_READ_FUTURE_UPDATER.compareAndSet(peer, peerFinishReadFuture, null);
        }
        if (peer.readInProgress && !peer.inboundBuffer.isEmpty()) {
            peer.readInProgress = false;
            peer.readInbound();
        }
    }

    private class LocalUnsafe
    extends AbstractChannel.AbstractUnsafe {
        private LocalUnsafe() {
            super(LocalChannel.this);
        }

        @Override
        public void connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
            Channel boundChannel;
            if (!promise.setUncancellable() || !this.ensureOpen(promise)) {
                return;
            }
            if (LocalChannel.this.state == State.CONNECTED) {
                AlreadyConnectedException cause = new AlreadyConnectedException();
                this.safeSetFailure(promise, cause);
                LocalChannel.this.pipeline().fireExceptionCaught(cause);
                return;
            }
            if (LocalChannel.this.connectPromise != null) {
                throw new ConnectionPendingException();
            }
            LocalChannel.this.connectPromise = promise;
            if (LocalChannel.this.state != State.BOUND && localAddress == null) {
                localAddress = new LocalAddress(LocalChannel.this);
            }
            if (localAddress != null) {
                try {
                    LocalChannel.this.doBind(localAddress);
                }
                catch (Throwable t) {
                    this.safeSetFailure(promise, t);
                    this.close(this.voidPromise());
                    return;
                }
            }
            if (!((boundChannel = LocalChannelRegistry.get(remoteAddress)) instanceof LocalServerChannel)) {
                ConnectException cause = new ConnectException("connection refused: " + remoteAddress);
                this.safeSetFailure(promise, cause);
                this.close(this.voidPromise());
                return;
            }
            LocalServerChannel serverChannel = (LocalServerChannel)boundChannel;
            LocalChannel.this.peer = serverChannel.serve(LocalChannel.this);
        }
    }

    private static enum State {
        OPEN,
        BOUND,
        CONNECTED,
        CLOSED;

    }
}

