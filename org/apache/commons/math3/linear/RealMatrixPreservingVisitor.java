/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.math3.linear;

public interface RealMatrixPreservingVisitor {
    public void start(int var1, int var2, int var3, int var4, int var5, int var6);

    public void visit(int var1, int var2, double var3);

    public double end();
}

