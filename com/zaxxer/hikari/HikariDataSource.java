/*
 * Decompiled with CFR 0.152.
 */
package com.zaxxer.hikari;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariConfigMXBean;
import com.zaxxer.hikari.HikariPoolMXBean;
import com.zaxxer.hikari.metrics.MetricsTrackerFactory;
import com.zaxxer.hikari.pool.HikariPool;
import java.io.Closeable;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HikariDataSource
extends HikariConfig
implements DataSource,
Closeable {
    private static final Logger LOGGER = LoggerFactory.getLogger(HikariDataSource.class);
    private final AtomicBoolean isShutdown = new AtomicBoolean();
    private final HikariPool fastPathPool;
    private volatile HikariPool pool;

    public HikariDataSource() {
        this.fastPathPool = null;
    }

    public HikariDataSource(HikariConfig configuration) {
        configuration.validate();
        configuration.copyStateTo(this);
        LOGGER.info("{} - Starting...", (Object)configuration.getPoolName());
        this.pool = this.fastPathPool = new HikariPool(this);
        LOGGER.info("{} - Start completed.", (Object)configuration.getPoolName());
        this.seal();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Connection getConnection() throws SQLException {
        if (this.isClosed()) {
            throw new SQLException("HikariDataSource " + this + " has been closed.");
        }
        if (this.fastPathPool != null) {
            return this.fastPathPool.getConnection();
        }
        HikariPool result = this.pool;
        if (result == null) {
            HikariDataSource hikariDataSource = this;
            synchronized (hikariDataSource) {
                result = this.pool;
                if (result == null) {
                    this.validate();
                    LOGGER.info("{} - Starting...", (Object)this.getPoolName());
                    try {
                        this.pool = result = new HikariPool(this);
                        this.seal();
                    }
                    catch (HikariPool.PoolInitializationException pie) {
                        if (pie.getCause() instanceof SQLException) {
                            throw (SQLException)pie.getCause();
                        }
                        throw pie;
                    }
                    LOGGER.info("{} - Start completed.", (Object)this.getPoolName());
                }
            }
        }
        return result.getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        HikariPool p = this.pool;
        return p != null ? p.getUnwrappedDataSource().getLogWriter() : null;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        HikariPool p = this.pool;
        if (p != null) {
            p.getUnwrappedDataSource().setLogWriter(out);
        }
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        HikariPool p = this.pool;
        if (p != null) {
            p.getUnwrappedDataSource().setLoginTimeout(seconds);
        }
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        HikariPool p = this.pool;
        return p != null ? p.getUnwrappedDataSource().getLoginTimeout() : 0;
    }

    @Override
    public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface.isInstance(this)) {
            return (T)this;
        }
        HikariPool p = this.pool;
        if (p != null) {
            DataSource unwrappedDataSource = p.getUnwrappedDataSource();
            if (iface.isInstance(unwrappedDataSource)) {
                return (T)unwrappedDataSource;
            }
            if (unwrappedDataSource != null) {
                return unwrappedDataSource.unwrap(iface);
            }
        }
        throw new SQLException("Wrapped DataSource is not an instance of " + iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        if (iface.isInstance(this)) {
            return true;
        }
        HikariPool p = this.pool;
        if (p != null) {
            DataSource unwrappedDataSource = p.getUnwrappedDataSource();
            if (iface.isInstance(unwrappedDataSource)) {
                return true;
            }
            if (unwrappedDataSource != null) {
                return unwrappedDataSource.isWrapperFor(iface);
            }
        }
        return false;
    }

    @Override
    public void setMetricRegistry(Object metricRegistry) {
        boolean isAlreadySet = this.getMetricRegistry() != null;
        super.setMetricRegistry(metricRegistry);
        HikariPool p = this.pool;
        if (p != null) {
            if (isAlreadySet) {
                throw new IllegalStateException("MetricRegistry can only be set one time");
            }
            p.setMetricRegistry(super.getMetricRegistry());
        }
    }

    @Override
    public void setMetricsTrackerFactory(MetricsTrackerFactory metricsTrackerFactory) {
        boolean isAlreadySet = this.getMetricsTrackerFactory() != null;
        super.setMetricsTrackerFactory(metricsTrackerFactory);
        HikariPool p = this.pool;
        if (p != null) {
            if (isAlreadySet) {
                throw new IllegalStateException("MetricsTrackerFactory can only be set one time");
            }
            p.setMetricsTrackerFactory(super.getMetricsTrackerFactory());
        }
    }

    @Override
    public void setHealthCheckRegistry(Object healthCheckRegistry) {
        boolean isAlreadySet = this.getHealthCheckRegistry() != null;
        super.setHealthCheckRegistry(healthCheckRegistry);
        HikariPool p = this.pool;
        if (p != null) {
            if (isAlreadySet) {
                throw new IllegalStateException("HealthCheckRegistry can only be set one time");
            }
            p.setHealthCheckRegistry(super.getHealthCheckRegistry());
        }
    }

    public boolean isRunning() {
        return this.pool != null && this.pool.poolState == 0;
    }

    public HikariPoolMXBean getHikariPoolMXBean() {
        return this.pool;
    }

    public HikariConfigMXBean getHikariConfigMXBean() {
        return this;
    }

    public void evictConnection(Connection connection) {
        HikariPool p;
        if (!this.isClosed() && (p = this.pool) != null && connection.getClass().getName().startsWith("com.zaxxer.hikari")) {
            p.evictConnection(connection);
        }
    }

    @Override
    public void close() {
        if (this.isShutdown.getAndSet(true)) {
            return;
        }
        HikariPool p = this.pool;
        if (p != null) {
            try {
                LOGGER.info("{} - Shutdown initiated...", (Object)this.getPoolName());
                p.shutdown();
                LOGGER.info("{} - Shutdown completed.", (Object)this.getPoolName());
            }
            catch (InterruptedException e) {
                LOGGER.warn("{} - Interrupted during closing", (Object)this.getPoolName(), (Object)e);
                Thread.currentThread().interrupt();
            }
        }
    }

    public boolean isClosed() {
        return this.isShutdown.get();
    }

    public String toString() {
        return "HikariDataSource (" + this.pool + ")";
    }
}

