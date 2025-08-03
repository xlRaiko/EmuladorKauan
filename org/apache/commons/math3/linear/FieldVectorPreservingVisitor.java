/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.math3.linear;

import org.apache.commons.math3.FieldElement;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface FieldVectorPreservingVisitor<T extends FieldElement<?>> {
    public void start(int var1, int var2, int var3);

    public void visit(int var1, T var2);

    public T end();
}

