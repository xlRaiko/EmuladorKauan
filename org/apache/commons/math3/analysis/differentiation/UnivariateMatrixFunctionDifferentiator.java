/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.math3.analysis.differentiation;

import org.apache.commons.math3.analysis.UnivariateMatrixFunction;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableMatrixFunction;

public interface UnivariateMatrixFunctionDifferentiator {
    public UnivariateDifferentiableMatrixFunction differentiate(UnivariateMatrixFunction var1);
}

