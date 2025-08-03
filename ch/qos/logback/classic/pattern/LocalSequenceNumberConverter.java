/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.classic.pattern;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import java.util.concurrent.atomic.AtomicLong;

public class LocalSequenceNumberConverter
extends ClassicConverter {
    AtomicLong sequenceNumber = new AtomicLong(System.currentTimeMillis());

    @Override
    public String convert(ILoggingEvent event) {
        return Long.toString(this.sequenceNumber.getAndIncrement());
    }
}

