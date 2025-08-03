/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.math3.exception;

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.Localizable;

public class MathIllegalNumberException
extends MathIllegalArgumentException {
    protected static final Integer INTEGER_ZERO = 0;
    private static final long serialVersionUID = -7447085893598031110L;
    private final Number argument;

    protected MathIllegalNumberException(Localizable pattern, Number wrong, Object ... arguments) {
        super(pattern, wrong, arguments);
        this.argument = wrong;
    }

    public Number getArgument() {
        return this.argument;
    }
}

