/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.BooleanArrayList;
import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.DoubleArrayList;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.FloatArrayList;
import com.google.protobuf.IntArrayList;
import com.google.protobuf.Internal;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.LazyStringList;
import com.google.protobuf.LongArrayList;
import com.google.protobuf.MapEntryLite;
import com.google.protobuf.Protobuf;
import com.google.protobuf.Reader;
import com.google.protobuf.Schema;
import com.google.protobuf.Utf8;
import com.google.protobuf.WireFormat;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

abstract class BinaryReader
implements Reader {
    private static final int FIXED32_MULTIPLE_MASK = 3;
    private static final int FIXED64_MULTIPLE_MASK = 7;

    public static BinaryReader newInstance(ByteBuffer buffer, boolean bufferIsImmutable) {
        if (buffer.hasArray()) {
            return new SafeHeapReader(buffer, bufferIsImmutable);
        }
        throw new IllegalArgumentException("Direct buffers not yet supported");
    }

    private BinaryReader() {
    }

    public abstract int getTotalBytesRead();

    @Override
    public boolean shouldDiscardUnknownFields() {
        return false;
    }

    private static final class SafeHeapReader
    extends BinaryReader {
        private final boolean bufferIsImmutable;
        private final byte[] buffer;
        private int pos;
        private final int initialPos;
        private int limit;
        private int tag;
        private int endGroupTag;

        public SafeHeapReader(ByteBuffer bytebuf, boolean bufferIsImmutable) {
            this.bufferIsImmutable = bufferIsImmutable;
            this.buffer = bytebuf.array();
            this.initialPos = this.pos = bytebuf.arrayOffset() + bytebuf.position();
            this.limit = bytebuf.arrayOffset() + bytebuf.limit();
        }

        private boolean isAtEnd() {
            return this.pos == this.limit;
        }

        @Override
        public int getTotalBytesRead() {
            return this.pos - this.initialPos;
        }

        @Override
        public int getFieldNumber() throws IOException {
            if (this.isAtEnd()) {
                return Integer.MAX_VALUE;
            }
            this.tag = this.readVarint32();
            if (this.tag == this.endGroupTag) {
                return Integer.MAX_VALUE;
            }
            return WireFormat.getTagFieldNumber(this.tag);
        }

        @Override
        public int getTag() {
            return this.tag;
        }

        @Override
        public boolean skipField() throws IOException {
            if (this.isAtEnd() || this.tag == this.endGroupTag) {
                return false;
            }
            switch (WireFormat.getTagWireType(this.tag)) {
                case 0: {
                    this.skipVarint();
                    return true;
                }
                case 1: {
                    this.skipBytes(8);
                    return true;
                }
                case 2: {
                    this.skipBytes(this.readVarint32());
                    return true;
                }
                case 5: {
                    this.skipBytes(4);
                    return true;
                }
                case 3: {
                    this.skipGroup();
                    return true;
                }
            }
            throw InvalidProtocolBufferException.invalidWireType();
        }

        @Override
        public double readDouble() throws IOException {
            this.requireWireType(1);
            return Double.longBitsToDouble(this.readLittleEndian64());
        }

        @Override
        public float readFloat() throws IOException {
            this.requireWireType(5);
            return Float.intBitsToFloat(this.readLittleEndian32());
        }

        @Override
        public long readUInt64() throws IOException {
            this.requireWireType(0);
            return this.readVarint64();
        }

        @Override
        public long readInt64() throws IOException {
            this.requireWireType(0);
            return this.readVarint64();
        }

        @Override
        public int readInt32() throws IOException {
            this.requireWireType(0);
            return this.readVarint32();
        }

        @Override
        public long readFixed64() throws IOException {
            this.requireWireType(1);
            return this.readLittleEndian64();
        }

        @Override
        public int readFixed32() throws IOException {
            this.requireWireType(5);
            return this.readLittleEndian32();
        }

        @Override
        public boolean readBool() throws IOException {
            this.requireWireType(0);
            return this.readVarint32() != 0;
        }

        @Override
        public String readString() throws IOException {
            return this.readStringInternal(false);
        }

        @Override
        public String readStringRequireUtf8() throws IOException {
            return this.readStringInternal(true);
        }

        public String readStringInternal(boolean requireUtf8) throws IOException {
            this.requireWireType(2);
            int size = this.readVarint32();
            if (size == 0) {
                return "";
            }
            this.requireBytes(size);
            if (requireUtf8 && !Utf8.isValidUtf8(this.buffer, this.pos, this.pos + size)) {
                throw InvalidProtocolBufferException.invalidUtf8();
            }
            String result = new String(this.buffer, this.pos, size, Internal.UTF_8);
            this.pos += size;
            return result;
        }

        @Override
        public <T> T readMessage(Class<T> clazz, ExtensionRegistryLite extensionRegistry) throws IOException {
            this.requireWireType(2);
            return (T)this.readMessage(Protobuf.getInstance().schemaFor(clazz), extensionRegistry);
        }

        @Override
        public <T> T readMessageBySchemaWithCheck(Schema<T> schema, ExtensionRegistryLite extensionRegistry) throws IOException {
            this.requireWireType(2);
            return this.readMessage(schema, extensionRegistry);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private <T> T readMessage(Schema<T> schema, ExtensionRegistryLite extensionRegistry) throws IOException {
            int newLimit;
            int size = this.readVarint32();
            this.requireBytes(size);
            int prevLimit = this.limit;
            this.limit = newLimit = this.pos + size;
            try {
                T message = schema.newInstance();
                schema.mergeFrom(message, this, extensionRegistry);
                schema.makeImmutable(message);
                if (this.pos != newLimit) {
                    throw InvalidProtocolBufferException.parseFailure();
                }
                T t = message;
                return t;
            }
            finally {
                this.limit = prevLimit;
            }
        }

        @Override
        public <T> T readGroup(Class<T> clazz, ExtensionRegistryLite extensionRegistry) throws IOException {
            this.requireWireType(3);
            return (T)this.readGroup(Protobuf.getInstance().schemaFor(clazz), extensionRegistry);
        }

        @Override
        public <T> T readGroupBySchemaWithCheck(Schema<T> schema, ExtensionRegistryLite extensionRegistry) throws IOException {
            this.requireWireType(3);
            return this.readGroup(schema, extensionRegistry);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private <T> T readGroup(Schema<T> schema, ExtensionRegistryLite extensionRegistry) throws IOException {
            int prevEndGroupTag = this.endGroupTag;
            this.endGroupTag = WireFormat.makeTag(WireFormat.getTagFieldNumber(this.tag), 4);
            try {
                T message = schema.newInstance();
                schema.mergeFrom(message, this, extensionRegistry);
                schema.makeImmutable(message);
                if (this.tag != this.endGroupTag) {
                    throw InvalidProtocolBufferException.parseFailure();
                }
                T t = message;
                return t;
            }
            finally {
                this.endGroupTag = prevEndGroupTag;
            }
        }

        @Override
        public ByteString readBytes() throws IOException {
            this.requireWireType(2);
            int size = this.readVarint32();
            if (size == 0) {
                return ByteString.EMPTY;
            }
            this.requireBytes(size);
            ByteString bytes = this.bufferIsImmutable ? ByteString.wrap(this.buffer, this.pos, size) : ByteString.copyFrom(this.buffer, this.pos, size);
            this.pos += size;
            return bytes;
        }

        @Override
        public int readUInt32() throws IOException {
            this.requireWireType(0);
            return this.readVarint32();
        }

        @Override
        public int readEnum() throws IOException {
            this.requireWireType(0);
            return this.readVarint32();
        }

        @Override
        public int readSFixed32() throws IOException {
            this.requireWireType(5);
            return this.readLittleEndian32();
        }

        @Override
        public long readSFixed64() throws IOException {
            this.requireWireType(1);
            return this.readLittleEndian64();
        }

        @Override
        public int readSInt32() throws IOException {
            this.requireWireType(0);
            return CodedInputStream.decodeZigZag32(this.readVarint32());
        }

        @Override
        public long readSInt64() throws IOException {
            this.requireWireType(0);
            return CodedInputStream.decodeZigZag64(this.readVarint64());
        }

        @Override
        public void readDoubleList(List<Double> target) throws IOException {
            block15: {
                block14: {
                    if (!(target instanceof DoubleArrayList)) break block14;
                    DoubleArrayList plist = (DoubleArrayList)target;
                    switch (WireFormat.getTagWireType(this.tag)) {
                        case 2: {
                            int bytes = this.readVarint32();
                            this.verifyPackedFixed64Length(bytes);
                            int fieldEndPos = this.pos + bytes;
                            while (this.pos < fieldEndPos) {
                                plist.addDouble(Double.longBitsToDouble(this.readLittleEndian64_NoCheck()));
                            }
                            break block15;
                        }
                        case 1: {
                            int prevPos;
                            int nextTag;
                            do {
                                plist.addDouble(this.readDouble());
                                if (this.isAtEnd()) {
                                    return;
                                }
                                prevPos = this.pos;
                            } while ((nextTag = this.readVarint32()) == this.tag);
                            this.pos = prevPos;
                            return;
                        }
                        default: {
                            throw InvalidProtocolBufferException.invalidWireType();
                        }
                    }
                }
                switch (WireFormat.getTagWireType(this.tag)) {
                    case 2: {
                        int bytes = this.readVarint32();
                        this.verifyPackedFixed64Length(bytes);
                        int fieldEndPos = this.pos + bytes;
                        while (this.pos < fieldEndPos) {
                            target.add(Double.longBitsToDouble(this.readLittleEndian64_NoCheck()));
                        }
                        break;
                    }
                    case 1: {
                        int prevPos;
                        int nextTag;
                        do {
                            target.add(this.readDouble());
                            if (this.isAtEnd()) {
                                return;
                            }
                            prevPos = this.pos;
                        } while ((nextTag = this.readVarint32()) == this.tag);
                        this.pos = prevPos;
                        return;
                    }
                    default: {
                        throw InvalidProtocolBufferException.invalidWireType();
                    }
                }
            }
        }

        @Override
        public void readFloatList(List<Float> target) throws IOException {
            block15: {
                block14: {
                    if (!(target instanceof FloatArrayList)) break block14;
                    FloatArrayList plist = (FloatArrayList)target;
                    switch (WireFormat.getTagWireType(this.tag)) {
                        case 2: {
                            int bytes = this.readVarint32();
                            this.verifyPackedFixed32Length(bytes);
                            int fieldEndPos = this.pos + bytes;
                            while (this.pos < fieldEndPos) {
                                plist.addFloat(Float.intBitsToFloat(this.readLittleEndian32_NoCheck()));
                            }
                            break block15;
                        }
                        case 5: {
                            int prevPos;
                            int nextTag;
                            do {
                                plist.addFloat(this.readFloat());
                                if (this.isAtEnd()) {
                                    return;
                                }
                                prevPos = this.pos;
                            } while ((nextTag = this.readVarint32()) == this.tag);
                            this.pos = prevPos;
                            return;
                        }
                        default: {
                            throw InvalidProtocolBufferException.invalidWireType();
                        }
                    }
                }
                switch (WireFormat.getTagWireType(this.tag)) {
                    case 2: {
                        int bytes = this.readVarint32();
                        this.verifyPackedFixed32Length(bytes);
                        int fieldEndPos = this.pos + bytes;
                        while (this.pos < fieldEndPos) {
                            target.add(Float.valueOf(Float.intBitsToFloat(this.readLittleEndian32_NoCheck())));
                        }
                        break;
                    }
                    case 5: {
                        int prevPos;
                        int nextTag;
                        do {
                            target.add(Float.valueOf(this.readFloat()));
                            if (this.isAtEnd()) {
                                return;
                            }
                            prevPos = this.pos;
                        } while ((nextTag = this.readVarint32()) == this.tag);
                        this.pos = prevPos;
                        return;
                    }
                    default: {
                        throw InvalidProtocolBufferException.invalidWireType();
                    }
                }
            }
        }

        @Override
        public void readUInt64List(List<Long> target) throws IOException {
            if (target instanceof LongArrayList) {
                LongArrayList plist = (LongArrayList)target;
                switch (WireFormat.getTagWireType(this.tag)) {
                    case 2: {
                        int bytes = this.readVarint32();
                        int fieldEndPos = this.pos + bytes;
                        while (this.pos < fieldEndPos) {
                            plist.addLong(this.readVarint64());
                        }
                        this.requirePosition(fieldEndPos);
                        break;
                    }
                    case 0: {
                        int prevPos;
                        int nextTag;
                        do {
                            plist.addLong(this.readUInt64());
                            if (this.isAtEnd()) {
                                return;
                            }
                            prevPos = this.pos;
                        } while ((nextTag = this.readVarint32()) == this.tag);
                        this.pos = prevPos;
                        return;
                    }
                    default: {
                        throw InvalidProtocolBufferException.invalidWireType();
                    }
                }
            } else {
                switch (WireFormat.getTagWireType(this.tag)) {
                    case 2: {
                        int bytes = this.readVarint32();
                        int fieldEndPos = this.pos + bytes;
                        while (this.pos < fieldEndPos) {
                            target.add(this.readVarint64());
                        }
                        this.requirePosition(fieldEndPos);
                        break;
                    }
                    case 0: {
                        int prevPos;
                        int nextTag;
                        do {
                            target.add(this.readUInt64());
                            if (this.isAtEnd()) {
                                return;
                            }
                            prevPos = this.pos;
                        } while ((nextTag = this.readVarint32()) == this.tag);
                        this.pos = prevPos;
                        return;
                    }
                    default: {
                        throw InvalidProtocolBufferException.invalidWireType();
                    }
                }
            }
        }

        @Override
        public void readInt64List(List<Long> target) throws IOException {
            if (target instanceof LongArrayList) {
                LongArrayList plist = (LongArrayList)target;
                switch (WireFormat.getTagWireType(this.tag)) {
                    case 2: {
                        int bytes = this.readVarint32();
                        int fieldEndPos = this.pos + bytes;
                        while (this.pos < fieldEndPos) {
                            plist.addLong(this.readVarint64());
                        }
                        this.requirePosition(fieldEndPos);
                        break;
                    }
                    case 0: {
                        int prevPos;
                        int nextTag;
                        do {
                            plist.addLong(this.readInt64());
                            if (this.isAtEnd()) {
                                return;
                            }
                            prevPos = this.pos;
                        } while ((nextTag = this.readVarint32()) == this.tag);
                        this.pos = prevPos;
                        return;
                    }
                    default: {
                        throw InvalidProtocolBufferException.invalidWireType();
                    }
                }
            } else {
                switch (WireFormat.getTagWireType(this.tag)) {
                    case 2: {
                        int bytes = this.readVarint32();
                        int fieldEndPos = this.pos + bytes;
                        while (this.pos < fieldEndPos) {
                            target.add(this.readVarint64());
                        }
                        this.requirePosition(fieldEndPos);
                        break;
                    }
                    case 0: {
                        int prevPos;
                        int nextTag;
                        do {
                            target.add(this.readInt64());
                            if (this.isAtEnd()) {
                                return;
                            }
                            prevPos = this.pos;
                        } while ((nextTag = this.readVarint32()) == this.tag);
                        this.pos = prevPos;
                        return;
                    }
                    default: {
                        throw InvalidProtocolBufferException.invalidWireType();
                    }
                }
            }
        }

        @Override
        public void readInt32List(List<Integer> target) throws IOException {
            if (target instanceof IntArrayList) {
                IntArrayList plist = (IntArrayList)target;
                switch (WireFormat.getTagWireType(this.tag)) {
                    case 2: {
                        int bytes = this.readVarint32();
                        int fieldEndPos = this.pos + bytes;
                        while (this.pos < fieldEndPos) {
                            plist.addInt(this.readVarint32());
                        }
                        this.requirePosition(fieldEndPos);
                        break;
                    }
                    case 0: {
                        int prevPos;
                        int nextTag;
                        do {
                            plist.addInt(this.readInt32());
                            if (this.isAtEnd()) {
                                return;
                            }
                            prevPos = this.pos;
                        } while ((nextTag = this.readVarint32()) == this.tag);
                        this.pos = prevPos;
                        return;
                    }
                    default: {
                        throw InvalidProtocolBufferException.invalidWireType();
                    }
                }
            } else {
                switch (WireFormat.getTagWireType(this.tag)) {
                    case 2: {
                        int bytes = this.readVarint32();
                        int fieldEndPos = this.pos + bytes;
                        while (this.pos < fieldEndPos) {
                            target.add(this.readVarint32());
                        }
                        this.requirePosition(fieldEndPos);
                        break;
                    }
                    case 0: {
                        int prevPos;
                        int nextTag;
                        do {
                            target.add(this.readInt32());
                            if (this.isAtEnd()) {
                                return;
                            }
                            prevPos = this.pos;
                        } while ((nextTag = this.readVarint32()) == this.tag);
                        this.pos = prevPos;
                        return;
                    }
                    default: {
                        throw InvalidProtocolBufferException.invalidWireType();
                    }
                }
            }
        }

        @Override
        public void readFixed64List(List<Long> target) throws IOException {
            block15: {
                block14: {
                    if (!(target instanceof LongArrayList)) break block14;
                    LongArrayList plist = (LongArrayList)target;
                    switch (WireFormat.getTagWireType(this.tag)) {
                        case 2: {
                            int bytes = this.readVarint32();
                            this.verifyPackedFixed64Length(bytes);
                            int fieldEndPos = this.pos + bytes;
                            while (this.pos < fieldEndPos) {
                                plist.addLong(this.readLittleEndian64_NoCheck());
                            }
                            break block15;
                        }
                        case 1: {
                            int prevPos;
                            int nextTag;
                            do {
                                plist.addLong(this.readFixed64());
                                if (this.isAtEnd()) {
                                    return;
                                }
                                prevPos = this.pos;
                            } while ((nextTag = this.readVarint32()) == this.tag);
                            this.pos = prevPos;
                            return;
                        }
                        default: {
                            throw InvalidProtocolBufferException.invalidWireType();
                        }
                    }
                }
                switch (WireFormat.getTagWireType(this.tag)) {
                    case 2: {
                        int bytes = this.readVarint32();
                        this.verifyPackedFixed64Length(bytes);
                        int fieldEndPos = this.pos + bytes;
                        while (this.pos < fieldEndPos) {
                            target.add(this.readLittleEndian64_NoCheck());
                        }
                        break;
                    }
                    case 1: {
                        int prevPos;
                        int nextTag;
                        do {
                            target.add(this.readFixed64());
                            if (this.isAtEnd()) {
                                return;
                            }
                            prevPos = this.pos;
                        } while ((nextTag = this.readVarint32()) == this.tag);
                        this.pos = prevPos;
                        return;
                    }
                    default: {
                        throw InvalidProtocolBufferException.invalidWireType();
                    }
                }
            }
        }

        @Override
        public void readFixed32List(List<Integer> target) throws IOException {
            block15: {
                block14: {
                    if (!(target instanceof IntArrayList)) break block14;
                    IntArrayList plist = (IntArrayList)target;
                    switch (WireFormat.getTagWireType(this.tag)) {
                        case 2: {
                            int bytes = this.readVarint32();
                            this.verifyPackedFixed32Length(bytes);
                            int fieldEndPos = this.pos + bytes;
                            while (this.pos < fieldEndPos) {
                                plist.addInt(this.readLittleEndian32_NoCheck());
                            }
                            break block15;
                        }
                        case 5: {
                            int prevPos;
                            int nextTag;
                            do {
                                plist.addInt(this.readFixed32());
                                if (this.isAtEnd()) {
                                    return;
                                }
                                prevPos = this.pos;
                            } while ((nextTag = this.readVarint32()) == this.tag);
                            this.pos = prevPos;
                            return;
                        }
                        default: {
                            throw InvalidProtocolBufferException.invalidWireType();
                        }
                    }
                }
                switch (WireFormat.getTagWireType(this.tag)) {
                    case 2: {
                        int bytes = this.readVarint32();
                        this.verifyPackedFixed32Length(bytes);
                        int fieldEndPos = this.pos + bytes;
                        while (this.pos < fieldEndPos) {
                            target.add(this.readLittleEndian32_NoCheck());
                        }
                        break;
                    }
                    case 5: {
                        int prevPos;
                        int nextTag;
                        do {
                            target.add(this.readFixed32());
                            if (this.isAtEnd()) {
                                return;
                            }
                            prevPos = this.pos;
                        } while ((nextTag = this.readVarint32()) == this.tag);
                        this.pos = prevPos;
                        return;
                    }
                    default: {
                        throw InvalidProtocolBufferException.invalidWireType();
                    }
                }
            }
        }

        @Override
        public void readBoolList(List<Boolean> target) throws IOException {
            if (target instanceof BooleanArrayList) {
                BooleanArrayList plist = (BooleanArrayList)target;
                switch (WireFormat.getTagWireType(this.tag)) {
                    case 2: {
                        int bytes = this.readVarint32();
                        int fieldEndPos = this.pos + bytes;
                        while (this.pos < fieldEndPos) {
                            plist.addBoolean(this.readVarint32() != 0);
                        }
                        this.requirePosition(fieldEndPos);
                        break;
                    }
                    case 0: {
                        int prevPos;
                        int nextTag;
                        do {
                            plist.addBoolean(this.readBool());
                            if (this.isAtEnd()) {
                                return;
                            }
                            prevPos = this.pos;
                        } while ((nextTag = this.readVarint32()) == this.tag);
                        this.pos = prevPos;
                        return;
                    }
                    default: {
                        throw InvalidProtocolBufferException.invalidWireType();
                    }
                }
            } else {
                switch (WireFormat.getTagWireType(this.tag)) {
                    case 2: {
                        int bytes = this.readVarint32();
                        int fieldEndPos = this.pos + bytes;
                        while (this.pos < fieldEndPos) {
                            target.add(this.readVarint32() != 0);
                        }
                        this.requirePosition(fieldEndPos);
                        break;
                    }
                    case 0: {
                        int prevPos;
                        int nextTag;
                        do {
                            target.add(this.readBool());
                            if (this.isAtEnd()) {
                                return;
                            }
                            prevPos = this.pos;
                        } while ((nextTag = this.readVarint32()) == this.tag);
                        this.pos = prevPos;
                        return;
                    }
                    default: {
                        throw InvalidProtocolBufferException.invalidWireType();
                    }
                }
            }
        }

        @Override
        public void readStringList(List<String> target) throws IOException {
            this.readStringListInternal(target, false);
        }

        @Override
        public void readStringListRequireUtf8(List<String> target) throws IOException {
            this.readStringListInternal(target, true);
        }

        public void readStringListInternal(List<String> target, boolean requireUtf8) throws IOException {
            int prevPos;
            int nextTag;
            if (WireFormat.getTagWireType(this.tag) != 2) {
                throw InvalidProtocolBufferException.invalidWireType();
            }
            if (target instanceof LazyStringList && !requireUtf8) {
                int prevPos2;
                int nextTag2;
                LazyStringList lazyList = (LazyStringList)target;
                do {
                    lazyList.add(this.readBytes());
                    if (this.isAtEnd()) {
                        return;
                    }
                    prevPos2 = this.pos;
                } while ((nextTag2 = this.readVarint32()) == this.tag);
                this.pos = prevPos2;
                return;
            }
            do {
                target.add(this.readStringInternal(requireUtf8));
                if (this.isAtEnd()) {
                    return;
                }
                prevPos = this.pos;
            } while ((nextTag = this.readVarint32()) == this.tag);
            this.pos = prevPos;
        }

        @Override
        public <T> void readMessageList(List<T> target, Class<T> targetType, ExtensionRegistryLite extensionRegistry) throws IOException {
            Schema<Class<T>> schema = Protobuf.getInstance().schemaFor(targetType);
            this.readMessageList(target, schema, extensionRegistry);
        }

        @Override
        public <T> void readMessageList(List<T> target, Schema<T> schema, ExtensionRegistryLite extensionRegistry) throws IOException {
            int prevPos;
            int nextTag;
            if (WireFormat.getTagWireType(this.tag) != 2) {
                throw InvalidProtocolBufferException.invalidWireType();
            }
            int listTag = this.tag;
            do {
                target.add(this.readMessage(schema, extensionRegistry));
                if (this.isAtEnd()) {
                    return;
                }
                prevPos = this.pos;
            } while ((nextTag = this.readVarint32()) == listTag);
            this.pos = prevPos;
        }

        @Override
        public <T> void readGroupList(List<T> target, Class<T> targetType, ExtensionRegistryLite extensionRegistry) throws IOException {
            Schema<Class<T>> schema = Protobuf.getInstance().schemaFor(targetType);
            this.readGroupList(target, schema, extensionRegistry);
        }

        @Override
        public <T> void readGroupList(List<T> target, Schema<T> schema, ExtensionRegistryLite extensionRegistry) throws IOException {
            int prevPos;
            int nextTag;
            if (WireFormat.getTagWireType(this.tag) != 3) {
                throw InvalidProtocolBufferException.invalidWireType();
            }
            int listTag = this.tag;
            do {
                target.add(this.readGroup(schema, extensionRegistry));
                if (this.isAtEnd()) {
                    return;
                }
                prevPos = this.pos;
            } while ((nextTag = this.readVarint32()) == listTag);
            this.pos = prevPos;
        }

        @Override
        public void readBytesList(List<ByteString> target) throws IOException {
            int prevPos;
            int nextTag;
            if (WireFormat.getTagWireType(this.tag) != 2) {
                throw InvalidProtocolBufferException.invalidWireType();
            }
            do {
                target.add(this.readBytes());
                if (this.isAtEnd()) {
                    return;
                }
                prevPos = this.pos;
            } while ((nextTag = this.readVarint32()) == this.tag);
            this.pos = prevPos;
        }

        @Override
        public void readUInt32List(List<Integer> target) throws IOException {
            block15: {
                block14: {
                    if (!(target instanceof IntArrayList)) break block14;
                    IntArrayList plist = (IntArrayList)target;
                    switch (WireFormat.getTagWireType(this.tag)) {
                        case 2: {
                            int bytes = this.readVarint32();
                            int fieldEndPos = this.pos + bytes;
                            while (this.pos < fieldEndPos) {
                                plist.addInt(this.readVarint32());
                            }
                            break block15;
                        }
                        case 0: {
                            int prevPos;
                            int nextTag;
                            do {
                                plist.addInt(this.readUInt32());
                                if (this.isAtEnd()) {
                                    return;
                                }
                                prevPos = this.pos;
                            } while ((nextTag = this.readVarint32()) == this.tag);
                            this.pos = prevPos;
                            return;
                        }
                        default: {
                            throw InvalidProtocolBufferException.invalidWireType();
                        }
                    }
                }
                switch (WireFormat.getTagWireType(this.tag)) {
                    case 2: {
                        int bytes = this.readVarint32();
                        int fieldEndPos = this.pos + bytes;
                        while (this.pos < fieldEndPos) {
                            target.add(this.readVarint32());
                        }
                        break;
                    }
                    case 0: {
                        int prevPos;
                        int nextTag;
                        do {
                            target.add(this.readUInt32());
                            if (this.isAtEnd()) {
                                return;
                            }
                            prevPos = this.pos;
                        } while ((nextTag = this.readVarint32()) == this.tag);
                        this.pos = prevPos;
                        return;
                    }
                    default: {
                        throw InvalidProtocolBufferException.invalidWireType();
                    }
                }
            }
        }

        @Override
        public void readEnumList(List<Integer> target) throws IOException {
            block15: {
                block14: {
                    if (!(target instanceof IntArrayList)) break block14;
                    IntArrayList plist = (IntArrayList)target;
                    switch (WireFormat.getTagWireType(this.tag)) {
                        case 2: {
                            int bytes = this.readVarint32();
                            int fieldEndPos = this.pos + bytes;
                            while (this.pos < fieldEndPos) {
                                plist.addInt(this.readVarint32());
                            }
                            break block15;
                        }
                        case 0: {
                            int prevPos;
                            int nextTag;
                            do {
                                plist.addInt(this.readEnum());
                                if (this.isAtEnd()) {
                                    return;
                                }
                                prevPos = this.pos;
                            } while ((nextTag = this.readVarint32()) == this.tag);
                            this.pos = prevPos;
                            return;
                        }
                        default: {
                            throw InvalidProtocolBufferException.invalidWireType();
                        }
                    }
                }
                switch (WireFormat.getTagWireType(this.tag)) {
                    case 2: {
                        int bytes = this.readVarint32();
                        int fieldEndPos = this.pos + bytes;
                        while (this.pos < fieldEndPos) {
                            target.add(this.readVarint32());
                        }
                        break;
                    }
                    case 0: {
                        int prevPos;
                        int nextTag;
                        do {
                            target.add(this.readEnum());
                            if (this.isAtEnd()) {
                                return;
                            }
                            prevPos = this.pos;
                        } while ((nextTag = this.readVarint32()) == this.tag);
                        this.pos = prevPos;
                        return;
                    }
                    default: {
                        throw InvalidProtocolBufferException.invalidWireType();
                    }
                }
            }
        }

        @Override
        public void readSFixed32List(List<Integer> target) throws IOException {
            block15: {
                block14: {
                    if (!(target instanceof IntArrayList)) break block14;
                    IntArrayList plist = (IntArrayList)target;
                    switch (WireFormat.getTagWireType(this.tag)) {
                        case 2: {
                            int bytes = this.readVarint32();
                            this.verifyPackedFixed32Length(bytes);
                            int fieldEndPos = this.pos + bytes;
                            while (this.pos < fieldEndPos) {
                                plist.addInt(this.readLittleEndian32_NoCheck());
                            }
                            break block15;
                        }
                        case 5: {
                            int prevPos;
                            int nextTag;
                            do {
                                plist.addInt(this.readSFixed32());
                                if (this.isAtEnd()) {
                                    return;
                                }
                                prevPos = this.pos;
                            } while ((nextTag = this.readVarint32()) == this.tag);
                            this.pos = prevPos;
                            return;
                        }
                        default: {
                            throw InvalidProtocolBufferException.invalidWireType();
                        }
                    }
                }
                switch (WireFormat.getTagWireType(this.tag)) {
                    case 2: {
                        int bytes = this.readVarint32();
                        this.verifyPackedFixed32Length(bytes);
                        int fieldEndPos = this.pos + bytes;
                        while (this.pos < fieldEndPos) {
                            target.add(this.readLittleEndian32_NoCheck());
                        }
                        break;
                    }
                    case 5: {
                        int prevPos;
                        int nextTag;
                        do {
                            target.add(this.readSFixed32());
                            if (this.isAtEnd()) {
                                return;
                            }
                            prevPos = this.pos;
                        } while ((nextTag = this.readVarint32()) == this.tag);
                        this.pos = prevPos;
                        return;
                    }
                    default: {
                        throw InvalidProtocolBufferException.invalidWireType();
                    }
                }
            }
        }

        @Override
        public void readSFixed64List(List<Long> target) throws IOException {
            block15: {
                block14: {
                    if (!(target instanceof LongArrayList)) break block14;
                    LongArrayList plist = (LongArrayList)target;
                    switch (WireFormat.getTagWireType(this.tag)) {
                        case 2: {
                            int bytes = this.readVarint32();
                            this.verifyPackedFixed64Length(bytes);
                            int fieldEndPos = this.pos + bytes;
                            while (this.pos < fieldEndPos) {
                                plist.addLong(this.readLittleEndian64_NoCheck());
                            }
                            break block15;
                        }
                        case 1: {
                            int prevPos;
                            int nextTag;
                            do {
                                plist.addLong(this.readSFixed64());
                                if (this.isAtEnd()) {
                                    return;
                                }
                                prevPos = this.pos;
                            } while ((nextTag = this.readVarint32()) == this.tag);
                            this.pos = prevPos;
                            return;
                        }
                        default: {
                            throw InvalidProtocolBufferException.invalidWireType();
                        }
                    }
                }
                switch (WireFormat.getTagWireType(this.tag)) {
                    case 2: {
                        int bytes = this.readVarint32();
                        this.verifyPackedFixed64Length(bytes);
                        int fieldEndPos = this.pos + bytes;
                        while (this.pos < fieldEndPos) {
                            target.add(this.readLittleEndian64_NoCheck());
                        }
                        break;
                    }
                    case 1: {
                        int prevPos;
                        int nextTag;
                        do {
                            target.add(this.readSFixed64());
                            if (this.isAtEnd()) {
                                return;
                            }
                            prevPos = this.pos;
                        } while ((nextTag = this.readVarint32()) == this.tag);
                        this.pos = prevPos;
                        return;
                    }
                    default: {
                        throw InvalidProtocolBufferException.invalidWireType();
                    }
                }
            }
        }

        @Override
        public void readSInt32List(List<Integer> target) throws IOException {
            block15: {
                block14: {
                    if (!(target instanceof IntArrayList)) break block14;
                    IntArrayList plist = (IntArrayList)target;
                    switch (WireFormat.getTagWireType(this.tag)) {
                        case 2: {
                            int bytes = this.readVarint32();
                            int fieldEndPos = this.pos + bytes;
                            while (this.pos < fieldEndPos) {
                                plist.addInt(CodedInputStream.decodeZigZag32(this.readVarint32()));
                            }
                            break block15;
                        }
                        case 0: {
                            int prevPos;
                            int nextTag;
                            do {
                                plist.addInt(this.readSInt32());
                                if (this.isAtEnd()) {
                                    return;
                                }
                                prevPos = this.pos;
                            } while ((nextTag = this.readVarint32()) == this.tag);
                            this.pos = prevPos;
                            return;
                        }
                        default: {
                            throw InvalidProtocolBufferException.invalidWireType();
                        }
                    }
                }
                switch (WireFormat.getTagWireType(this.tag)) {
                    case 2: {
                        int bytes = this.readVarint32();
                        int fieldEndPos = this.pos + bytes;
                        while (this.pos < fieldEndPos) {
                            target.add(CodedInputStream.decodeZigZag32(this.readVarint32()));
                        }
                        break;
                    }
                    case 0: {
                        int prevPos;
                        int nextTag;
                        do {
                            target.add(this.readSInt32());
                            if (this.isAtEnd()) {
                                return;
                            }
                            prevPos = this.pos;
                        } while ((nextTag = this.readVarint32()) == this.tag);
                        this.pos = prevPos;
                        return;
                    }
                    default: {
                        throw InvalidProtocolBufferException.invalidWireType();
                    }
                }
            }
        }

        @Override
        public void readSInt64List(List<Long> target) throws IOException {
            block15: {
                block14: {
                    if (!(target instanceof LongArrayList)) break block14;
                    LongArrayList plist = (LongArrayList)target;
                    switch (WireFormat.getTagWireType(this.tag)) {
                        case 2: {
                            int bytes = this.readVarint32();
                            int fieldEndPos = this.pos + bytes;
                            while (this.pos < fieldEndPos) {
                                plist.addLong(CodedInputStream.decodeZigZag64(this.readVarint64()));
                            }
                            break block15;
                        }
                        case 0: {
                            int prevPos;
                            int nextTag;
                            do {
                                plist.addLong(this.readSInt64());
                                if (this.isAtEnd()) {
                                    return;
                                }
                                prevPos = this.pos;
                            } while ((nextTag = this.readVarint32()) == this.tag);
                            this.pos = prevPos;
                            return;
                        }
                        default: {
                            throw InvalidProtocolBufferException.invalidWireType();
                        }
                    }
                }
                switch (WireFormat.getTagWireType(this.tag)) {
                    case 2: {
                        int bytes = this.readVarint32();
                        int fieldEndPos = this.pos + bytes;
                        while (this.pos < fieldEndPos) {
                            target.add(CodedInputStream.decodeZigZag64(this.readVarint64()));
                        }
                        break;
                    }
                    case 0: {
                        int prevPos;
                        int nextTag;
                        do {
                            target.add(this.readSInt64());
                            if (this.isAtEnd()) {
                                return;
                            }
                            prevPos = this.pos;
                        } while ((nextTag = this.readVarint32()) == this.tag);
                        this.pos = prevPos;
                        return;
                    }
                    default: {
                        throw InvalidProtocolBufferException.invalidWireType();
                    }
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public <K, V> void readMap(Map<K, V> target, MapEntryLite.Metadata<K, V> metadata, ExtensionRegistryLite extensionRegistry) throws IOException {
            int newLimit;
            this.requireWireType(2);
            int size = this.readVarint32();
            this.requireBytes(size);
            int prevLimit = this.limit;
            this.limit = newLimit = this.pos + size;
            try {
                int number;
                Object key = metadata.defaultKey;
                Object value = metadata.defaultValue;
                block9: while ((number = this.getFieldNumber()) != Integer.MAX_VALUE) {
                    try {
                        switch (number) {
                            case 1: {
                                key = this.readField(metadata.keyType, null, null);
                                break;
                            }
                            case 2: {
                                value = this.readField(metadata.valueType, metadata.defaultValue.getClass(), extensionRegistry);
                                break;
                            }
                            default: {
                                if (this.skipField()) continue block9;
                                throw new InvalidProtocolBufferException("Unable to parse map entry.");
                            }
                        }
                    }
                    catch (InvalidProtocolBufferException.InvalidWireTypeException ignore) {
                        if (this.skipField()) continue;
                        throw new InvalidProtocolBufferException("Unable to parse map entry.");
                    }
                }
                target.put(key, value);
            }
            finally {
                this.limit = prevLimit;
            }
        }

        private Object readField(WireFormat.FieldType fieldType, Class<?> messageType, ExtensionRegistryLite extensionRegistry) throws IOException {
            switch (fieldType) {
                case BOOL: {
                    return this.readBool();
                }
                case BYTES: {
                    return this.readBytes();
                }
                case DOUBLE: {
                    return this.readDouble();
                }
                case ENUM: {
                    return this.readEnum();
                }
                case FIXED32: {
                    return this.readFixed32();
                }
                case FIXED64: {
                    return this.readFixed64();
                }
                case FLOAT: {
                    return Float.valueOf(this.readFloat());
                }
                case INT32: {
                    return this.readInt32();
                }
                case INT64: {
                    return this.readInt64();
                }
                case MESSAGE: {
                    return this.readMessage(messageType, extensionRegistry);
                }
                case SFIXED32: {
                    return this.readSFixed32();
                }
                case SFIXED64: {
                    return this.readSFixed64();
                }
                case SINT32: {
                    return this.readSInt32();
                }
                case SINT64: {
                    return this.readSInt64();
                }
                case STRING: {
                    return this.readStringRequireUtf8();
                }
                case UINT32: {
                    return this.readUInt32();
                }
                case UINT64: {
                    return this.readUInt64();
                }
            }
            throw new RuntimeException("unsupported field type.");
        }

        private int readVarint32() throws IOException {
            int x;
            int i = this.pos;
            if (this.limit == this.pos) {
                throw InvalidProtocolBufferException.truncatedMessage();
            }
            if ((x = this.buffer[i++]) >= 0) {
                this.pos = i;
                return x;
            }
            if (this.limit - i < 9) {
                return (int)this.readVarint64SlowPath();
            }
            if ((x ^= this.buffer[i++] << 7) < 0) {
                x ^= 0xFFFFFF80;
            } else if ((x ^= this.buffer[i++] << 14) >= 0) {
                x ^= 0x3F80;
            } else if ((x ^= this.buffer[i++] << 21) < 0) {
                x ^= 0xFFE03F80;
            } else {
                byte y = this.buffer[i++];
                x ^= y << 28;
                x ^= 0xFE03F80;
                if (y < 0 && this.buffer[i++] < 0 && this.buffer[i++] < 0 && this.buffer[i++] < 0 && this.buffer[i++] < 0 && this.buffer[i++] < 0) {
                    throw InvalidProtocolBufferException.malformedVarint();
                }
            }
            this.pos = i;
            return x;
        }

        public long readVarint64() throws IOException {
            long x;
            int y;
            int i = this.pos;
            if (this.limit == i) {
                throw InvalidProtocolBufferException.truncatedMessage();
            }
            byte[] buffer = this.buffer;
            if ((y = buffer[i++]) >= 0) {
                this.pos = i;
                return y;
            }
            if (this.limit - i < 9) {
                return this.readVarint64SlowPath();
            }
            if ((y ^= buffer[i++] << 7) < 0) {
                x = y ^ 0xFFFFFF80;
            } else if ((y ^= buffer[i++] << 14) >= 0) {
                x = y ^ 0x3F80;
            } else if ((y ^= buffer[i++] << 21) < 0) {
                x = y ^ 0xFFE03F80;
            } else if ((x = (long)y ^ (long)buffer[i++] << 28) >= 0L) {
                x ^= 0xFE03F80L;
            } else if ((x ^= (long)buffer[i++] << 35) < 0L) {
                x ^= 0xFFFFFFF80FE03F80L;
            } else if ((x ^= (long)buffer[i++] << 42) >= 0L) {
                x ^= 0x3F80FE03F80L;
            } else if ((x ^= (long)buffer[i++] << 49) < 0L) {
                x ^= 0xFFFE03F80FE03F80L;
            } else {
                x ^= (long)buffer[i++] << 56;
                if ((x ^= 0xFE03F80FE03F80L) < 0L && (long)buffer[i++] < 0L) {
                    throw InvalidProtocolBufferException.malformedVarint();
                }
            }
            this.pos = i;
            return x;
        }

        private long readVarint64SlowPath() throws IOException {
            long result = 0L;
            for (int shift = 0; shift < 64; shift += 7) {
                byte b = this.readByte();
                result |= (long)(b & 0x7F) << shift;
                if ((b & 0x80) != 0) continue;
                return result;
            }
            throw InvalidProtocolBufferException.malformedVarint();
        }

        private byte readByte() throws IOException {
            if (this.pos == this.limit) {
                throw InvalidProtocolBufferException.truncatedMessage();
            }
            return this.buffer[this.pos++];
        }

        private int readLittleEndian32() throws IOException {
            this.requireBytes(4);
            return this.readLittleEndian32_NoCheck();
        }

        private long readLittleEndian64() throws IOException {
            this.requireBytes(8);
            return this.readLittleEndian64_NoCheck();
        }

        private int readLittleEndian32_NoCheck() {
            int p = this.pos;
            byte[] buffer = this.buffer;
            this.pos = p + 4;
            return buffer[p] & 0xFF | (buffer[p + 1] & 0xFF) << 8 | (buffer[p + 2] & 0xFF) << 16 | (buffer[p + 3] & 0xFF) << 24;
        }

        private long readLittleEndian64_NoCheck() {
            int p = this.pos;
            byte[] buffer = this.buffer;
            this.pos = p + 8;
            return (long)buffer[p] & 0xFFL | ((long)buffer[p + 1] & 0xFFL) << 8 | ((long)buffer[p + 2] & 0xFFL) << 16 | ((long)buffer[p + 3] & 0xFFL) << 24 | ((long)buffer[p + 4] & 0xFFL) << 32 | ((long)buffer[p + 5] & 0xFFL) << 40 | ((long)buffer[p + 6] & 0xFFL) << 48 | ((long)buffer[p + 7] & 0xFFL) << 56;
        }

        private void skipVarint() throws IOException {
            if (this.limit - this.pos >= 10) {
                byte[] buffer = this.buffer;
                int p = this.pos;
                for (int i = 0; i < 10; ++i) {
                    if (buffer[p++] < 0) continue;
                    this.pos = p;
                    return;
                }
            }
            this.skipVarintSlowPath();
        }

        private void skipVarintSlowPath() throws IOException {
            for (int i = 0; i < 10; ++i) {
                if (this.readByte() < 0) continue;
                return;
            }
            throw InvalidProtocolBufferException.malformedVarint();
        }

        private void skipBytes(int size) throws IOException {
            this.requireBytes(size);
            this.pos += size;
        }

        private void skipGroup() throws IOException {
            int prevEndGroupTag = this.endGroupTag;
            this.endGroupTag = WireFormat.makeTag(WireFormat.getTagFieldNumber(this.tag), 4);
            while (this.getFieldNumber() != Integer.MAX_VALUE && this.skipField()) {
            }
            if (this.tag != this.endGroupTag) {
                throw InvalidProtocolBufferException.parseFailure();
            }
            this.endGroupTag = prevEndGroupTag;
        }

        private void requireBytes(int size) throws IOException {
            if (size < 0 || size > this.limit - this.pos) {
                throw InvalidProtocolBufferException.truncatedMessage();
            }
        }

        private void requireWireType(int requiredWireType) throws IOException {
            if (WireFormat.getTagWireType(this.tag) != requiredWireType) {
                throw InvalidProtocolBufferException.invalidWireType();
            }
        }

        private void verifyPackedFixed64Length(int bytes) throws IOException {
            this.requireBytes(bytes);
            if ((bytes & 7) != 0) {
                throw InvalidProtocolBufferException.parseFailure();
            }
        }

        private void verifyPackedFixed32Length(int bytes) throws IOException {
            this.requireBytes(bytes);
            if ((bytes & 3) != 0) {
                throw InvalidProtocolBufferException.parseFailure();
            }
        }

        private void requirePosition(int expectedPosition) throws IOException {
            if (this.pos != expectedPosition) {
                throw InvalidProtocolBufferException.truncatedMessage();
            }
        }
    }
}

