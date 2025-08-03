/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.math3.analysis.function;

import org.apache.commons.math3.analysis.DifferentiableUnivariateFunction;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;
import org.apache.commons.math3.analysis.function.Sinh;
import org.apache.commons.math3.util.FastMath;

public class Cosh
implements UnivariateDifferentiableFunction,
DifferentiableUnivariateFunction {
    public double value(double x) {
        return FastMath.cosh(x);
    }

    @Deprecated
    public DifferentiableUnivariateFunction derivative() {
        return new Sinh();
    }

    public DerivativeStructure value(DerivativeStructure t) {
        return t.cosh();
    }
}

