/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.classic;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AsyncAppenderBase;

public class AsyncAppender
extends AsyncAppenderBase<ILoggingEvent> {
    boolean includeCallerData = false;

    @Override
    protected boolean isDiscardable(ILoggingEvent event) {
        Level level = event.getLevel();
        return level.toInt() <= 20000;
    }

    @Override
    protected void preprocess(ILoggingEvent eventObject) {
        eventObject.prepareForDeferredProcessing();
        if (this.includeCallerData) {
            eventObject.getCallerData();
        }
    }

    public boolean isIncludeCallerData() {
        return this.includeCallerData;
    }

    public void setIncludeCallerData(boolean includeCallerData) {
        this.includeCallerData = includeCallerData;
    }
}

