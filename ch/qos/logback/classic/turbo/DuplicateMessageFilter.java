/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.classic.turbo;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.turbo.LRUMessageCache;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;
import org.slf4j.Marker;

public class DuplicateMessageFilter
extends TurboFilter {
    public static final int DEFAULT_CACHE_SIZE = 100;
    public static final int DEFAULT_ALLOWED_REPETITIONS = 5;
    public int allowedRepetitions = 5;
    public int cacheSize = 100;
    private LRUMessageCache msgCache;

    @Override
    public void start() {
        this.msgCache = new LRUMessageCache(this.cacheSize);
        super.start();
    }

    @Override
    public void stop() {
        this.msgCache.clear();
        this.msgCache = null;
        super.stop();
    }

    @Override
    public FilterReply decide(Marker marker, Logger logger, Level level, String format, Object[] params, Throwable t) {
        int count = this.msgCache.getMessageCountAndThenIncrement(format);
        if (count <= this.allowedRepetitions) {
            return FilterReply.NEUTRAL;
        }
        return FilterReply.DENY;
    }

    public int getAllowedRepetitions() {
        return this.allowedRepetitions;
    }

    public void setAllowedRepetitions(int allowedRepetitions) {
        this.allowedRepetitions = allowedRepetitions;
    }

    public int getCacheSize() {
        return this.cacheSize;
    }

    public void setCacheSize(int cacheSize) {
        this.cacheSize = cacheSize;
    }
}

