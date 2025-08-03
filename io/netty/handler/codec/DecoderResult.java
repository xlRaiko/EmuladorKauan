/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec;

import io.netty.util.Signal;
import io.netty.util.internal.ObjectUtil;

public class DecoderResult {
    protected static final Signal SIGNAL_UNFINISHED = Signal.valueOf(DecoderResult.class, "UNFINISHED");
    protected static final Signal SIGNAL_SUCCESS = Signal.valueOf(DecoderResult.class, "SUCCESS");
    public static final DecoderResult UNFINISHED = new DecoderResult(SIGNAL_UNFINISHED);
    public static final DecoderResult SUCCESS = new DecoderResult(SIGNAL_SUCCESS);
    private final Throwable cause;

    public static DecoderResult failure(Throwable cause) {
        return new DecoderResult(ObjectUtil.checkNotNull(cause, "cause"));
    }

    protected DecoderResult(Throwable cause) {
        this.cause = ObjectUtil.checkNotNull(cause, "cause");
    }

    public boolean isFinished() {
        return this.cause != SIGNAL_UNFINISHED;
    }

    public boolean isSuccess() {
        return this.cause == SIGNAL_SUCCESS;
    }

    public boolean isFailure() {
        return this.cause != SIGNAL_SUCCESS && this.cause != SIGNAL_UNFINISHED;
    }

    public Throwable cause() {
        if (this.isFailure()) {
            return this.cause;
        }
        return null;
    }

    public String toString() {
        if (this.isFinished()) {
            if (this.isSuccess()) {
                return "success";
            }
            String cause = this.cause().toString();
            return new StringBuilder(cause.length() + 17).append("failure(").append(cause).append(')').toString();
        }
        return "unfinished";
    }
}

