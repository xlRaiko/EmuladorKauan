/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 */
package io.netty.util.internal.logging;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.Log4J2Logger;
import org.apache.logging.log4j.LogManager;

public final class Log4J2LoggerFactory
extends InternalLoggerFactory {
    public static final InternalLoggerFactory INSTANCE = new Log4J2LoggerFactory();

    @Deprecated
    public Log4J2LoggerFactory() {
    }

    @Override
    public InternalLogger newInstance(String name) {
        return new Log4J2Logger(LogManager.getLogger((String)name));
    }
}

