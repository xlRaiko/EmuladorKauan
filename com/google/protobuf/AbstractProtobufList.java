/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.Internal;
import java.util.AbstractList;
import java.util.Collection;
import java.util.List;
import java.util.RandomAccess;

abstract class AbstractProtobufList<E>
extends AbstractList<E>
implements Internal.ProtobufList<E> {
    protected static final int DEFAULT_CAPACITY = 10;
    private boolean isMutable = true;

    AbstractProtobufList() {
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof List)) {
            return false;
        }
        if (!(o instanceof RandomAccess)) {
            return super.equals(o);
        }
        List other = (List)o;
        int size = this.size();
        if (size != other.size()) {
            return false;
        }
        for (int i = 0; i < size; ++i) {
            if (this.get(i).equals(other.get(i))) continue;
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int size = this.size();
        int hashCode = 1;
        for (int i = 0; i < size; ++i) {
            hashCode = 31 * hashCode + this.get(i).hashCode();
        }
        return hashCode;
    }

    @Override
    public boolean add(E e) {
        this.ensureIsMutable();
        return super.add(e);
    }

    @Override
    public void add(int index, E element) {
        this.ensureIsMutable();
        super.add(index, element);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        this.ensureIsMutable();
        return super.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        this.ensureIsMutable();
        return super.addAll(index, c);
    }

    @Override
    public void clear() {
        this.ensureIsMutable();
        super.clear();
    }

    @Override
    public boolean isModifiable() {
        return this.isMutable;
    }

    @Override
    public final void makeImmutable() {
        this.isMutable = false;
    }

    @Override
    public E remove(int index) {
        this.ensureIsMutable();
        return super.remove(index);
    }

    @Override
    public boolean remove(Object o) {
        this.ensureIsMutable();
        return super.remove(o);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        this.ensureIsMutable();
        return super.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        this.ensureIsMutable();
        return super.retainAll(c);
    }

    @Override
    public E set(int index, E element) {
        this.ensureIsMutable();
        return super.set(index, element);
    }

    protected void ensureIsMutable() {
        if (!this.isMutable) {
            throw new UnsupportedOperationException();
        }
    }
}

