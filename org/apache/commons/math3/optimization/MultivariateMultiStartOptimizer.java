/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.math3.optimization;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.optimization.BaseMultivariateMultiStartOptimizer;
import org.apache.commons.math3.optimization.MultivariateOptimizer;
import org.apache.commons.math3.random.RandomVectorGenerator;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Deprecated
public class MultivariateMultiStartOptimizer
extends BaseMultivariateMultiStartOptimizer<MultivariateFunction>
implements MultivariateOptimizer {
    public MultivariateMultiStartOptimizer(MultivariateOptimizer optimizer, int starts, RandomVectorGenerator generator) {
        super(optimizer, starts, generator);
    }
}

