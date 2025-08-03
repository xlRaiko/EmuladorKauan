/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.AbstractParser;
import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLite;
import com.google.protobuf.TextFormat;
import com.google.protobuf.WireFormat;
import com.google.protobuf.Writer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.TreeMap;

public final class UnknownFieldSet
implements MessageLite {
    private static final UnknownFieldSet defaultInstance = new UnknownFieldSet(Collections.emptyMap(), Collections.emptyMap());
    private final Map<Integer, Field> fields;
    private final Map<Integer, Field> fieldsDescending;
    private static final Parser PARSER = new Parser();

    private UnknownFieldSet() {
        this.fields = null;
        this.fieldsDescending = null;
    }

    public static Builder newBuilder() {
        return Builder.create();
    }

    public static Builder newBuilder(UnknownFieldSet copyFrom) {
        return UnknownFieldSet.newBuilder().mergeFrom(copyFrom);
    }

    public static UnknownFieldSet getDefaultInstance() {
        return defaultInstance;
    }

    @Override
    public UnknownFieldSet getDefaultInstanceForType() {
        return defaultInstance;
    }

    UnknownFieldSet(Map<Integer, Field> fields, Map<Integer, Field> fieldsDescending) {
        this.fields = fields;
        this.fieldsDescending = fieldsDescending;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        return other instanceof UnknownFieldSet && this.fields.equals(((UnknownFieldSet)other).fields);
    }

    public int hashCode() {
        return this.fields.hashCode();
    }

    public Map<Integer, Field> asMap() {
        return this.fields;
    }

    public boolean hasField(int number) {
        return this.fields.containsKey(number);
    }

    public Field getField(int number) {
        Field result = this.fields.get(number);
        return result == null ? Field.getDefaultInstance() : result;
    }

    @Override
    public void writeTo(CodedOutputStream output) throws IOException {
        for (Map.Entry<Integer, Field> entry : this.fields.entrySet()) {
            Field field = entry.getValue();
            field.writeTo((int)entry.getKey(), output);
        }
    }

    public String toString() {
        return TextFormat.printer().printToString(this);
    }

    @Override
    public ByteString toByteString() {
        try {
            ByteString.CodedBuilder out = ByteString.newCodedBuilder(this.getSerializedSize());
            this.writeTo(out.getCodedOutput());
            return out.build();
        }
        catch (IOException e) {
            throw new RuntimeException("Serializing to a ByteString threw an IOException (should never happen).", e);
        }
    }

    @Override
    public byte[] toByteArray() {
        try {
            byte[] result = new byte[this.getSerializedSize()];
            CodedOutputStream output = CodedOutputStream.newInstance(result);
            this.writeTo(output);
            output.checkNoSpaceLeft();
            return result;
        }
        catch (IOException e) {
            throw new RuntimeException("Serializing to a byte array threw an IOException (should never happen).", e);
        }
    }

    @Override
    public void writeTo(OutputStream output) throws IOException {
        CodedOutputStream codedOutput = CodedOutputStream.newInstance(output);
        this.writeTo(codedOutput);
        codedOutput.flush();
    }

    @Override
    public void writeDelimitedTo(OutputStream output) throws IOException {
        CodedOutputStream codedOutput = CodedOutputStream.newInstance(output);
        codedOutput.writeRawVarint32(this.getSerializedSize());
        this.writeTo(codedOutput);
        codedOutput.flush();
    }

    @Override
    public int getSerializedSize() {
        int result = 0;
        for (Map.Entry<Integer, Field> entry : this.fields.entrySet()) {
            result += entry.getValue().getSerializedSize(entry.getKey());
        }
        return result;
    }

    public void writeAsMessageSetTo(CodedOutputStream output) throws IOException {
        for (Map.Entry<Integer, Field> entry : this.fields.entrySet()) {
            entry.getValue().writeAsMessageSetExtensionTo((int)entry.getKey(), output);
        }
    }

    void writeTo(Writer writer) throws IOException {
        if (writer.fieldOrder() == Writer.FieldOrder.DESCENDING) {
            for (Map.Entry<Integer, Field> entry : this.fieldsDescending.entrySet()) {
                entry.getValue().writeTo((int)entry.getKey(), writer);
            }
        } else {
            for (Map.Entry<Integer, Field> entry : this.fields.entrySet()) {
                entry.getValue().writeTo((int)entry.getKey(), writer);
            }
        }
    }

    void writeAsMessageSetTo(Writer writer) throws IOException {
        if (writer.fieldOrder() == Writer.FieldOrder.DESCENDING) {
            for (Map.Entry<Integer, Field> entry : this.fieldsDescending.entrySet()) {
                entry.getValue().writeAsMessageSetExtensionTo(entry.getKey(), writer);
            }
        } else {
            for (Map.Entry<Integer, Field> entry : this.fields.entrySet()) {
                entry.getValue().writeAsMessageSetExtensionTo(entry.getKey(), writer);
            }
        }
    }

    public int getSerializedSizeAsMessageSet() {
        int result = 0;
        for (Map.Entry<Integer, Field> entry : this.fields.entrySet()) {
            result += entry.getValue().getSerializedSizeAsMessageSetExtension(entry.getKey());
        }
        return result;
    }

    @Override
    public boolean isInitialized() {
        return true;
    }

    public static UnknownFieldSet parseFrom(CodedInputStream input) throws IOException {
        return UnknownFieldSet.newBuilder().mergeFrom(input).build();
    }

    public static UnknownFieldSet parseFrom(ByteString data) throws InvalidProtocolBufferException {
        return UnknownFieldSet.newBuilder().mergeFrom(data).build();
    }

    public static UnknownFieldSet parseFrom(byte[] data) throws InvalidProtocolBufferException {
        return UnknownFieldSet.newBuilder().mergeFrom(data).build();
    }

    public static UnknownFieldSet parseFrom(InputStream input) throws IOException {
        return UnknownFieldSet.newBuilder().mergeFrom(input).build();
    }

    @Override
    public Builder newBuilderForType() {
        return UnknownFieldSet.newBuilder();
    }

    @Override
    public Builder toBuilder() {
        return UnknownFieldSet.newBuilder().mergeFrom(this);
    }

    public final Parser getParserForType() {
        return PARSER;
    }

    public static final class Parser
    extends AbstractParser<UnknownFieldSet> {
        @Override
        public UnknownFieldSet parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            Builder builder = UnknownFieldSet.newBuilder();
            try {
                builder.mergeFrom(input);
            }
            catch (InvalidProtocolBufferException e) {
                throw e.setUnfinishedMessage(builder.buildPartial());
            }
            catch (IOException e) {
                throw new InvalidProtocolBufferException(e).setUnfinishedMessage(builder.buildPartial());
            }
            return builder.buildPartial();
        }
    }

    public static final class Field {
        private static final Field fieldDefaultInstance = Field.newBuilder().build();
        private List<Long> varint;
        private List<Integer> fixed32;
        private List<Long> fixed64;
        private List<ByteString> lengthDelimited;
        private List<UnknownFieldSet> group;

        private Field() {
        }

        public static Builder newBuilder() {
            return Builder.create();
        }

        public static Builder newBuilder(Field copyFrom) {
            return Field.newBuilder().mergeFrom(copyFrom);
        }

        public static Field getDefaultInstance() {
            return fieldDefaultInstance;
        }

        public List<Long> getVarintList() {
            return this.varint;
        }

        public List<Integer> getFixed32List() {
            return this.fixed32;
        }

        public List<Long> getFixed64List() {
            return this.fixed64;
        }

        public List<ByteString> getLengthDelimitedList() {
            return this.lengthDelimited;
        }

        public List<UnknownFieldSet> getGroupList() {
            return this.group;
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof Field)) {
                return false;
            }
            return Arrays.equals(this.getIdentityArray(), ((Field)other).getIdentityArray());
        }

        public int hashCode() {
            return Arrays.hashCode(this.getIdentityArray());
        }

        private Object[] getIdentityArray() {
            return new Object[]{this.varint, this.fixed32, this.fixed64, this.lengthDelimited, this.group};
        }

        public ByteString toByteString(int fieldNumber) {
            try {
                ByteString.CodedBuilder out = ByteString.newCodedBuilder(this.getSerializedSize(fieldNumber));
                this.writeTo(fieldNumber, out.getCodedOutput());
                return out.build();
            }
            catch (IOException e) {
                throw new RuntimeException("Serializing to a ByteString should never fail with an IOException", e);
            }
        }

        public void writeTo(int fieldNumber, CodedOutputStream output) throws IOException {
            Iterator<Object> iterator = this.varint.iterator();
            while (iterator.hasNext()) {
                long value = iterator.next();
                output.writeUInt64(fieldNumber, value);
            }
            iterator = this.fixed32.iterator();
            while (iterator.hasNext()) {
                int value = (Integer)iterator.next();
                output.writeFixed32(fieldNumber, value);
            }
            iterator = this.fixed64.iterator();
            while (iterator.hasNext()) {
                long value = (Long)iterator.next();
                output.writeFixed64(fieldNumber, value);
            }
            for (ByteString value : this.lengthDelimited) {
                output.writeBytes(fieldNumber, value);
            }
            for (UnknownFieldSet value : this.group) {
                output.writeGroup(fieldNumber, value);
            }
        }

        public int getSerializedSize(int fieldNumber) {
            int result = 0;
            Iterator<Object> iterator = this.varint.iterator();
            while (iterator.hasNext()) {
                long value = iterator.next();
                result += CodedOutputStream.computeUInt64Size(fieldNumber, value);
            }
            iterator = this.fixed32.iterator();
            while (iterator.hasNext()) {
                int value = (Integer)iterator.next();
                result += CodedOutputStream.computeFixed32Size(fieldNumber, value);
            }
            iterator = this.fixed64.iterator();
            while (iterator.hasNext()) {
                long value = (Long)iterator.next();
                result += CodedOutputStream.computeFixed64Size(fieldNumber, value);
            }
            for (ByteString value : this.lengthDelimited) {
                result += CodedOutputStream.computeBytesSize(fieldNumber, value);
            }
            for (UnknownFieldSet value : this.group) {
                result += CodedOutputStream.computeGroupSize(fieldNumber, value);
            }
            return result;
        }

        public void writeAsMessageSetExtensionTo(int fieldNumber, CodedOutputStream output) throws IOException {
            for (ByteString value : this.lengthDelimited) {
                output.writeRawMessageSetExtension(fieldNumber, value);
            }
        }

        void writeTo(int fieldNumber, Writer writer) throws IOException {
            writer.writeInt64List(fieldNumber, this.varint, false);
            writer.writeFixed32List(fieldNumber, this.fixed32, false);
            writer.writeFixed64List(fieldNumber, this.fixed64, false);
            writer.writeBytesList(fieldNumber, this.lengthDelimited);
            if (writer.fieldOrder() == Writer.FieldOrder.ASCENDING) {
                for (int i = 0; i < this.group.size(); ++i) {
                    writer.writeStartGroup(fieldNumber);
                    this.group.get(i).writeTo(writer);
                    writer.writeEndGroup(fieldNumber);
                }
            } else {
                for (int i = this.group.size() - 1; i >= 0; --i) {
                    writer.writeEndGroup(fieldNumber);
                    this.group.get(i).writeTo(writer);
                    writer.writeStartGroup(fieldNumber);
                }
            }
        }

        private void writeAsMessageSetExtensionTo(int fieldNumber, Writer writer) throws IOException {
            if (writer.fieldOrder() == Writer.FieldOrder.DESCENDING) {
                ListIterator<ByteString> iter = this.lengthDelimited.listIterator(this.lengthDelimited.size());
                while (iter.hasPrevious()) {
                    writer.writeMessageSetItem(fieldNumber, iter.previous());
                }
            } else {
                for (ByteString value : this.lengthDelimited) {
                    writer.writeMessageSetItem(fieldNumber, value);
                }
            }
        }

        public int getSerializedSizeAsMessageSetExtension(int fieldNumber) {
            int result = 0;
            for (ByteString value : this.lengthDelimited) {
                result += CodedOutputStream.computeRawMessageSetExtensionSize(fieldNumber, value);
            }
            return result;
        }

        public static final class Builder {
            private Field result;

            private Builder() {
            }

            private static Builder create() {
                Builder builder = new Builder();
                builder.result = new Field();
                return builder;
            }

            public Field build() {
                if (this.result.varint == null) {
                    this.result.varint = Collections.emptyList();
                } else {
                    this.result.varint = Collections.unmodifiableList(this.result.varint);
                }
                if (this.result.fixed32 == null) {
                    this.result.fixed32 = Collections.emptyList();
                } else {
                    this.result.fixed32 = Collections.unmodifiableList(this.result.fixed32);
                }
                if (this.result.fixed64 == null) {
                    this.result.fixed64 = Collections.emptyList();
                } else {
                    this.result.fixed64 = Collections.unmodifiableList(this.result.fixed64);
                }
                if (this.result.lengthDelimited == null) {
                    this.result.lengthDelimited = Collections.emptyList();
                } else {
                    this.result.lengthDelimited = Collections.unmodifiableList(this.result.lengthDelimited);
                }
                if (this.result.group == null) {
                    this.result.group = Collections.emptyList();
                } else {
                    this.result.group = Collections.unmodifiableList(this.result.group);
                }
                Field returnMe = this.result;
                this.result = null;
                return returnMe;
            }

            public Builder clear() {
                this.result = new Field();
                return this;
            }

            public Builder mergeFrom(Field other) {
                if (!other.varint.isEmpty()) {
                    if (this.result.varint == null) {
                        this.result.varint = new ArrayList();
                    }
                    this.result.varint.addAll(other.varint);
                }
                if (!other.fixed32.isEmpty()) {
                    if (this.result.fixed32 == null) {
                        this.result.fixed32 = new ArrayList();
                    }
                    this.result.fixed32.addAll(other.fixed32);
                }
                if (!other.fixed64.isEmpty()) {
                    if (this.result.fixed64 == null) {
                        this.result.fixed64 = new ArrayList();
                    }
                    this.result.fixed64.addAll(other.fixed64);
                }
                if (!other.lengthDelimited.isEmpty()) {
                    if (this.result.lengthDelimited == null) {
                        this.result.lengthDelimited = new ArrayList();
                    }
                    this.result.lengthDelimited.addAll(other.lengthDelimited);
                }
                if (!other.group.isEmpty()) {
                    if (this.result.group == null) {
                        this.result.group = new ArrayList();
                    }
                    this.result.group.addAll(other.group);
                }
                return this;
            }

            public Builder addVarint(long value) {
                if (this.result.varint == null) {
                    this.result.varint = new ArrayList();
                }
                this.result.varint.add(value);
                return this;
            }

            public Builder addFixed32(int value) {
                if (this.result.fixed32 == null) {
                    this.result.fixed32 = new ArrayList();
                }
                this.result.fixed32.add(value);
                return this;
            }

            public Builder addFixed64(long value) {
                if (this.result.fixed64 == null) {
                    this.result.fixed64 = new ArrayList();
                }
                this.result.fixed64.add(value);
                return this;
            }

            public Builder addLengthDelimited(ByteString value) {
                if (this.result.lengthDelimited == null) {
                    this.result.lengthDelimited = new ArrayList();
                }
                this.result.lengthDelimited.add(value);
                return this;
            }

            public Builder addGroup(UnknownFieldSet value) {
                if (this.result.group == null) {
                    this.result.group = new ArrayList();
                }
                this.result.group.add(value);
                return this;
            }
        }
    }

    public static final class Builder
    implements MessageLite.Builder {
        private Map<Integer, Field> fields;
        private int lastFieldNumber;
        private Field.Builder lastField;

        private Builder() {
        }

        private static Builder create() {
            Builder builder = new Builder();
            builder.reinitialize();
            return builder;
        }

        private Field.Builder getFieldBuilder(int number) {
            if (this.lastField != null) {
                if (number == this.lastFieldNumber) {
                    return this.lastField;
                }
                this.addField(this.lastFieldNumber, this.lastField.build());
            }
            if (number == 0) {
                return null;
            }
            Field existing = this.fields.get(number);
            this.lastFieldNumber = number;
            this.lastField = Field.newBuilder();
            if (existing != null) {
                this.lastField.mergeFrom(existing);
            }
            return this.lastField;
        }

        @Override
        public UnknownFieldSet build() {
            UnknownFieldSet result;
            this.getFieldBuilder(0);
            if (this.fields.isEmpty()) {
                result = UnknownFieldSet.getDefaultInstance();
            } else {
                Map<Integer, Field> descendingFields = null;
                descendingFields = Collections.unmodifiableMap(((TreeMap)this.fields).descendingMap());
                result = new UnknownFieldSet(Collections.unmodifiableMap(this.fields), descendingFields);
            }
            this.fields = null;
            return result;
        }

        @Override
        public UnknownFieldSet buildPartial() {
            return this.build();
        }

        @Override
        public Builder clone() {
            this.getFieldBuilder(0);
            Map<Integer, Field> descendingFields = null;
            descendingFields = Collections.unmodifiableMap(((TreeMap)this.fields).descendingMap());
            return UnknownFieldSet.newBuilder().mergeFrom(new UnknownFieldSet(this.fields, descendingFields));
        }

        @Override
        public UnknownFieldSet getDefaultInstanceForType() {
            return UnknownFieldSet.getDefaultInstance();
        }

        private void reinitialize() {
            this.fields = Collections.emptyMap();
            this.lastFieldNumber = 0;
            this.lastField = null;
        }

        @Override
        public Builder clear() {
            this.reinitialize();
            return this;
        }

        public Builder clearField(int number) {
            if (number == 0) {
                throw new IllegalArgumentException("Zero is not a valid field number.");
            }
            if (this.lastField != null && this.lastFieldNumber == number) {
                this.lastField = null;
                this.lastFieldNumber = 0;
            }
            if (this.fields.containsKey(number)) {
                this.fields.remove(number);
            }
            return this;
        }

        public Builder mergeFrom(UnknownFieldSet other) {
            if (other != UnknownFieldSet.getDefaultInstance()) {
                for (Map.Entry entry : other.fields.entrySet()) {
                    this.mergeField((Integer)entry.getKey(), (Field)entry.getValue());
                }
            }
            return this;
        }

        public Builder mergeField(int number, Field field) {
            if (number == 0) {
                throw new IllegalArgumentException("Zero is not a valid field number.");
            }
            if (this.hasField(number)) {
                this.getFieldBuilder(number).mergeFrom(field);
            } else {
                this.addField(number, field);
            }
            return this;
        }

        public Builder mergeVarintField(int number, int value) {
            if (number == 0) {
                throw new IllegalArgumentException("Zero is not a valid field number.");
            }
            this.getFieldBuilder(number).addVarint(value);
            return this;
        }

        public Builder mergeLengthDelimitedField(int number, ByteString value) {
            if (number == 0) {
                throw new IllegalArgumentException("Zero is not a valid field number.");
            }
            this.getFieldBuilder(number).addLengthDelimited(value);
            return this;
        }

        public boolean hasField(int number) {
            if (number == 0) {
                throw new IllegalArgumentException("Zero is not a valid field number.");
            }
            return number == this.lastFieldNumber || this.fields.containsKey(number);
        }

        public Builder addField(int number, Field field) {
            if (number == 0) {
                throw new IllegalArgumentException("Zero is not a valid field number.");
            }
            if (this.lastField != null && this.lastFieldNumber == number) {
                this.lastField = null;
                this.lastFieldNumber = 0;
            }
            if (this.fields.isEmpty()) {
                this.fields = new TreeMap<Integer, Field>();
            }
            this.fields.put(number, field);
            return this;
        }

        public Map<Integer, Field> asMap() {
            this.getFieldBuilder(0);
            return Collections.unmodifiableMap(this.fields);
        }

        @Override
        public Builder mergeFrom(CodedInputStream input) throws IOException {
            int tag;
            while ((tag = input.readTag()) != 0 && this.mergeFieldFrom(tag, input)) {
            }
            return this;
        }

        public boolean mergeFieldFrom(int tag, CodedInputStream input) throws IOException {
            int number = WireFormat.getTagFieldNumber(tag);
            switch (WireFormat.getTagWireType(tag)) {
                case 0: {
                    this.getFieldBuilder(number).addVarint(input.readInt64());
                    return true;
                }
                case 1: {
                    this.getFieldBuilder(number).addFixed64(input.readFixed64());
                    return true;
                }
                case 2: {
                    this.getFieldBuilder(number).addLengthDelimited(input.readBytes());
                    return true;
                }
                case 3: {
                    Builder subBuilder = UnknownFieldSet.newBuilder();
                    input.readGroup(number, subBuilder, (ExtensionRegistryLite)ExtensionRegistry.getEmptyRegistry());
                    this.getFieldBuilder(number).addGroup(subBuilder.build());
                    return true;
                }
                case 4: {
                    return false;
                }
                case 5: {
                    this.getFieldBuilder(number).addFixed32(input.readFixed32());
                    return true;
                }
            }
            throw InvalidProtocolBufferException.invalidWireType();
        }

        @Override
        public Builder mergeFrom(ByteString data) throws InvalidProtocolBufferException {
            try {
                CodedInputStream input = data.newCodedInput();
                this.mergeFrom(input);
                input.checkLastTagWas(0);
                return this;
            }
            catch (InvalidProtocolBufferException e) {
                throw e;
            }
            catch (IOException e) {
                throw new RuntimeException("Reading from a ByteString threw an IOException (should never happen).", e);
            }
        }

        @Override
        public Builder mergeFrom(byte[] data) throws InvalidProtocolBufferException {
            try {
                CodedInputStream input = CodedInputStream.newInstance(data);
                this.mergeFrom(input);
                input.checkLastTagWas(0);
                return this;
            }
            catch (InvalidProtocolBufferException e) {
                throw e;
            }
            catch (IOException e) {
                throw new RuntimeException("Reading from a byte array threw an IOException (should never happen).", e);
            }
        }

        @Override
        public Builder mergeFrom(InputStream input) throws IOException {
            CodedInputStream codedInput = CodedInputStream.newInstance(input);
            this.mergeFrom(codedInput);
            codedInput.checkLastTagWas(0);
            return this;
        }

        @Override
        public boolean mergeDelimitedFrom(InputStream input) throws IOException {
            int firstByte = input.read();
            if (firstByte == -1) {
                return false;
            }
            int size = CodedInputStream.readRawVarint32(firstByte, input);
            AbstractMessageLite.Builder.LimitedInputStream limitedInput = new AbstractMessageLite.Builder.LimitedInputStream(input, size);
            this.mergeFrom(limitedInput);
            return true;
        }

        @Override
        public boolean mergeDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return this.mergeDelimitedFrom(input);
        }

        @Override
        public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return this.mergeFrom(input);
        }

        @Override
        public Builder mergeFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return this.mergeFrom(data);
        }

        @Override
        public Builder mergeFrom(byte[] data, int off, int len) throws InvalidProtocolBufferException {
            try {
                CodedInputStream input = CodedInputStream.newInstance(data, off, len);
                this.mergeFrom(input);
                input.checkLastTagWas(0);
                return this;
            }
            catch (InvalidProtocolBufferException e) {
                throw e;
            }
            catch (IOException e) {
                throw new RuntimeException("Reading from a byte array threw an IOException (should never happen).", e);
            }
        }

        @Override
        public Builder mergeFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return this.mergeFrom(data);
        }

        @Override
        public Builder mergeFrom(byte[] data, int off, int len, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return this.mergeFrom(data, off, len);
        }

        @Override
        public Builder mergeFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return this.mergeFrom(input);
        }

        @Override
        public Builder mergeFrom(MessageLite m) {
            if (m instanceof UnknownFieldSet) {
                return this.mergeFrom((UnknownFieldSet)m);
            }
            throw new IllegalArgumentException("mergeFrom(MessageLite) can only merge messages of the same type.");
        }

        @Override
        public boolean isInitialized() {
            return true;
        }
    }
}

