/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.math3.optim.nonlinear.scalar;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.optim.OptimizationData;

public class ObjectiveFunction
implements OptimizationData {
    private final MultivariateFunction function;

    public ObjectiveFunction(MultivariateFunction f) {
        this.function = f;
    }

    public MultivariateFunction getObjectiveFunction() {
        return this.function;
    }
}

