/*
 * Decompiled with CFR 0.152.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.impl.unmodifiable.TUnmodifiableIntCollection;
import gnu.trove.set.TIntSet;
import java.io.Serializable;

public class TUnmodifiableIntSet
extends TUnmodifiableIntCollection
implements TIntSet,
Serializable {
    private static final long serialVersionUID = -9215047833775013803L;

    public TUnmodifiableIntSet(TIntSet s) {
        super(s);
    }

    public boolean equals(Object o) {
        return o == this || ((Object)this.c).equals(o);
    }

    public int hashCode() {
        return ((Object)this.c).hashCode();
    }
}

