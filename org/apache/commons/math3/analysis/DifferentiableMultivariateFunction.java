/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.math3.analysis;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.analysis.MultivariateVectorFunction;

@Deprecated
public interface DifferentiableMultivariateFunction
extends MultivariateFunction {
    public MultivariateFunction partialDerivative(int var1);

    public MultivariateVectorFunction gradient();
}

