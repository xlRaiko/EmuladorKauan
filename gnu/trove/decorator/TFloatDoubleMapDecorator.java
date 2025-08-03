/*
 * Decompiled with CFR 0.152.
 */
package gnu.trove.decorator;

import gnu.trove.iterator.TFloatDoubleIterator;
import gnu.trove.map.TFloatDoubleMap;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class TFloatDoubleMapDecorator
extends AbstractMap<Float, Double>
implements Map<Float, Double>,
Externalizable,
Cloneable {
    static final long serialVersionUID = 1L;
    protected TFloatDoubleMap _map;

    public TFloatDoubleMapDecorator() {
    }

    public TFloatDoubleMapDecorator(TFloatDoubleMap map) {
        this._map = map;
    }

    public TFloatDoubleMap getMap() {
        return this._map;
    }

    @Override
    public Double put(Float key, Double value) {
        double v;
        float k = key == null ? this._map.getNoEntryKey() : this.unwrapKey(key);
        double retval = this._map.put(k, v = value == null ? this._map.getNoEntryValue() : this.unwrapValue(value));
        if (retval == this._map.getNoEntryValue()) {
            return null;
        }
        return this.wrapValue(retval);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public Double get(Object key) {
        float k;
        if (key != null) {
            if (!(key instanceof Float)) return null;
            k = this.unwrapKey(key);
        } else {
            k = this._map.getNoEntryKey();
        }
        double v = this._map.get(k);
        if (v != this._map.getNoEntryValue()) return this.wrapValue(v);
        return null;
    }

    @Override
    public void clear() {
        this._map.clear();
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public Double remove(Object key) {
        float k;
        if (key != null) {
            if (!(key instanceof Float)) return null;
            k = this.unwrapKey(key);
        } else {
            k = this._map.getNoEntryKey();
        }
        double v = this._map.remove(k);
        if (v != this._map.getNoEntryValue()) return this.wrapValue(v);
        return null;
    }

    @Override
    public Set<Map.Entry<Float, Double>> entrySet() {
        return new AbstractSet<Map.Entry<Float, Double>>(){

            @Override
            public int size() {
                return TFloatDoubleMapDecorator.this._map.size();
            }

            @Override
            public boolean isEmpty() {
                return TFloatDoubleMapDecorator.this.isEmpty();
            }

            @Override
            public boolean contains(Object o) {
                if (o instanceof Map.Entry) {
                    Object k = ((Map.Entry)o).getKey();
                    Object v = ((Map.Entry)o).getValue();
                    return TFloatDoubleMapDecorator.this.containsKey(k) && TFloatDoubleMapDecorator.this.get(k).equals(v);
                }
                return false;
            }

            @Override
            public Iterator<Map.Entry<Float, Double>> iterator() {
                return new Iterator<Map.Entry<Float, Double>>(){
                    private final TFloatDoubleIterator it;
                    {
                        this.it = TFloatDoubleMapDecorator.this._map.iterator();
                    }

                    @Override
                    public Map.Entry<Float, Double> next() {
                        this.it.advance();
                        float ik = this.it.key();
                        final Float key = ik == TFloatDoubleMapDecorator.this._map.getNoEntryKey() ? null : TFloatDoubleMapDecorator.this.wrapKey(ik);
                        double iv = this.it.value();
                        final Double v = iv == TFloatDoubleMapDecorator.this._map.getNoEntryValue() ? null : TFloatDoubleMapDecorator.this.wrapValue(iv);
                        return new Map.Entry<Float, Double>(){
                            private Double val;
                            {
                                this.val = v;
                            }

                            @Override
                            public boolean equals(Object o) {
                                return o instanceof Map.Entry && ((Map.Entry)o).getKey().equals(key) && ((Map.Entry)o).getValue().equals(this.val);
                            }

                            @Override
                            public Float getKey() {
                                return key;
                            }

                            @Override
                            public Double getValue() {
                                return this.val;
                            }

                            @Override
                            public int hashCode() {
                                return key.hashCode() + this.val.hashCode();
                            }

                            @Override
                            public Double setValue(Double value) {
                                this.val = value;
                                return TFloatDoubleMapDecorator.this.put(key, value);
                            }
                        };
                    }

                    @Override
                    public boolean hasNext() {
                        return this.it.hasNext();
                    }

                    @Override
                    public void remove() {
                        this.it.remove();
                    }
                };
            }

            @Override
            public boolean add(Map.Entry<Float, Double> o) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean remove(Object o) {
                boolean modified = false;
                if (this.contains(o)) {
                    Float key = (Float)((Map.Entry)o).getKey();
                    TFloatDoubleMapDecorator.this._map.remove(TFloatDoubleMapDecorator.this.unwrapKey(key));
                    modified = true;
                }
                return modified;
            }

            @Override
            public boolean addAll(Collection<? extends Map.Entry<Float, Double>> c) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void clear() {
                TFloatDoubleMapDecorator.this.clear();
            }
        };
    }

    @Override
    public boolean containsValue(Object val) {
        return val instanceof Double && this._map.containsValue(this.unwrapValue(val));
    }

    @Override
    public boolean containsKey(Object key) {
        if (key == null) {
            return this._map.containsKey(this._map.getNoEntryKey());
        }
        return key instanceof Float && this._map.containsKey(this.unwrapKey(key));
    }

    @Override
    public int size() {
        return this._map.size();
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    @Override
    public void putAll(Map<? extends Float, ? extends Double> map) {
        Iterator<Map.Entry<? extends Float, ? extends Double>> it = map.entrySet().iterator();
        int i = map.size();
        while (i-- > 0) {
            Map.Entry<? extends Float, ? extends Double> e = it.next();
            this.put(e.getKey(), e.getValue());
        }
    }

    protected Float wrapKey(float k) {
        return Float.valueOf(k);
    }

    protected float unwrapKey(Object key) {
        return ((Float)key).floatValue();
    }

    protected Double wrapValue(double k) {
        return k;
    }

    protected double unwrapValue(Object value) {
        return (Double)value;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        this._map = (TFloatDoubleMap)in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte(0);
        out.writeObject(this._map);
    }
}

