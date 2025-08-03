/*
 * Decompiled with CFR 0.152.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TDoubleCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.iterator.TIntDoubleIterator;
import gnu.trove.map.TIntDoubleMap;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TIntDoubleProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.TIntSet;
import java.io.Serializable;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class TUnmodifiableIntDoubleMap
implements TIntDoubleMap,
Serializable {
    private static final long serialVersionUID = -1034234728574286014L;
    private final TIntDoubleMap m;
    private transient TIntSet keySet = null;
    private transient TDoubleCollection values = null;

    public TUnmodifiableIntDoubleMap(TIntDoubleMap m) {
        if (m == null) {
            throw new NullPointerException();
        }
        this.m = m;
    }

    @Override
    public int size() {
        return this.m.size();
    }

    @Override
    public boolean isEmpty() {
        return this.m.isEmpty();
    }

    @Override
    public boolean containsKey(int key) {
        return this.m.containsKey(key);
    }

    @Override
    public boolean containsValue(double val) {
        return this.m.containsValue(val);
    }

    @Override
    public double get(int key) {
        return this.m.get(key);
    }

    @Override
    public double put(int key, double value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double remove(int key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TIntDoubleMap m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Integer, ? extends Double> map) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public TIntSet keySet() {
        if (this.keySet == null) {
            this.keySet = TCollections.unmodifiableSet(this.m.keySet());
        }
        return this.keySet;
    }

    @Override
    public int[] keys() {
        return this.m.keys();
    }

    @Override
    public int[] keys(int[] array) {
        return this.m.keys(array);
    }

    @Override
    public TDoubleCollection valueCollection() {
        if (this.values == null) {
            this.values = TCollections.unmodifiableCollection(this.m.valueCollection());
        }
        return this.values;
    }

    @Override
    public double[] values() {
        return this.m.values();
    }

    @Override
    public double[] values(double[] array) {
        return this.m.values(array);
    }

    public boolean equals(Object o) {
        return o == this || this.m.equals(o);
    }

    public int hashCode() {
        return this.m.hashCode();
    }

    public String toString() {
        return this.m.toString();
    }

    @Override
    public int getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public double getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TIntProcedure procedure) {
        return this.m.forEachKey(procedure);
    }

    @Override
    public boolean forEachValue(TDoubleProcedure procedure) {
        return this.m.forEachValue(procedure);
    }

    @Override
    public boolean forEachEntry(TIntDoubleProcedure procedure) {
        return this.m.forEachEntry(procedure);
    }

    @Override
    public TIntDoubleIterator iterator() {
        return new TIntDoubleIterator(){
            TIntDoubleIterator iter;
            {
                this.iter = TUnmodifiableIntDoubleMap.this.m.iterator();
            }

            public int key() {
                return this.iter.key();
            }

            public double value() {
                return this.iter.value();
            }

            public void advance() {
                this.iter.advance();
            }

            public boolean hasNext() {
                return this.iter.hasNext();
            }

            public double setValue(double val) {
                throw new UnsupportedOperationException();
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public double putIfAbsent(int key, double value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TDoubleFunction function) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TIntDoubleProcedure procedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(int key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(int key, double amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double adjustOrPutValue(int key, double adjust_amount, double put_amount) {
        throw new UnsupportedOperationException();
    }
}

