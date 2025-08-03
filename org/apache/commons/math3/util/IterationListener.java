/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.math3.util;

import java.util.EventListener;
import org.apache.commons.math3.util.IterationEvent;

public interface IterationListener
extends EventListener {
    public void initializationPerformed(IterationEvent var1);

    public void iterationPerformed(IterationEvent var1);

    public void iterationStarted(IterationEvent var1);

    public void terminationPerformed(IterationEvent var1);
}

