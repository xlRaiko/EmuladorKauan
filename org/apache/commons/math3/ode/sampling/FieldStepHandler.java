/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.math3.ode.sampling;

import org.apache.commons.math3.RealFieldElement;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.ode.FieldODEStateAndDerivative;
import org.apache.commons.math3.ode.sampling.FieldStepInterpolator;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface FieldStepHandler<T extends RealFieldElement<T>> {
    public void init(FieldODEStateAndDerivative<T> var1, T var2);

    public void handleStep(FieldStepInterpolator<T> var1, boolean var2) throws MaxCountExceededException;
}

