/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.math3.analysis.function;

import org.apache.commons.math3.analysis.BivariateFunction;

public class Add
implements BivariateFunction {
    public double value(double x, double y) {
        return x + y;
    }
}

