/*
 * Decompiled with CFR 0.152.
 */
package com.zaxxer.hikari.util;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DriverDataSource
implements DataSource {
    private static final Logger LOGGER = LoggerFactory.getLogger(DriverDataSource.class);
    private static final String PASSWORD = "password";
    private static final String USER = "user";
    private final String jdbcUrl;
    private final Properties driverProperties;
    private Driver driver;

    public DriverDataSource(String jdbcUrl, String driverClassName, Properties properties, String username, String password) {
        this.jdbcUrl = jdbcUrl;
        this.driverProperties = new Properties();
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            this.driverProperties.setProperty(entry.getKey().toString(), entry.getValue().toString());
        }
        if (username != null) {
            this.driverProperties.put(USER, this.driverProperties.getProperty(USER, username));
        }
        if (password != null) {
            this.driverProperties.put(PASSWORD, this.driverProperties.getProperty(PASSWORD, password));
        }
        if (driverClassName != null) {
            Enumeration<Driver> drivers = DriverManager.getDrivers();
            while (drivers.hasMoreElements()) {
                Driver d = drivers.nextElement();
                if (!d.getClass().getName().equals(driverClassName)) continue;
                this.driver = d;
                break;
            }
            if (this.driver == null) {
                LOGGER.warn("Registered driver with driverClassName={} was not found, trying direct instantiation.", (Object)driverClassName);
                Class<?> driverClass = null;
                ClassLoader threadContextClassLoader = Thread.currentThread().getContextClassLoader();
                try {
                    if (threadContextClassLoader != null) {
                        try {
                            driverClass = threadContextClassLoader.loadClass(driverClassName);
                            LOGGER.debug("Driver class {} found in Thread context class loader {}", (Object)driverClassName, (Object)threadContextClassLoader);
                        }
                        catch (ClassNotFoundException e) {
                            LOGGER.debug("Driver class {} not found in Thread context class loader {}, trying classloader {}", driverClassName, threadContextClassLoader, this.getClass().getClassLoader());
                        }
                    }
                    if (driverClass == null) {
                        driverClass = this.getClass().getClassLoader().loadClass(driverClassName);
                        LOGGER.debug("Driver class {} found in the HikariConfig class classloader {}", (Object)driverClassName, (Object)this.getClass().getClassLoader());
                    }
                }
                catch (ClassNotFoundException e) {
                    LOGGER.debug("Failed to load driver class {} from HikariConfig class classloader {}", (Object)driverClassName, (Object)this.getClass().getClassLoader());
                }
                if (driverClass != null) {
                    try {
                        this.driver = (Driver)driverClass.newInstance();
                    }
                    catch (Exception e) {
                        LOGGER.warn("Failed to create instance of driver class {}, trying jdbcUrl resolution", (Object)driverClassName, (Object)e);
                    }
                }
            }
        }
        String sanitizedUrl = jdbcUrl.replaceAll("([?&;]password=)[^&#;]*(.*)", "$1<masked>$2");
        try {
            if (this.driver == null) {
                this.driver = DriverManager.getDriver(jdbcUrl);
                LOGGER.debug("Loaded driver with class name {} for jdbcUrl={}", (Object)this.driver.getClass().getName(), (Object)sanitizedUrl);
            } else if (!this.driver.acceptsURL(jdbcUrl)) {
                throw new RuntimeException("Driver " + driverClassName + " claims to not accept jdbcUrl, " + sanitizedUrl);
            }
        }
        catch (SQLException e) {
            throw new RuntimeException("Failed to get driver instance for jdbcUrl=" + sanitizedUrl, e);
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        return this.driver.connect(this.jdbcUrl, this.driverProperties);
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        Properties cloned = (Properties)this.driverProperties.clone();
        if (username != null) {
            cloned.put(USER, username);
            if (cloned.containsKey("username")) {
                cloned.put("username", username);
            }
        }
        if (password != null) {
            cloned.put(PASSWORD, password);
        }
        return this.driver.connect(this.jdbcUrl, cloned);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public void setLogWriter(PrintWriter logWriter) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        DriverManager.setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return DriverManager.getLoginTimeout();
    }

    @Override
    public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return this.driver.getParentLogger();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
}

