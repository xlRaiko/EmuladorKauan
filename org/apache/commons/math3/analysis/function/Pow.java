/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.math3.analysis.function;

import org.apache.commons.math3.analysis.BivariateFunction;
import org.apache.commons.math3.util.FastMath;

public class Pow
implements BivariateFunction {
    public double value(double x, double y) {
        return FastMath.pow(x, y);
    }
}

