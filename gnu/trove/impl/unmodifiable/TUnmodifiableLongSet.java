/*
 * Decompiled with CFR 0.152.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.impl.unmodifiable.TUnmodifiableLongCollection;
import gnu.trove.set.TLongSet;
import java.io.Serializable;

public class TUnmodifiableLongSet
extends TUnmodifiableLongCollection
implements TLongSet,
Serializable {
    private static final long serialVersionUID = -9215047833775013803L;

    public TUnmodifiableLongSet(TLongSet s) {
        super(s);
    }

    public boolean equals(Object o) {
        return o == this || ((Object)this.c).equals(o);
    }

    public int hashCode() {
        return ((Object)this.c).hashCode();
    }
}

