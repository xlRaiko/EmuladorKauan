/*
 * Decompiled with CFR 0.152.
 */
package com.zaxxer.hikari.pool;

import com.zaxxer.hikari.pool.HikariPool;
import com.zaxxer.hikari.pool.PoolBase;
import com.zaxxer.hikari.pool.ProxyConnection;
import com.zaxxer.hikari.pool.ProxyFactory;
import com.zaxxer.hikari.pool.ProxyLeakTask;
import com.zaxxer.hikari.util.ClockSource;
import com.zaxxer.hikari.util.ConcurrentBag;
import com.zaxxer.hikari.util.FastList;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class PoolEntry
implements ConcurrentBag.IConcurrentBagEntry {
    private static final Logger LOGGER = LoggerFactory.getLogger(PoolEntry.class);
    private static final AtomicIntegerFieldUpdater<PoolEntry> stateUpdater = AtomicIntegerFieldUpdater.newUpdater(PoolEntry.class, "state");
    Connection connection;
    long lastAccessed;
    long lastBorrowed;
    private volatile int state = 0;
    private volatile boolean evict;
    private volatile ScheduledFuture<?> endOfLife;
    private final FastList<Statement> openStatements;
    private final HikariPool hikariPool;
    private final boolean isReadOnly;
    private final boolean isAutoCommit;

    PoolEntry(Connection connection, PoolBase pool, boolean isReadOnly, boolean isAutoCommit) {
        this.connection = connection;
        this.hikariPool = (HikariPool)pool;
        this.isReadOnly = isReadOnly;
        this.isAutoCommit = isAutoCommit;
        this.lastAccessed = ClockSource.currentTime();
        this.openStatements = new FastList(Statement.class, 16);
    }

    void recycle(long lastAccessed) {
        if (this.connection != null) {
            this.lastAccessed = lastAccessed;
            this.hikariPool.recycle(this);
        }
    }

    void setFutureEol(ScheduledFuture<?> endOfLife) {
        this.endOfLife = endOfLife;
    }

    Connection createProxyConnection(ProxyLeakTask leakTask, long now) {
        return ProxyFactory.getProxyConnection(this, this.connection, this.openStatements, leakTask, now, this.isReadOnly, this.isAutoCommit);
    }

    void resetConnectionState(ProxyConnection proxyConnection, int dirtyBits) throws SQLException {
        this.hikariPool.resetConnectionState(this.connection, proxyConnection, dirtyBits);
    }

    String getPoolName() {
        return this.hikariPool.toString();
    }

    boolean isMarkedEvicted() {
        return this.evict;
    }

    void markEvicted() {
        this.evict = true;
    }

    void evict(String closureReason) {
        this.hikariPool.closeConnection(this, closureReason);
    }

    long getMillisSinceBorrowed() {
        return ClockSource.elapsedMillis(this.lastBorrowed);
    }

    PoolBase getPoolBase() {
        return this.hikariPool;
    }

    public String toString() {
        long now = ClockSource.currentTime();
        return this.connection + ", accessed " + ClockSource.elapsedDisplayString(this.lastAccessed, now) + " ago, " + this.stateToString();
    }

    @Override
    public int getState() {
        return stateUpdater.get(this);
    }

    @Override
    public boolean compareAndSet(int expect, int update) {
        return stateUpdater.compareAndSet(this, expect, update);
    }

    @Override
    public void setState(int update) {
        stateUpdater.set(this, update);
    }

    Connection close() {
        ScheduledFuture<?> eol = this.endOfLife;
        if (eol != null && !eol.isDone() && !eol.cancel(false)) {
            LOGGER.warn("{} - maxLifeTime expiration task cancellation unexpectedly returned false for connection {}", (Object)this.getPoolName(), (Object)this.connection);
        }
        Connection con = this.connection;
        this.connection = null;
        this.endOfLife = null;
        return con;
    }

    private String stateToString() {
        switch (this.state) {
            case 1: {
                return "IN_USE";
            }
            case 0: {
                return "NOT_IN_USE";
            }
            case -1: {
                return "REMOVED";
            }
            case -2: {
                return "RESERVED";
            }
        }
        return "Invalid";
    }
}

