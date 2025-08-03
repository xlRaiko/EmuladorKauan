/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.math3.distribution;

import org.apache.commons.math3.special.Gamma;
import org.apache.commons.math3.util.FastMath;

final class SaddlePointExpansion {
    private static final double HALF_LOG_2_PI = 0.5 * FastMath.log(Math.PI * 2);
    private static final double[] EXACT_STIRLING_ERRORS = new double[]{0.0, 0.15342640972002736, 0.08106146679532726, 0.05481412105191765, 0.0413406959554093, 0.03316287351993629, 0.02767792568499834, 0.023746163656297496, 0.020790672103765093, 0.018488450532673187, 0.016644691189821193, 0.015134973221917378, 0.013876128823070748, 0.012810465242920227, 0.01189670994589177, 0.011104559758206917, 0.010411265261972096, 0.009799416126158804, 0.009255462182712733, 0.008768700134139386, 0.00833056343336287, 0.00793411456431402, 0.007573675487951841, 0.007244554301320383, 0.00694284010720953, 0.006665247032707682, 0.006408994188004207, 0.006171712263039458, 0.0059513701127588475, 0.0057462165130101155, 0.005554733551962801};

    private SaddlePointExpansion() {
    }

    static double getStirlingError(double z) {
        double ret;
        if (z < 15.0) {
            double z2 = 2.0 * z;
            ret = FastMath.floor(z2) == z2 ? EXACT_STIRLING_ERRORS[(int)z2] : Gamma.logGamma(z + 1.0) - (z + 0.5) * FastMath.log(z) + z - HALF_LOG_2_PI;
        } else {
            double z2 = z * z;
            ret = (0.08333333333333333 - (0.002777777777777778 - (7.936507936507937E-4 - (5.952380952380953E-4 - 8.417508417508417E-4 / z2) / z2) / z2) / z2) / z;
        }
        return ret;
    }

    static double getDeviancePart(double x, double mu) {
        double ret;
        if (FastMath.abs(x - mu) < 0.1 * (x + mu)) {
            double d = x - mu;
            double v = d / (x + mu);
            double s1 = v * d;
            double s = Double.NaN;
            double ej = 2.0 * x * v;
            v *= v;
            int j = 1;
            while (s1 != s) {
                s = s1;
                s1 = s + (ej *= v) / (double)(j * 2 + 1);
                ++j;
            }
            ret = s1;
        } else {
            ret = x * FastMath.log(x / mu) + mu - x;
        }
        return ret;
    }

    static double logBinomialProbability(int x, int n, double p, double q) {
        double ret;
        if (x == 0) {
            ret = p < 0.1 ? -SaddlePointExpansion.getDeviancePart(n, (double)n * q) - (double)n * p : (double)n * FastMath.log(q);
        } else if (x == n) {
            ret = q < 0.1 ? -SaddlePointExpansion.getDeviancePart(n, (double)n * p) - (double)n * q : (double)n * FastMath.log(p);
        } else {
            ret = SaddlePointExpansion.getStirlingError(n) - SaddlePointExpansion.getStirlingError(x) - SaddlePointExpansion.getStirlingError(n - x) - SaddlePointExpansion.getDeviancePart(x, (double)n * p) - SaddlePointExpansion.getDeviancePart(n - x, (double)n * q);
            double f = Math.PI * 2 * (double)x * (double)(n - x) / (double)n;
            ret = -0.5 * FastMath.log(f) + ret;
        }
        return ret;
    }
}

