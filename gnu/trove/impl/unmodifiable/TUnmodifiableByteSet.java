/*
 * Decompiled with CFR 0.152.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.impl.unmodifiable.TUnmodifiableByteCollection;
import gnu.trove.set.TByteSet;
import java.io.Serializable;

public class TUnmodifiableByteSet
extends TUnmodifiableByteCollection
implements TByteSet,
Serializable {
    private static final long serialVersionUID = -9215047833775013803L;

    public TUnmodifiableByteSet(TByteSet s) {
        super(s);
    }

    public boolean equals(Object o) {
        return o == this || ((Object)this.c).equals(o);
    }

    public int hashCode() {
        return ((Object)this.c).hashCode();
    }
}

