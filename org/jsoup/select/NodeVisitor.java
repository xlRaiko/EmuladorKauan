/*
 * Decompiled with CFR 0.152.
 */
package org.jsoup.select;

import org.jsoup.nodes.Node;

public interface NodeVisitor {
    public void head(Node var1, int var2);

    public void tail(Node var1, int var2);
}

