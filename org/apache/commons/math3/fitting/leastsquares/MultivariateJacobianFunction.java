/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.math3.fitting.leastsquares;

import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.util.Pair;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface MultivariateJacobianFunction {
    public Pair<RealVector, RealMatrix> value(RealVector var1);
}

