/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.math3.optimization;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.optimization.BaseMultivariateOptimizer;
import org.apache.commons.math3.optimization.GoalType;
import org.apache.commons.math3.optimization.PointValuePair;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Deprecated
public interface BaseMultivariateSimpleBoundsOptimizer<FUNC extends MultivariateFunction>
extends BaseMultivariateOptimizer<FUNC> {
    public PointValuePair optimize(int var1, FUNC var2, GoalType var3, double[] var4, double[] var5, double[] var6);
}

