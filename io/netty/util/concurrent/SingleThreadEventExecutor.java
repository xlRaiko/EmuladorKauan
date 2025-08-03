/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util.concurrent;

import io.netty.util.concurrent.AbstractEventExecutor;
import io.netty.util.concurrent.AbstractScheduledEventExecutor;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.concurrent.OrderedEventExecutor;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.RejectedExecutionHandler;
import io.netty.util.concurrent.RejectedExecutionHandlers;
import io.netty.util.concurrent.ScheduledFutureTask;
import io.netty.util.concurrent.ThreadPerTaskExecutor;
import io.netty.util.concurrent.ThreadProperties;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.ThreadExecutorMap;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public abstract class SingleThreadEventExecutor
extends AbstractScheduledEventExecutor
implements OrderedEventExecutor {
    static final int DEFAULT_MAX_PENDING_EXECUTOR_TASKS = Math.max(16, SystemPropertyUtil.getInt("io.netty.eventexecutor.maxPendingTasks", Integer.MAX_VALUE));
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(SingleThreadEventExecutor.class);
    private static final int ST_NOT_STARTED = 1;
    private static final int ST_STARTED = 2;
    private static final int ST_SHUTTING_DOWN = 3;
    private static final int ST_SHUTDOWN = 4;
    private static final int ST_TERMINATED = 5;
    private static final Runnable NOOP_TASK = new Runnable(){

        @Override
        public void run() {
        }
    };
    private static final AtomicIntegerFieldUpdater<SingleThreadEventExecutor> STATE_UPDATER = AtomicIntegerFieldUpdater.newUpdater(SingleThreadEventExecutor.class, "state");
    private static final AtomicReferenceFieldUpdater<SingleThreadEventExecutor, ThreadProperties> PROPERTIES_UPDATER = AtomicReferenceFieldUpdater.newUpdater(SingleThreadEventExecutor.class, ThreadProperties.class, "threadProperties");
    private final Queue<Runnable> taskQueue;
    private volatile Thread thread;
    private volatile ThreadProperties threadProperties;
    private final Executor executor;
    private volatile boolean interrupted;
    private final CountDownLatch threadLock = new CountDownLatch(1);
    private final Set<Runnable> shutdownHooks = new LinkedHashSet<Runnable>();
    private final boolean addTaskWakesUp;
    private final int maxPendingTasks;
    private final RejectedExecutionHandler rejectedExecutionHandler;
    private long lastExecutionTime;
    private volatile int state = 1;
    private volatile long gracefulShutdownQuietPeriod;
    private volatile long gracefulShutdownTimeout;
    private long gracefulShutdownStartTime;
    private final Promise<?> terminationFuture = new DefaultPromise(GlobalEventExecutor.INSTANCE);
    private static final long SCHEDULE_PURGE_INTERVAL = TimeUnit.SECONDS.toNanos(1L);

    protected SingleThreadEventExecutor(EventExecutorGroup parent, ThreadFactory threadFactory, boolean addTaskWakesUp) {
        this(parent, new ThreadPerTaskExecutor(threadFactory), addTaskWakesUp);
    }

    protected SingleThreadEventExecutor(EventExecutorGroup parent, ThreadFactory threadFactory, boolean addTaskWakesUp, int maxPendingTasks, RejectedExecutionHandler rejectedHandler) {
        this(parent, (Executor)new ThreadPerTaskExecutor(threadFactory), addTaskWakesUp, maxPendingTasks, rejectedHandler);
    }

    protected SingleThreadEventExecutor(EventExecutorGroup parent, Executor executor, boolean addTaskWakesUp) {
        this(parent, executor, addTaskWakesUp, DEFAULT_MAX_PENDING_EXECUTOR_TASKS, RejectedExecutionHandlers.reject());
    }

    protected SingleThreadEventExecutor(EventExecutorGroup parent, Executor executor, boolean addTaskWakesUp, int maxPendingTasks, RejectedExecutionHandler rejectedHandler) {
        super(parent);
        this.addTaskWakesUp = addTaskWakesUp;
        this.maxPendingTasks = Math.max(16, maxPendingTasks);
        this.executor = ThreadExecutorMap.apply(executor, (EventExecutor)this);
        this.taskQueue = this.newTaskQueue(this.maxPendingTasks);
        this.rejectedExecutionHandler = ObjectUtil.checkNotNull(rejectedHandler, "rejectedHandler");
    }

    protected SingleThreadEventExecutor(EventExecutorGroup parent, Executor executor, boolean addTaskWakesUp, Queue<Runnable> taskQueue, RejectedExecutionHandler rejectedHandler) {
        super(parent);
        this.addTaskWakesUp = addTaskWakesUp;
        this.maxPendingTasks = DEFAULT_MAX_PENDING_EXECUTOR_TASKS;
        this.executor = ThreadExecutorMap.apply(executor, (EventExecutor)this);
        this.taskQueue = ObjectUtil.checkNotNull(taskQueue, "taskQueue");
        this.rejectedExecutionHandler = ObjectUtil.checkNotNull(rejectedHandler, "rejectedHandler");
    }

    @Deprecated
    protected Queue<Runnable> newTaskQueue() {
        return this.newTaskQueue(this.maxPendingTasks);
    }

    protected Queue<Runnable> newTaskQueue(int maxPendingTasks) {
        return new LinkedBlockingQueue<Runnable>(maxPendingTasks);
    }

    protected void interruptThread() {
        Thread currentThread = this.thread;
        if (currentThread == null) {
            this.interrupted = true;
        } else {
            currentThread.interrupt();
        }
    }

    protected Runnable pollTask() {
        assert (this.inEventLoop());
        return SingleThreadEventExecutor.pollTaskFrom(this.taskQueue);
    }

    protected static Runnable pollTaskFrom(Queue<Runnable> taskQueue) {
        Runnable task;
        while ((task = taskQueue.poll()) == WAKEUP_TASK) {
        }
        return task;
    }

    protected Runnable takeTask() {
        Runnable task;
        assert (this.inEventLoop());
        if (!(this.taskQueue instanceof BlockingQueue)) {
            throw new UnsupportedOperationException();
        }
        BlockingQueue taskQueue = (BlockingQueue)this.taskQueue;
        do {
            ScheduledFutureTask<?> scheduledTask;
            if ((scheduledTask = this.peekScheduledTask()) == null) {
                Runnable task2 = null;
                try {
                    task2 = (Runnable)taskQueue.take();
                    if (task2 == WAKEUP_TASK) {
                        task2 = null;
                    }
                }
                catch (InterruptedException interruptedException) {
                    // empty catch block
                }
                return task2;
            }
            long delayNanos = scheduledTask.delayNanos();
            task = null;
            if (delayNanos > 0L) {
                try {
                    task = (Runnable)taskQueue.poll(delayNanos, TimeUnit.NANOSECONDS);
                }
                catch (InterruptedException e) {
                    return null;
                }
            }
            if (task != null) continue;
            this.fetchFromScheduledTaskQueue();
            task = (Runnable)taskQueue.poll();
        } while (task == null);
        return task;
    }

    private boolean fetchFromScheduledTaskQueue() {
        Runnable scheduledTask;
        if (this.scheduledTaskQueue == null || this.scheduledTaskQueue.isEmpty()) {
            return true;
        }
        long nanoTime = AbstractScheduledEventExecutor.nanoTime();
        do {
            if ((scheduledTask = this.pollScheduledTask(nanoTime)) != null) continue;
            return true;
        } while (this.taskQueue.offer(scheduledTask));
        this.scheduledTaskQueue.add((ScheduledFutureTask)scheduledTask);
        return false;
    }

    private boolean executeExpiredScheduledTasks() {
        if (this.scheduledTaskQueue == null || this.scheduledTaskQueue.isEmpty()) {
            return false;
        }
        long nanoTime = AbstractScheduledEventExecutor.nanoTime();
        Runnable scheduledTask = this.pollScheduledTask(nanoTime);
        if (scheduledTask == null) {
            return false;
        }
        do {
            SingleThreadEventExecutor.safeExecute(scheduledTask);
        } while ((scheduledTask = this.pollScheduledTask(nanoTime)) != null);
        return true;
    }

    protected Runnable peekTask() {
        assert (this.inEventLoop());
        return this.taskQueue.peek();
    }

    protected boolean hasTasks() {
        assert (this.inEventLoop());
        return !this.taskQueue.isEmpty();
    }

    public int pendingTasks() {
        return this.taskQueue.size();
    }

    protected void addTask(Runnable task) {
        ObjectUtil.checkNotNull(task, "task");
        if (!this.offerTask(task)) {
            this.reject(task);
        }
    }

    final boolean offerTask(Runnable task) {
        if (this.isShutdown()) {
            SingleThreadEventExecutor.reject();
        }
        return this.taskQueue.offer(task);
    }

    protected boolean removeTask(Runnable task) {
        return this.taskQueue.remove(ObjectUtil.checkNotNull(task, "task"));
    }

    protected boolean runAllTasks() {
        boolean fetchedAll;
        assert (this.inEventLoop());
        boolean ranAtLeastOne = false;
        do {
            fetchedAll = this.fetchFromScheduledTaskQueue();
            if (!this.runAllTasksFrom(this.taskQueue)) continue;
            ranAtLeastOne = true;
        } while (!fetchedAll);
        if (ranAtLeastOne) {
            this.lastExecutionTime = ScheduledFutureTask.nanoTime();
        }
        this.afterRunningAllTasks();
        return ranAtLeastOne;
    }

    protected final boolean runScheduledAndExecutorTasks(int maxDrainAttempts) {
        boolean ranAtLeastOneTask;
        assert (this.inEventLoop());
        int drainAttempt = 0;
        while ((ranAtLeastOneTask = this.runExistingTasksFrom(this.taskQueue) | this.executeExpiredScheduledTasks()) && ++drainAttempt < maxDrainAttempts) {
        }
        if (drainAttempt > 0) {
            this.lastExecutionTime = ScheduledFutureTask.nanoTime();
        }
        this.afterRunningAllTasks();
        return drainAttempt > 0;
    }

    protected final boolean runAllTasksFrom(Queue<Runnable> taskQueue) {
        Runnable task = SingleThreadEventExecutor.pollTaskFrom(taskQueue);
        if (task == null) {
            return false;
        }
        do {
            SingleThreadEventExecutor.safeExecute(task);
        } while ((task = SingleThreadEventExecutor.pollTaskFrom(taskQueue)) != null);
        return true;
    }

    private boolean runExistingTasksFrom(Queue<Runnable> taskQueue) {
        Runnable task = SingleThreadEventExecutor.pollTaskFrom(taskQueue);
        if (task == null) {
            return false;
        }
        int remaining = Math.min(this.maxPendingTasks, taskQueue.size());
        SingleThreadEventExecutor.safeExecute(task);
        while (remaining-- > 0 && (task = taskQueue.poll()) != null) {
            SingleThreadEventExecutor.safeExecute(task);
        }
        return true;
    }

    protected boolean runAllTasks(long timeoutNanos) {
        long lastExecutionTime;
        block2: {
            this.fetchFromScheduledTaskQueue();
            Runnable task = this.pollTask();
            if (task == null) {
                this.afterRunningAllTasks();
                return false;
            }
            long deadline = timeoutNanos > 0L ? ScheduledFutureTask.nanoTime() + timeoutNanos : 0L;
            long runTasks = 0L;
            do {
                SingleThreadEventExecutor.safeExecute(task);
                if ((++runTasks & 0x3FL) == 0L && (lastExecutionTime = ScheduledFutureTask.nanoTime()) >= deadline) break block2;
            } while ((task = this.pollTask()) != null);
            lastExecutionTime = ScheduledFutureTask.nanoTime();
        }
        this.afterRunningAllTasks();
        this.lastExecutionTime = lastExecutionTime;
        return true;
    }

    protected void afterRunningAllTasks() {
    }

    protected long delayNanos(long currentTimeNanos) {
        ScheduledFutureTask<?> scheduledTask = this.peekScheduledTask();
        if (scheduledTask == null) {
            return SCHEDULE_PURGE_INTERVAL;
        }
        return scheduledTask.delayNanos(currentTimeNanos);
    }

    protected long deadlineNanos() {
        ScheduledFutureTask<?> scheduledTask = this.peekScheduledTask();
        if (scheduledTask == null) {
            return SingleThreadEventExecutor.nanoTime() + SCHEDULE_PURGE_INTERVAL;
        }
        return scheduledTask.deadlineNanos();
    }

    protected void updateLastExecutionTime() {
        this.lastExecutionTime = ScheduledFutureTask.nanoTime();
    }

    protected abstract void run();

    protected void cleanup() {
    }

    protected void wakeup(boolean inEventLoop) {
        if (!inEventLoop) {
            this.taskQueue.offer(WAKEUP_TASK);
        }
    }

    @Override
    public boolean inEventLoop(Thread thread) {
        return thread == this.thread;
    }

    public void addShutdownHook(final Runnable task) {
        if (this.inEventLoop()) {
            this.shutdownHooks.add(task);
        } else {
            this.execute(new Runnable(){

                @Override
                public void run() {
                    SingleThreadEventExecutor.this.shutdownHooks.add(task);
                }
            });
        }
    }

    public void removeShutdownHook(final Runnable task) {
        if (this.inEventLoop()) {
            this.shutdownHooks.remove(task);
        } else {
            this.execute(new Runnable(){

                @Override
                public void run() {
                    SingleThreadEventExecutor.this.shutdownHooks.remove(task);
                }
            });
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean runShutdownHooks() {
        boolean ran = false;
        while (!this.shutdownHooks.isEmpty()) {
            ArrayList<Runnable> copy = new ArrayList<Runnable>(this.shutdownHooks);
            this.shutdownHooks.clear();
            for (Runnable task : copy) {
                try {
                    task.run();
                }
                catch (Throwable t) {
                    logger.warn("Shutdown hook raised an exception.", t);
                }
                finally {
                    ran = true;
                }
            }
        }
        if (ran) {
            this.lastExecutionTime = ScheduledFutureTask.nanoTime();
        }
        return ran;
    }

    @Override
    public Future<?> shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit) {
        boolean wakeup;
        int newState;
        int oldState;
        ObjectUtil.checkPositiveOrZero(quietPeriod, "quietPeriod");
        if (timeout < quietPeriod) {
            throw new IllegalArgumentException("timeout: " + timeout + " (expected >= quietPeriod (" + quietPeriod + "))");
        }
        ObjectUtil.checkNotNull(unit, "unit");
        if (this.isShuttingDown()) {
            return this.terminationFuture();
        }
        boolean inEventLoop = this.inEventLoop();
        do {
            if (this.isShuttingDown()) {
                return this.terminationFuture();
            }
            wakeup = true;
            oldState = this.state;
            if (inEventLoop) {
                newState = 3;
                continue;
            }
            switch (oldState) {
                case 1: 
                case 2: {
                    newState = 3;
                    break;
                }
                default: {
                    newState = oldState;
                    wakeup = false;
                }
            }
        } while (!STATE_UPDATER.compareAndSet(this, oldState, newState));
        this.gracefulShutdownQuietPeriod = unit.toNanos(quietPeriod);
        this.gracefulShutdownTimeout = unit.toNanos(timeout);
        if (this.ensureThreadStarted(oldState)) {
            return this.terminationFuture;
        }
        if (wakeup) {
            this.taskQueue.offer(WAKEUP_TASK);
            if (!this.addTaskWakesUp) {
                this.wakeup(inEventLoop);
            }
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
        boolean wakeup;
        int newState;
        int oldState;
        if (this.isShutdown()) {
            return;
        }
        boolean inEventLoop = this.inEventLoop();
        do {
            if (this.isShuttingDown()) {
                return;
            }
            wakeup = true;
            oldState = this.state;
            if (inEventLoop) {
                newState = 4;
                continue;
            }
            switch (oldState) {
                case 1: 
                case 2: 
                case 3: {
                    newState = 4;
                    break;
                }
                default: {
                    newState = oldState;
                    wakeup = false;
                }
            }
        } while (!STATE_UPDATER.compareAndSet(this, oldState, newState));
        if (this.ensureThreadStarted(oldState)) {
            return;
        }
        if (wakeup) {
            this.taskQueue.offer(WAKEUP_TASK);
            if (!this.addTaskWakesUp) {
                this.wakeup(inEventLoop);
            }
        }
    }

    @Override
    public boolean isShuttingDown() {
        return this.state >= 3;
    }

    @Override
    public boolean isShutdown() {
        return this.state >= 4;
    }

    @Override
    public boolean isTerminated() {
        return this.state == 5;
    }

    protected boolean confirmShutdown() {
        if (!this.isShuttingDown()) {
            return false;
        }
        if (!this.inEventLoop()) {
            throw new IllegalStateException("must be invoked from an event loop");
        }
        this.cancelScheduledTasks();
        if (this.gracefulShutdownStartTime == 0L) {
            this.gracefulShutdownStartTime = ScheduledFutureTask.nanoTime();
        }
        if (this.runAllTasks() || this.runShutdownHooks()) {
            if (this.isShutdown()) {
                return true;
            }
            if (this.gracefulShutdownQuietPeriod == 0L) {
                return true;
            }
            this.taskQueue.offer(WAKEUP_TASK);
            return false;
        }
        long nanoTime = ScheduledFutureTask.nanoTime();
        if (this.isShutdown() || nanoTime - this.gracefulShutdownStartTime > this.gracefulShutdownTimeout) {
            return true;
        }
        if (nanoTime - this.lastExecutionTime <= this.gracefulShutdownQuietPeriod) {
            this.taskQueue.offer(WAKEUP_TASK);
            try {
                Thread.sleep(100L);
            }
            catch (InterruptedException interruptedException) {
                // empty catch block
            }
            return false;
        }
        return true;
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        ObjectUtil.checkNotNull(unit, "unit");
        if (this.inEventLoop()) {
            throw new IllegalStateException("cannot await termination of the current thread");
        }
        this.threadLock.await(timeout, unit);
        return this.isTerminated();
    }

    @Override
    public void execute(Runnable task) {
        ObjectUtil.checkNotNull(task, "task");
        this.execute(task, !(task instanceof AbstractEventExecutor.LazyRunnable) && this.wakesUpForTask(task));
    }

    @Override
    public void lazyExecute(Runnable task) {
        this.execute(ObjectUtil.checkNotNull(task, "task"), false);
    }

    private void execute(Runnable task, boolean immediate) {
        boolean inEventLoop = this.inEventLoop();
        this.addTask(task);
        if (!inEventLoop) {
            this.startThread();
            if (this.isShutdown()) {
                boolean reject = false;
                try {
                    if (this.removeTask(task)) {
                        reject = true;
                    }
                }
                catch (UnsupportedOperationException unsupportedOperationException) {
                    // empty catch block
                }
                if (reject) {
                    SingleThreadEventExecutor.reject();
                }
            }
        }
        if (!this.addTaskWakesUp && immediate) {
            this.wakeup(inEventLoop);
        }
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        this.throwIfInEventLoop("invokeAny");
        return super.invokeAny(tasks);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        this.throwIfInEventLoop("invokeAny");
        return super.invokeAny(tasks, timeout, unit);
    }

    @Override
    public <T> List<java.util.concurrent.Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        this.throwIfInEventLoop("invokeAll");
        return super.invokeAll(tasks);
    }

    @Override
    public <T> List<java.util.concurrent.Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        this.throwIfInEventLoop("invokeAll");
        return super.invokeAll(tasks, timeout, unit);
    }

    private void throwIfInEventLoop(String method) {
        if (this.inEventLoop()) {
            throw new RejectedExecutionException("Calling " + method + " from within the EventLoop is not allowed");
        }
    }

    public final ThreadProperties threadProperties() {
        ThreadProperties threadProperties = this.threadProperties;
        if (threadProperties == null) {
            Thread thread = this.thread;
            if (thread == null) {
                assert (!this.inEventLoop());
                this.submit(NOOP_TASK).syncUninterruptibly();
                thread = this.thread;
                assert (thread != null);
            }
            if (!PROPERTIES_UPDATER.compareAndSet(this, null, threadProperties = new DefaultThreadProperties(thread))) {
                threadProperties = this.threadProperties;
            }
        }
        return threadProperties;
    }

    protected boolean wakesUpForTask(Runnable task) {
        return true;
    }

    protected static void reject() {
        throw new RejectedExecutionException("event executor terminated");
    }

    protected final void reject(Runnable task) {
        this.rejectedExecutionHandler.rejected(task, this);
    }

    private void startThread() {
        if (this.state == 1 && STATE_UPDATER.compareAndSet(this, 1, 2)) {
            boolean success = false;
            try {
                this.doStartThread();
                success = true;
            }
            finally {
                if (!success) {
                    STATE_UPDATER.compareAndSet(this, 2, 1);
                }
            }
        }
    }

    private boolean ensureThreadStarted(int oldState) {
        if (oldState == 1) {
            try {
                this.doStartThread();
            }
            catch (Throwable cause) {
                STATE_UPDATER.set(this, 5);
                this.terminationFuture.tryFailure(cause);
                if (!(cause instanceof Exception)) {
                    PlatformDependent.throwException(cause);
                }
                return true;
            }
        }
        return false;
    }

    private void doStartThread() {
        assert (this.thread == null);
        this.executor.execute(new Runnable(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void run() {
                SingleThreadEventExecutor.this.thread = Thread.currentThread();
                if (SingleThreadEventExecutor.this.interrupted) {
                    SingleThreadEventExecutor.this.thread.interrupt();
                }
                boolean success = false;
                SingleThreadEventExecutor.this.updateLastExecutionTime();
                try {
                    SingleThreadEventExecutor.this.run();
                    success = true;
                }
                catch (Throwable t) {
                    logger.warn("Unexpected exception from an event executor: ", t);
                }
                finally {
                    int oldState;
                    while ((oldState = SingleThreadEventExecutor.this.state) < 3 && !STATE_UPDATER.compareAndSet(SingleThreadEventExecutor.this, oldState, 3)) {
                    }
                    if (success && SingleThreadEventExecutor.this.gracefulShutdownStartTime == 0L && logger.isErrorEnabled()) {
                        logger.error("Buggy " + EventExecutor.class.getSimpleName() + " implementation; " + SingleThreadEventExecutor.class.getSimpleName() + ".confirmShutdown() must be called before run() implementation terminates.");
                    }
                    while (!SingleThreadEventExecutor.this.confirmShutdown()) {
                    }
                    while ((oldState = SingleThreadEventExecutor.this.state) < 4 && !STATE_UPDATER.compareAndSet(SingleThreadEventExecutor.this, oldState, 4)) {
                    }
                    SingleThreadEventExecutor.this.confirmShutdown();
                }
            }
        });
    }

    final int drainTasks() {
        Runnable runnable;
        int numTasks = 0;
        while ((runnable = this.taskQueue.poll()) != null) {
            if (WAKEUP_TASK == runnable) continue;
            ++numTasks;
        }
        return numTasks;
    }

    static /* synthetic */ CountDownLatch access$700(SingleThreadEventExecutor x0) {
        return x0.threadLock;
    }

    static /* synthetic */ Promise access$800(SingleThreadEventExecutor x0) {
        return x0.terminationFuture;
    }

    private static final class DefaultThreadProperties
    implements ThreadProperties {
        private final Thread t;

        DefaultThreadProperties(Thread t) {
            this.t = t;
        }

        @Override
        public Thread.State state() {
            return this.t.getState();
        }

        @Override
        public int priority() {
            return this.t.getPriority();
        }

        @Override
        public boolean isInterrupted() {
            return this.t.isInterrupted();
        }

        @Override
        public boolean isDaemon() {
            return this.t.isDaemon();
        }

        @Override
        public String name() {
            return this.t.getName();
        }

        @Override
        public long id() {
            return this.t.getId();
        }

        @Override
        public StackTraceElement[] stackTrace() {
            return this.t.getStackTrace();
        }

        @Override
        public boolean isAlive() {
            return this.t.isAlive();
        }
    }

    @Deprecated
    protected static interface NonWakeupRunnable
    extends AbstractEventExecutor.LazyRunnable {
    }
}

