/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.pattern;

import ch.qos.logback.core.pattern.Converter;
import ch.qos.logback.core.pattern.FormatInfo;
import ch.qos.logback.core.pattern.SpacePadder;

public abstract class FormattingConverter<E>
extends Converter<E> {
    static final int INITIAL_BUF_SIZE = 256;
    static final int MAX_CAPACITY = 1024;
    FormatInfo formattingInfo;

    public final FormatInfo getFormattingInfo() {
        return this.formattingInfo;
    }

    public final void setFormattingInfo(FormatInfo formattingInfo) {
        if (this.formattingInfo != null) {
            throw new IllegalStateException("FormattingInfo has been already set");
        }
        this.formattingInfo = formattingInfo;
    }

    @Override
    public final void write(StringBuilder buf, E event) {
        String s = this.convert(event);
        if (this.formattingInfo == null) {
            buf.append(s);
            return;
        }
        int min = this.formattingInfo.getMin();
        int max = this.formattingInfo.getMax();
        if (s == null) {
            if (0 < min) {
                SpacePadder.spacePad(buf, min);
            }
            return;
        }
        int len = s.length();
        if (len > max) {
            if (this.formattingInfo.isLeftTruncate()) {
                buf.append(s.substring(len - max));
            } else {
                buf.append(s.substring(0, max));
            }
        } else if (len < min) {
            if (this.formattingInfo.isLeftPad()) {
                SpacePadder.leftPad(buf, s, min);
            } else {
                SpacePadder.rightPad(buf, s, min);
            }
        } else {
            buf.append(s);
        }
    }
}

