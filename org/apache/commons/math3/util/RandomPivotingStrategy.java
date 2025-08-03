/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.math3.util;

import java.io.Serializable;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.util.PivotingStrategyInterface;

public class RandomPivotingStrategy
implements PivotingStrategyInterface,
Serializable {
    private static final long serialVersionUID = 20140713L;
    private final RandomGenerator random;

    public RandomPivotingStrategy(RandomGenerator random) {
        this.random = random;
    }

    public int pivotIndex(double[] work, int begin, int end) throws MathIllegalArgumentException {
        MathArrays.verifyValues(work, begin, end - begin);
        return begin + this.random.nextInt(end - begin - 1);
    }
}

