/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.math3.ml.neuralnet;

import org.apache.commons.math3.ml.neuralnet.Network;

public interface UpdateAction {
    public void update(Network var1, double[] var2);
}

