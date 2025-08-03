/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.classic.net;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.pattern.SyslogStartConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.classic.util.LevelToSyslogSeverity;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.net.SyslogAppenderBase;
import ch.qos.logback.core.net.SyslogOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;
import java.net.UnknownHostException;

public class SyslogAppender
extends SyslogAppenderBase<ILoggingEvent> {
    public static final String DEFAULT_SUFFIX_PATTERN = "[%thread] %logger %msg";
    public static final String DEFAULT_STACKTRACE_PATTERN = "\t";
    PatternLayout stackTraceLayout = new PatternLayout();
    String stackTracePattern = "\t";
    boolean throwableExcluded = false;

    @Override
    public void start() {
        super.start();
        this.setupStackTraceLayout();
    }

    String getPrefixPattern() {
        return "%syslogStart{" + this.getFacility() + "}%nopex{}";
    }

    @Override
    public SyslogOutputStream createOutputStream() throws SocketException, UnknownHostException {
        return new SyslogOutputStream(this.getSyslogHost(), this.getPort());
    }

    @Override
    public int getSeverityForEvent(Object eventObject) {
        ILoggingEvent event = (ILoggingEvent)eventObject;
        return LevelToSyslogSeverity.convert(event);
    }

    @Override
    protected void postProcess(Object eventObject, OutputStream sw) {
        if (this.throwableExcluded) {
            return;
        }
        ILoggingEvent event = (ILoggingEvent)eventObject;
        IThrowableProxy tp = event.getThrowableProxy();
        if (tp == null) {
            return;
        }
        String stackTracePrefix = this.stackTraceLayout.doLayout(event);
        boolean isRootException = true;
        while (tp != null) {
            StackTraceElementProxy[] stepArray = tp.getStackTraceElementProxyArray();
            try {
                this.handleThrowableFirstLine(sw, tp, stackTracePrefix, isRootException);
                isRootException = false;
                for (StackTraceElementProxy step : stepArray) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(stackTracePrefix).append(step);
                    sw.write(sb.toString().getBytes());
                    sw.flush();
                }
            }
            catch (IOException e) {
                break;
            }
            tp = tp.getCause();
        }
    }

    private void handleThrowableFirstLine(OutputStream sw, IThrowableProxy tp, String stackTracePrefix, boolean isRootException) throws IOException {
        StringBuilder sb = new StringBuilder().append(stackTracePrefix);
        if (!isRootException) {
            sb.append("Caused by: ");
        }
        sb.append(tp.getClassName()).append(": ").append(tp.getMessage());
        sw.write(sb.toString().getBytes());
        sw.flush();
    }

    boolean stackTraceHeaderLine(StringBuilder sb, boolean topException) {
        return false;
    }

    @Override
    public Layout<ILoggingEvent> buildLayout() {
        PatternLayout layout = new PatternLayout();
        layout.getInstanceConverterMap().put("syslogStart", SyslogStartConverter.class.getName());
        if (this.suffixPattern == null) {
            this.suffixPattern = DEFAULT_SUFFIX_PATTERN;
        }
        layout.setPattern(this.getPrefixPattern() + this.suffixPattern);
        layout.setContext(this.getContext());
        layout.start();
        return layout;
    }

    private void setupStackTraceLayout() {
        this.stackTraceLayout.getInstanceConverterMap().put("syslogStart", SyslogStartConverter.class.getName());
        this.stackTraceLayout.setPattern(this.getPrefixPattern() + this.stackTracePattern);
        this.stackTraceLayout.setContext(this.getContext());
        this.stackTraceLayout.start();
    }

    public boolean isThrowableExcluded() {
        return this.throwableExcluded;
    }

    public void setThrowableExcluded(boolean throwableExcluded) {
        this.throwableExcluded = throwableExcluded;
    }

    public String getStackTracePattern() {
        return this.stackTracePattern;
    }

    public void setStackTracePattern(String stackTracePattern) {
        this.stackTracePattern = stackTracePattern;
    }
}

