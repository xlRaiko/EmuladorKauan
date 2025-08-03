/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util;

import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.AttributeMap;
import io.netty.util.internal.ObjectUtil;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class DefaultAttributeMap
implements AttributeMap {
    private static final AtomicReferenceFieldUpdater<DefaultAttributeMap, AtomicReferenceArray> updater = AtomicReferenceFieldUpdater.newUpdater(DefaultAttributeMap.class, AtomicReferenceArray.class, "attributes");
    private static final int BUCKET_SIZE = 4;
    private static final int MASK = 3;
    private volatile AtomicReferenceArray<DefaultAttribute<?>> attributes;

    @Override
    public <T> Attribute<T> attr(AttributeKey<T> key) {
        int i;
        DefaultAttribute<Object> head;
        ObjectUtil.checkNotNull(key, "key");
        AtomicReferenceArray<DefaultAttribute<Object>> attributes = this.attributes;
        if (attributes == null && !updater.compareAndSet(this, null, attributes = new AtomicReferenceArray(4))) {
            attributes = this.attributes;
        }
        if ((head = attributes.get(i = DefaultAttributeMap.index(key))) == null) {
            head = new DefaultAttribute();
            DefaultAttribute<T> attr = new DefaultAttribute<T>(head, key);
            ((DefaultAttribute)head).next = (DefaultAttribute)attr;
            ((DefaultAttribute)attr).prev = (DefaultAttribute)head;
            if (attributes.compareAndSet(i, null, head)) {
                return attr;
            }
            head = attributes.get(i);
        }
        DefaultAttribute<?> defaultAttribute = head;
        synchronized (defaultAttribute) {
            DefaultAttribute curr = head;
            while (true) {
                DefaultAttribute next;
                if ((next = curr.next) == null) {
                    DefaultAttribute<T> attr = new DefaultAttribute<T>(head, key);
                    curr.next = (DefaultAttribute)attr;
                    ((DefaultAttribute)attr).prev = curr;
                    return attr;
                }
                if (next.key == key && !next.removed) {
                    return next;
                }
                curr = next;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public <T> boolean hasAttr(AttributeKey<T> key) {
        ObjectUtil.checkNotNull(key, "key");
        AtomicReferenceArray<DefaultAttribute<?>> attributes = this.attributes;
        if (attributes == null) {
            return false;
        }
        int i = DefaultAttributeMap.index(key);
        DefaultAttribute<?> head = attributes.get(i);
        if (head == null) {
            return false;
        }
        DefaultAttribute<?> defaultAttribute = head;
        synchronized (defaultAttribute) {
            DefaultAttribute curr = ((DefaultAttribute)head).next;
            while (curr != null) {
                if (curr.key == key && !curr.removed) {
                    return true;
                }
                curr = curr.next;
            }
            return false;
        }
    }

    private static int index(AttributeKey<?> key) {
        return key.id() & 3;
    }

    private static final class DefaultAttribute<T>
    extends AtomicReference<T>
    implements Attribute<T> {
        private static final long serialVersionUID = -2661411462200283011L;
        private final DefaultAttribute<?> head;
        private final AttributeKey<T> key;
        private DefaultAttribute<?> prev;
        private DefaultAttribute<?> next;
        private volatile boolean removed;

        DefaultAttribute(DefaultAttribute<?> head, AttributeKey<T> key) {
            this.head = head;
            this.key = key;
        }

        DefaultAttribute() {
            this.head = this;
            this.key = null;
        }

        @Override
        public AttributeKey<T> key() {
            return this.key;
        }

        @Override
        public T setIfAbsent(T value) {
            while (!this.compareAndSet(null, value)) {
                Object old = this.get();
                if (old == null) continue;
                return (T)old;
            }
            return null;
        }

        @Override
        public T getAndRemove() {
            this.removed = true;
            T oldValue = this.getAndSet(null);
            this.remove0();
            return oldValue;
        }

        @Override
        public void remove() {
            this.removed = true;
            this.set(null);
            this.remove0();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void remove0() {
            DefaultAttribute<?> defaultAttribute = this.head;
            synchronized (defaultAttribute) {
                if (this.prev == null) {
                    return;
                }
                this.prev.next = this.next;
                if (this.next != null) {
                    this.next.prev = this.prev;
                }
                this.prev = null;
                this.next = null;
            }
        }
    }
}

