/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.math3.optim;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface ConvergenceChecker<PAIR> {
    public boolean converged(int var1, PAIR var2, PAIR var3);
}

