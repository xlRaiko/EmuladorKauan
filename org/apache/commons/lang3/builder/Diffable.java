/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.lang3.builder;

import org.apache.commons.lang3.builder.DiffResult;

@FunctionalInterface
public interface Diffable<T> {
    public DiffResult<T> diff(T var1);
}

