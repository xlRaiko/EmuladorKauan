/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.read;

import ch.qos.logback.core.AppenderBase;
import java.util.ArrayList;
import java.util.List;

public class ListAppender<E>
extends AppenderBase<E> {
    public List<E> list = new ArrayList();

    @Override
    protected void append(E e) {
        this.list.add(e);
    }
}

