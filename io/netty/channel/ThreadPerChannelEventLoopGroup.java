/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel;

import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.FailedChannelFuture;
import io.netty.channel.ThreadPerChannelEventLoop;
import io.netty.util.concurrent.AbstractEventExecutorGroup;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.ThreadPerTaskExecutor;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.ReadOnlyIterator;
import io.netty.util.internal.ThrowableUtil;
import java.util.Collections;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

@Deprecated
public class ThreadPerChannelEventLoopGroup
extends AbstractEventExecutorGroup
implements EventLoopGroup {
    private final Object[] childArgs;
    private final int maxChannels;
    final Executor executor;
    final Set<EventLoop> activeChildren = Collections.newSetFromMap(PlatformDependent.newConcurrentHashMap());
    final Queue<EventLoop> idleChildren = new ConcurrentLinkedQueue<EventLoop>();
    private final ChannelException tooManyChannels;
    private volatile boolean shuttingDown;
    private final Promise<?> terminationFuture = new DefaultPromise(GlobalEventExecutor.INSTANCE);
    private final FutureListener<Object> childTerminationListener = new FutureListener<Object>(){

        @Override
        public void operationComplete(Future<Object> future) throws Exception {
            if (ThreadPerChannelEventLoopGroup.this.isTerminated()) {
                ThreadPerChannelEventLoopGroup.this.terminationFuture.trySuccess(null);
            }
        }
    };

    protected ThreadPerChannelEventLoopGroup() {
        this(0);
    }

    protected ThreadPerChannelEventLoopGroup(int maxChannels) {
        this(maxChannels, (ThreadFactory)null, new Object[0]);
    }

    protected ThreadPerChannelEventLoopGroup(int maxChannels, ThreadFactory threadFactory, Object ... args) {
        this(maxChannels, threadFactory == null ? null : new ThreadPerTaskExecutor(threadFactory), args);
    }

    protected ThreadPerChannelEventLoopGroup(int maxChannels, Executor executor, Object ... args) {
        ObjectUtil.checkPositiveOrZero(maxChannels, "maxChannels");
        if (executor == null) {
            executor = new ThreadPerTaskExecutor(new DefaultThreadFactory(this.getClass()));
        }
        this.childArgs = args == null ? EmptyArrays.EMPTY_OBJECTS : (Object[])args.clone();
        this.maxChannels = maxChannels;
        this.executor = executor;
        this.tooManyChannels = ThrowableUtil.unknownStackTrace(ChannelException.newStatic("too many channels (max: " + maxChannels + ')', null), ThreadPerChannelEventLoopGroup.class, "nextChild()");
    }

    protected EventLoop newChild(Object ... args) throws Exception {
        return new ThreadPerChannelEventLoop(this);
    }

    @Override
    public Iterator<EventExecutor> iterator() {
        return new ReadOnlyIterator<EventExecutor>(this.activeChildren.iterator());
    }

    @Override
    public EventLoop next() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Future<?> shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit) {
        this.shuttingDown = true;
        for (EventLoop l : this.activeChildren) {
            l.shutdownGracefully(quietPeriod, timeout, unit);
        }
        for (EventLoop l : this.idleChildren) {
            l.shutdownGracefully(quietPeriod, timeout, unit);
        }
        if (this.isTerminated()) {
            this.terminationFuture.trySuccess(null);
        }
        return this.terminationFuture();
    }

    @Override
    public Future<?> terminationFuture() {
        return this.terminationFuture;
    }

    @Override
    @Deprecated
    public void shutdown() {
        this.shuttingDown = true;
        for (EventLoop l : this.activeChildren) {
            l.shutdown();
        }
        for (EventLoop l : this.idleChildren) {
            l.shutdown();
        }
        if (this.isTerminated()) {
            this.terminationFuture.trySuccess(null);
        }
    }

    @Override
    public boolean isShuttingDown() {
        for (EventLoop l : this.activeChildren) {
            if (l.isShuttingDown()) continue;
            return false;
        }
        for (EventLoop l : this.idleChildren) {
            if (l.isShuttingDown()) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean isShutdown() {
        for (EventLoop l : this.activeChildren) {
            if (l.isShutdown()) continue;
            return false;
        }
        for (EventLoop l : this.idleChildren) {
            if (l.isShutdown()) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean isTerminated() {
        for (EventLoop l : this.activeChildren) {
            if (l.isTerminated()) continue;
            return false;
        }
        for (EventLoop l : this.idleChildren) {
            if (l.isTerminated()) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        long timeLeft;
        long deadline = System.nanoTime() + unit.toNanos(timeout);
        for (EventLoop l : this.activeChildren) {
            do {
                if ((timeLeft = deadline - System.nanoTime()) > 0L) continue;
                return this.isTerminated();
            } while (!l.awaitTermination(timeLeft, TimeUnit.NANOSECONDS));
        }
        for (EventLoop l : this.idleChildren) {
            do {
                if ((timeLeft = deadline - System.nanoTime()) > 0L) continue;
                return this.isTerminated();
            } while (!l.awaitTermination(timeLeft, TimeUnit.NANOSECONDS));
        }
        return this.isTerminated();
    }

    @Override
    public ChannelFuture register(Channel channel) {
        ObjectUtil.checkNotNull(channel, "channel");
        try {
            EventLoop l = this.nextChild();
            return l.register(new DefaultChannelPromise(channel, l));
        }
        catch (Throwable t) {
            return new FailedChannelFuture(channel, GlobalEventExecutor.INSTANCE, t);
        }
    }

    @Override
    public ChannelFuture register(ChannelPromise promise) {
        try {
            return this.nextChild().register(promise);
        }
        catch (Throwable t) {
            promise.setFailure(t);
            return promise;
        }
    }

    @Override
    @Deprecated
    public ChannelFuture register(Channel channel, ChannelPromise promise) {
        ObjectUtil.checkNotNull(channel, "channel");
        try {
            return this.nextChild().register(channel, promise);
        }
        catch (Throwable t) {
            promise.setFailure(t);
            return promise;
        }
    }

    private EventLoop nextChild() throws Exception {
        if (this.shuttingDown) {
            throw new RejectedExecutionException("shutting down");
        }
        EventLoop loop = this.idleChildren.poll();
        if (loop == null) {
            if (this.maxChannels > 0 && this.activeChildren.size() >= this.maxChannels) {
                throw this.tooManyChannels;
            }
            loop = this.newChild(this.childArgs);
            loop.terminationFuture().addListener(this.childTerminationListener);
        }
        this.activeChildren.add(loop);
        return loop;
    }
}

