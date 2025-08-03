/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf.compiler;

import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.AbstractParser;
import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.LazyStringArrayList;
import com.google.protobuf.LazyStringList;
import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.Parser;
import com.google.protobuf.ProtocolStringList;
import com.google.protobuf.RepeatedFieldBuilderV3;
import com.google.protobuf.SingleFieldBuilderV3;
import com.google.protobuf.UnknownFieldSet;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class PluginProtos {
    private static final Descriptors.Descriptor internal_static_google_protobuf_compiler_Version_descriptor;
    private static final GeneratedMessageV3.FieldAccessorTable internal_static_google_protobuf_compiler_Version_fieldAccessorTable;
    private static final Descriptors.Descriptor internal_static_google_protobuf_compiler_CodeGeneratorRequest_descriptor;
    private static final GeneratedMessageV3.FieldAccessorTable internal_static_google_protobuf_compiler_CodeGeneratorRequest_fieldAccessorTable;
    private static final Descriptors.Descriptor internal_static_google_protobuf_compiler_CodeGeneratorResponse_descriptor;
    private static final GeneratedMessageV3.FieldAccessorTable internal_static_google_protobuf_compiler_CodeGeneratorResponse_fieldAccessorTable;
    private static final Descriptors.Descriptor internal_static_google_protobuf_compiler_CodeGeneratorResponse_File_descriptor;
    private static final GeneratedMessageV3.FieldAccessorTable internal_static_google_protobuf_compiler_CodeGeneratorResponse_File_fieldAccessorTable;
    private static Descriptors.FileDescriptor descriptor;

    private PluginProtos() {
    }

    public static void registerAllExtensions(ExtensionRegistryLite registry) {
    }

    public static void registerAllExtensions(ExtensionRegistry registry) {
        PluginProtos.registerAllExtensions((ExtensionRegistryLite)registry);
    }

    public static Descriptors.FileDescriptor getDescriptor() {
        return descriptor;
    }

    static {
        String[] descriptorData = new String[]{"\n%google/protobuf/compiler/plugin.proto\u0012\u0018google.protobuf.compiler\u001a google/protobuf/descriptor.proto\"F\n\u0007Version\u0012\r\n\u0005major\u0018\u0001 \u0001(\u0005\u0012\r\n\u0005minor\u0018\u0002 \u0001(\u0005\u0012\r\n\u0005patch\u0018\u0003 \u0001(\u0005\u0012\u000e\n\u0006suffix\u0018\u0004 \u0001(\t\"\u00ba\u0001\n\u0014CodeGeneratorRequest\u0012\u0018\n\u0010file_to_generate\u0018\u0001 \u0003(\t\u0012\u0011\n\tparameter\u0018\u0002 \u0001(\t\u00128\n\nproto_file\u0018\u000f \u0003(\u000b2$.google.protobuf.FileDescriptorProto\u0012;\n\u0010compiler_version\u0018\u0003 \u0001(\u000b2!.google.protobuf.compiler.Version\"\u00aa\u0001\n\u0015CodeGeneratorResponse\u0012\r\n\u0005error\u0018\u0001 \u0001(\t\u0012B\n\u0004file\u0018\u000f \u0003(\u000b24.google.protobuf.compiler.CodeGeneratorResponse.File\u001a>\n\u0004File\u0012\f\n\u0004name\u0018\u0001 \u0001(\t\u0012\u0017\n\u000finsertion_point\u0018\u0002 \u0001(\t\u0012\u000f\n\u0007content\u0018\u000f \u0001(\tBg\n\u001ccom.google.protobuf.compilerB\fPluginProtosZ9github.com/golang/protobuf/protoc-gen-go/plugin;plugin_go"};
        descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[]{DescriptorProtos.getDescriptor()});
        internal_static_google_protobuf_compiler_Version_descriptor = PluginProtos.getDescriptor().getMessageTypes().get(0);
        internal_static_google_protobuf_compiler_Version_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_google_protobuf_compiler_Version_descriptor, new String[]{"Major", "Minor", "Patch", "Suffix"});
        internal_static_google_protobuf_compiler_CodeGeneratorRequest_descriptor = PluginProtos.getDescriptor().getMessageTypes().get(1);
        internal_static_google_protobuf_compiler_CodeGeneratorRequest_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_google_protobuf_compiler_CodeGeneratorRequest_descriptor, new String[]{"FileToGenerate", "Parameter", "ProtoFile", "CompilerVersion"});
        internal_static_google_protobuf_compiler_CodeGeneratorResponse_descriptor = PluginProtos.getDescriptor().getMessageTypes().get(2);
        internal_static_google_protobuf_compiler_CodeGeneratorResponse_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_google_protobuf_compiler_CodeGeneratorResponse_descriptor, new String[]{"Error", "File"});
        internal_static_google_protobuf_compiler_CodeGeneratorResponse_File_descriptor = internal_static_google_protobuf_compiler_CodeGeneratorResponse_descriptor.getNestedTypes().get(0);
        internal_static_google_protobuf_compiler_CodeGeneratorResponse_File_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_google_protobuf_compiler_CodeGeneratorResponse_File_descriptor, new String[]{"Name", "InsertionPoint", "Content"});
        DescriptorProtos.getDescriptor();
    }

    public static final class CodeGeneratorResponse
    extends GeneratedMessageV3
    implements CodeGeneratorResponseOrBuilder {
        private static final long serialVersionUID = 0L;
        private int bitField0_;
        public static final int ERROR_FIELD_NUMBER = 1;
        private volatile Object error_;
        public static final int FILE_FIELD_NUMBER = 15;
        private List<File> file_;
        private byte memoizedIsInitialized = (byte)-1;
        private static final CodeGeneratorResponse DEFAULT_INSTANCE = new CodeGeneratorResponse();
        @Deprecated
        public static final Parser<CodeGeneratorResponse> PARSER = new AbstractParser<CodeGeneratorResponse>(){

            @Override
            public CodeGeneratorResponse parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return new CodeGeneratorResponse(input, extensionRegistry);
            }
        };

        private CodeGeneratorResponse(GeneratedMessageV3.Builder<?> builder) {
            super(builder);
        }

        private CodeGeneratorResponse() {
            this.error_ = "";
            this.file_ = Collections.emptyList();
        }

        @Override
        protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
            return new CodeGeneratorResponse();
        }

        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }

        private CodeGeneratorResponse(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            this();
            if (extensionRegistry == null) {
                throw new NullPointerException();
            }
            int mutable_bitField0_ = 0;
            UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();
            try {
                boolean done = false;
                block11: while (!done) {
                    int tag = input.readTag();
                    switch (tag) {
                        case 0: {
                            done = true;
                            continue block11;
                        }
                        case 10: {
                            ByteString bs = input.readBytes();
                            this.bitField0_ |= 1;
                            this.error_ = bs;
                            continue block11;
                        }
                        case 122: {
                            if ((mutable_bitField0_ & 2) == 0) {
                                this.file_ = new ArrayList<File>();
                                mutable_bitField0_ |= 2;
                            }
                            this.file_.add(input.readMessage(File.PARSER, extensionRegistry));
                            continue block11;
                        }
                    }
                    if (this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) continue;
                    done = true;
                }
            }
            catch (InvalidProtocolBufferException e) {
                throw e.setUnfinishedMessage(this);
            }
            catch (IOException e) {
                throw new InvalidProtocolBufferException(e).setUnfinishedMessage(this);
            }
            finally {
                if ((mutable_bitField0_ & 2) != 0) {
                    this.file_ = Collections.unmodifiableList(this.file_);
                }
                this.unknownFields = unknownFields.build();
                this.makeExtensionsImmutable();
            }
        }

        public static final Descriptors.Descriptor getDescriptor() {
            return internal_static_google_protobuf_compiler_CodeGeneratorResponse_descriptor;
        }

        @Override
        protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return internal_static_google_protobuf_compiler_CodeGeneratorResponse_fieldAccessorTable.ensureFieldAccessorsInitialized(CodeGeneratorResponse.class, Builder.class);
        }

        @Override
        public boolean hasError() {
            return (this.bitField0_ & 1) != 0;
        }

        @Override
        public String getError() {
            Object ref = this.error_;
            if (ref instanceof String) {
                return (String)ref;
            }
            ByteString bs = (ByteString)ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.error_ = s;
            }
            return s;
        }

        @Override
        public ByteString getErrorBytes() {
            Object ref = this.error_;
            if (ref instanceof String) {
                ByteString b = ByteString.copyFromUtf8((String)ref);
                this.error_ = b;
                return b;
            }
            return (ByteString)ref;
        }

        @Override
        public List<File> getFileList() {
            return this.file_;
        }

        @Override
        public List<? extends FileOrBuilder> getFileOrBuilderList() {
            return this.file_;
        }

        @Override
        public int getFileCount() {
            return this.file_.size();
        }

        @Override
        public File getFile(int index) {
            return this.file_.get(index);
        }

        @Override
        public FileOrBuilder getFileOrBuilder(int index) {
            return this.file_.get(index);
        }

        @Override
        public final boolean isInitialized() {
            byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized == 1) {
                return true;
            }
            if (isInitialized == 0) {
                return false;
            }
            this.memoizedIsInitialized = 1;
            return true;
        }

        @Override
        public void writeTo(CodedOutputStream output) throws IOException {
            if ((this.bitField0_ & 1) != 0) {
                GeneratedMessageV3.writeString(output, 1, this.error_);
            }
            for (int i = 0; i < this.file_.size(); ++i) {
                output.writeMessage(15, this.file_.get(i));
            }
            this.unknownFields.writeTo(output);
        }

        @Override
        public int getSerializedSize() {
            int size = this.memoizedSize;
            if (size != -1) {
                return size;
            }
            size = 0;
            if ((this.bitField0_ & 1) != 0) {
                size += GeneratedMessageV3.computeStringSize(1, this.error_);
            }
            for (int i = 0; i < this.file_.size(); ++i) {
                size += CodedOutputStream.computeMessageSize(15, this.file_.get(i));
            }
            this.memoizedSize = size += this.unknownFields.getSerializedSize();
            return size;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof CodeGeneratorResponse)) {
                return super.equals(obj);
            }
            CodeGeneratorResponse other = (CodeGeneratorResponse)obj;
            if (this.hasError() != other.hasError()) {
                return false;
            }
            if (this.hasError() && !this.getError().equals(other.getError())) {
                return false;
            }
            if (!this.getFileList().equals(other.getFileList())) {
                return false;
            }
            return this.unknownFields.equals(other.unknownFields);
        }

        @Override
        public int hashCode() {
            if (this.memoizedHashCode != 0) {
                return this.memoizedHashCode;
            }
            int hash = 41;
            hash = 19 * hash + CodeGeneratorResponse.getDescriptor().hashCode();
            if (this.hasError()) {
                hash = 37 * hash + 1;
                hash = 53 * hash + this.getError().hashCode();
            }
            if (this.getFileCount() > 0) {
                hash = 37 * hash + 15;
                hash = 53 * hash + this.getFileList().hashCode();
            }
            this.memoizedHashCode = hash = 29 * hash + this.unknownFields.hashCode();
            return hash;
        }

        public static CodeGeneratorResponse parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static CodeGeneratorResponse parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static CodeGeneratorResponse parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static CodeGeneratorResponse parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static CodeGeneratorResponse parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static CodeGeneratorResponse parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static CodeGeneratorResponse parseFrom(InputStream input) throws IOException {
            return GeneratedMessageV3.parseWithIOException(PARSER, input);
        }

        public static CodeGeneratorResponse parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
        }

        public static CodeGeneratorResponse parseDelimitedFrom(InputStream input) throws IOException {
            return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
        }

        public static CodeGeneratorResponse parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
        }

        public static CodeGeneratorResponse parseFrom(CodedInputStream input) throws IOException {
            return GeneratedMessageV3.parseWithIOException(PARSER, input);
        }

        public static CodeGeneratorResponse parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
        }

        @Override
        public Builder newBuilderForType() {
            return CodeGeneratorResponse.newBuilder();
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(CodeGeneratorResponse prototype) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
        }

        @Override
        public Builder toBuilder() {
            return this == DEFAULT_INSTANCE ? new Builder() : new Builder().mergeFrom(this);
        }

        @Override
        protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
            Builder builder = new Builder(parent);
            return builder;
        }

        public static CodeGeneratorResponse getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<CodeGeneratorResponse> parser() {
            return PARSER;
        }

        public Parser<CodeGeneratorResponse> getParserForType() {
            return PARSER;
        }

        @Override
        public CodeGeneratorResponse getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }

        public static final class Builder
        extends GeneratedMessageV3.Builder<Builder>
        implements CodeGeneratorResponseOrBuilder {
            private int bitField0_;
            private Object error_ = "";
            private List<File> file_ = Collections.emptyList();
            private RepeatedFieldBuilderV3<File, File.Builder, FileOrBuilder> fileBuilder_;

            public static final Descriptors.Descriptor getDescriptor() {
                return internal_static_google_protobuf_compiler_CodeGeneratorResponse_descriptor;
            }

            @Override
            protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
                return internal_static_google_protobuf_compiler_CodeGeneratorResponse_fieldAccessorTable.ensureFieldAccessorsInitialized(CodeGeneratorResponse.class, Builder.class);
            }

            private Builder() {
                this.maybeForceBuilderInitialization();
            }

            private Builder(GeneratedMessageV3.BuilderParent parent) {
                super(parent);
                this.maybeForceBuilderInitialization();
            }

            private void maybeForceBuilderInitialization() {
                if (alwaysUseFieldBuilders) {
                    this.getFileFieldBuilder();
                }
            }

            @Override
            public Builder clear() {
                super.clear();
                this.error_ = "";
                this.bitField0_ &= 0xFFFFFFFE;
                if (this.fileBuilder_ == null) {
                    this.file_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFFD;
                } else {
                    this.fileBuilder_.clear();
                }
                return this;
            }

            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return internal_static_google_protobuf_compiler_CodeGeneratorResponse_descriptor;
            }

            @Override
            public CodeGeneratorResponse getDefaultInstanceForType() {
                return CodeGeneratorResponse.getDefaultInstance();
            }

            @Override
            public CodeGeneratorResponse build() {
                CodeGeneratorResponse result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw Builder.newUninitializedMessageException(result);
                }
                return result;
            }

            @Override
            public CodeGeneratorResponse buildPartial() {
                CodeGeneratorResponse result = new CodeGeneratorResponse(this);
                int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 1) != 0) {
                    to_bitField0_ |= 1;
                }
                result.error_ = this.error_;
                if (this.fileBuilder_ == null) {
                    if ((this.bitField0_ & 2) != 0) {
                        this.file_ = Collections.unmodifiableList(this.file_);
                        this.bitField0_ &= 0xFFFFFFFD;
                    }
                    result.file_ = this.file_;
                } else {
                    result.file_ = this.fileBuilder_.build();
                }
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }

            @Override
            public Builder clone() {
                return (Builder)super.clone();
            }

            @Override
            public Builder setField(Descriptors.FieldDescriptor field, Object value) {
                return (Builder)super.setField(field, value);
            }

            @Override
            public Builder clearField(Descriptors.FieldDescriptor field) {
                return (Builder)super.clearField(field);
            }

            @Override
            public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
                return (Builder)super.clearOneof(oneof);
            }

            @Override
            public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
                return (Builder)super.setRepeatedField(field, index, value);
            }

            @Override
            public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
                return (Builder)super.addRepeatedField(field, value);
            }

            @Override
            public Builder mergeFrom(Message other) {
                if (other instanceof CodeGeneratorResponse) {
                    return this.mergeFrom((CodeGeneratorResponse)other);
                }
                super.mergeFrom(other);
                return this;
            }

            public Builder mergeFrom(CodeGeneratorResponse other) {
                if (other == CodeGeneratorResponse.getDefaultInstance()) {
                    return this;
                }
                if (other.hasError()) {
                    this.bitField0_ |= 1;
                    this.error_ = other.error_;
                    this.onChanged();
                }
                if (this.fileBuilder_ == null) {
                    if (!other.file_.isEmpty()) {
                        if (this.file_.isEmpty()) {
                            this.file_ = other.file_;
                            this.bitField0_ &= 0xFFFFFFFD;
                        } else {
                            this.ensureFileIsMutable();
                            this.file_.addAll(other.file_);
                        }
                        this.onChanged();
                    }
                } else if (!other.file_.isEmpty()) {
                    if (this.fileBuilder_.isEmpty()) {
                        this.fileBuilder_.dispose();
                        this.fileBuilder_ = null;
                        this.file_ = other.file_;
                        this.bitField0_ &= 0xFFFFFFFD;
                        this.fileBuilder_ = alwaysUseFieldBuilders ? this.getFileFieldBuilder() : null;
                    } else {
                        this.fileBuilder_.addAllMessages(other.file_);
                    }
                }
                this.mergeUnknownFields(other.unknownFields);
                this.onChanged();
                return this;
            }

            @Override
            public final boolean isInitialized() {
                return true;
            }

            @Override
            public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                CodeGeneratorResponse parsedMessage = null;
                try {
                    parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (CodeGeneratorResponse)e.getUnfinishedMessage();
                    throw e.unwrapIOException();
                }
                finally {
                    if (parsedMessage != null) {
                        this.mergeFrom(parsedMessage);
                    }
                }
                return this;
            }

            @Override
            public boolean hasError() {
                return (this.bitField0_ & 1) != 0;
            }

            @Override
            public String getError() {
                Object ref = this.error_;
                if (!(ref instanceof String)) {
                    ByteString bs = (ByteString)ref;
                    String s = bs.toStringUtf8();
                    if (bs.isValidUtf8()) {
                        this.error_ = s;
                    }
                    return s;
                }
                return (String)ref;
            }

            @Override
            public ByteString getErrorBytes() {
                Object ref = this.error_;
                if (ref instanceof String) {
                    ByteString b = ByteString.copyFromUtf8((String)ref);
                    this.error_ = b;
                    return b;
                }
                return (ByteString)ref;
            }

            public Builder setError(String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 1;
                this.error_ = value;
                this.onChanged();
                return this;
            }

            public Builder clearError() {
                this.bitField0_ &= 0xFFFFFFFE;
                this.error_ = CodeGeneratorResponse.getDefaultInstance().getError();
                this.onChanged();
                return this;
            }

            public Builder setErrorBytes(ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 1;
                this.error_ = value;
                this.onChanged();
                return this;
            }

            private void ensureFileIsMutable() {
                if ((this.bitField0_ & 2) == 0) {
                    this.file_ = new ArrayList<File>(this.file_);
                    this.bitField0_ |= 2;
                }
            }

            @Override
            public List<File> getFileList() {
                if (this.fileBuilder_ == null) {
                    return Collections.unmodifiableList(this.file_);
                }
                return this.fileBuilder_.getMessageList();
            }

            @Override
            public int getFileCount() {
                if (this.fileBuilder_ == null) {
                    return this.file_.size();
                }
                return this.fileBuilder_.getCount();
            }

            @Override
            public File getFile(int index) {
                if (this.fileBuilder_ == null) {
                    return this.file_.get(index);
                }
                return this.fileBuilder_.getMessage(index);
            }

            public Builder setFile(int index, File value) {
                if (this.fileBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureFileIsMutable();
                    this.file_.set(index, value);
                    this.onChanged();
                } else {
                    this.fileBuilder_.setMessage(index, value);
                }
                return this;
            }

            public Builder setFile(int index, File.Builder builderForValue) {
                if (this.fileBuilder_ == null) {
                    this.ensureFileIsMutable();
                    this.file_.set(index, builderForValue.build());
                    this.onChanged();
                } else {
                    this.fileBuilder_.setMessage(index, builderForValue.build());
                }
                return this;
            }

            public Builder addFile(File value) {
                if (this.fileBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureFileIsMutable();
                    this.file_.add(value);
                    this.onChanged();
                } else {
                    this.fileBuilder_.addMessage(value);
                }
                return this;
            }

            public Builder addFile(int index, File value) {
                if (this.fileBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureFileIsMutable();
                    this.file_.add(index, value);
                    this.onChanged();
                } else {
                    this.fileBuilder_.addMessage(index, value);
                }
                return this;
            }

            public Builder addFile(File.Builder builderForValue) {
                if (this.fileBuilder_ == null) {
                    this.ensureFileIsMutable();
                    this.file_.add(builderForValue.build());
                    this.onChanged();
                } else {
                    this.fileBuilder_.addMessage(builderForValue.build());
                }
                return this;
            }

            public Builder addFile(int index, File.Builder builderForValue) {
                if (this.fileBuilder_ == null) {
                    this.ensureFileIsMutable();
                    this.file_.add(index, builderForValue.build());
                    this.onChanged();
                } else {
                    this.fileBuilder_.addMessage(index, builderForValue.build());
                }
                return this;
            }

            public Builder addAllFile(Iterable<? extends File> values) {
                if (this.fileBuilder_ == null) {
                    this.ensureFileIsMutable();
                    AbstractMessageLite.Builder.addAll(values, this.file_);
                    this.onChanged();
                } else {
                    this.fileBuilder_.addAllMessages(values);
                }
                return this;
            }

            public Builder clearFile() {
                if (this.fileBuilder_ == null) {
                    this.file_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFFD;
                    this.onChanged();
                } else {
                    this.fileBuilder_.clear();
                }
                return this;
            }

            public Builder removeFile(int index) {
                if (this.fileBuilder_ == null) {
                    this.ensureFileIsMutable();
                    this.file_.remove(index);
                    this.onChanged();
                } else {
                    this.fileBuilder_.remove(index);
                }
                return this;
            }

            public File.Builder getFileBuilder(int index) {
                return this.getFileFieldBuilder().getBuilder(index);
            }

            @Override
            public FileOrBuilder getFileOrBuilder(int index) {
                if (this.fileBuilder_ == null) {
                    return this.file_.get(index);
                }
                return this.fileBuilder_.getMessageOrBuilder(index);
            }

            @Override
            public List<? extends FileOrBuilder> getFileOrBuilderList() {
                if (this.fileBuilder_ != null) {
                    return this.fileBuilder_.getMessageOrBuilderList();
                }
                return Collections.unmodifiableList(this.file_);
            }

            public File.Builder addFileBuilder() {
                return this.getFileFieldBuilder().addBuilder(File.getDefaultInstance());
            }

            public File.Builder addFileBuilder(int index) {
                return this.getFileFieldBuilder().addBuilder(index, File.getDefaultInstance());
            }

            public List<File.Builder> getFileBuilderList() {
                return this.getFileFieldBuilder().getBuilderList();
            }

            private RepeatedFieldBuilderV3<File, File.Builder, FileOrBuilder> getFileFieldBuilder() {
                if (this.fileBuilder_ == null) {
                    this.fileBuilder_ = new RepeatedFieldBuilderV3(this.file_, (this.bitField0_ & 2) != 0, this.getParentForChildren(), this.isClean());
                    this.file_ = null;
                }
                return this.fileBuilder_;
            }

            @Override
            public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
                return (Builder)super.setUnknownFields(unknownFields);
            }

            @Override
            public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
                return (Builder)super.mergeUnknownFields(unknownFields);
            }
        }

        public static final class File
        extends GeneratedMessageV3
        implements FileOrBuilder {
            private static final long serialVersionUID = 0L;
            private int bitField0_;
            public static final int NAME_FIELD_NUMBER = 1;
            private volatile Object name_;
            public static final int INSERTION_POINT_FIELD_NUMBER = 2;
            private volatile Object insertionPoint_;
            public static final int CONTENT_FIELD_NUMBER = 15;
            private volatile Object content_;
            private byte memoizedIsInitialized = (byte)-1;
            private static final File DEFAULT_INSTANCE = new File();
            @Deprecated
            public static final Parser<File> PARSER = new AbstractParser<File>(){

                @Override
                public File parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new File(input, extensionRegistry);
                }
            };

            private File(GeneratedMessageV3.Builder<?> builder) {
                super(builder);
            }

            private File() {
                this.name_ = "";
                this.insertionPoint_ = "";
                this.content_ = "";
            }

            @Override
            protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
                return new File();
            }

            @Override
            public final UnknownFieldSet getUnknownFields() {
                return this.unknownFields;
            }

            private File(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                this();
                if (extensionRegistry == null) {
                    throw new NullPointerException();
                }
                boolean mutable_bitField0_ = false;
                UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();
                try {
                    boolean done = false;
                    block12: while (!done) {
                        int tag = input.readTag();
                        switch (tag) {
                            case 0: {
                                done = true;
                                continue block12;
                            }
                            case 10: {
                                ByteString bs = input.readBytes();
                                this.bitField0_ |= 1;
                                this.name_ = bs;
                                continue block12;
                            }
                            case 18: {
                                ByteString bs = input.readBytes();
                                this.bitField0_ |= 2;
                                this.insertionPoint_ = bs;
                                continue block12;
                            }
                            case 122: {
                                ByteString bs = input.readBytes();
                                this.bitField0_ |= 4;
                                this.content_ = bs;
                                continue block12;
                            }
                        }
                        if (this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) continue;
                        done = true;
                    }
                }
                catch (InvalidProtocolBufferException e) {
                    throw e.setUnfinishedMessage(this);
                }
                catch (IOException e) {
                    throw new InvalidProtocolBufferException(e).setUnfinishedMessage(this);
                }
                finally {
                    this.unknownFields = unknownFields.build();
                    this.makeExtensionsImmutable();
                }
            }

            public static final Descriptors.Descriptor getDescriptor() {
                return internal_static_google_protobuf_compiler_CodeGeneratorResponse_File_descriptor;
            }

            @Override
            protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
                return internal_static_google_protobuf_compiler_CodeGeneratorResponse_File_fieldAccessorTable.ensureFieldAccessorsInitialized(File.class, Builder.class);
            }

            @Override
            public boolean hasName() {
                return (this.bitField0_ & 1) != 0;
            }

            @Override
            public String getName() {
                Object ref = this.name_;
                if (ref instanceof String) {
                    return (String)ref;
                }
                ByteString bs = (ByteString)ref;
                String s = bs.toStringUtf8();
                if (bs.isValidUtf8()) {
                    this.name_ = s;
                }
                return s;
            }

            @Override
            public ByteString getNameBytes() {
                Object ref = this.name_;
                if (ref instanceof String) {
                    ByteString b = ByteString.copyFromUtf8((String)ref);
                    this.name_ = b;
                    return b;
                }
                return (ByteString)ref;
            }

            @Override
            public boolean hasInsertionPoint() {
                return (this.bitField0_ & 2) != 0;
            }

            @Override
            public String getInsertionPoint() {
                Object ref = this.insertionPoint_;
                if (ref instanceof String) {
                    return (String)ref;
                }
                ByteString bs = (ByteString)ref;
                String s = bs.toStringUtf8();
                if (bs.isValidUtf8()) {
                    this.insertionPoint_ = s;
                }
                return s;
            }

            @Override
            public ByteString getInsertionPointBytes() {
                Object ref = this.insertionPoint_;
                if (ref instanceof String) {
                    ByteString b = ByteString.copyFromUtf8((String)ref);
                    this.insertionPoint_ = b;
                    return b;
                }
                return (ByteString)ref;
            }

            @Override
            public boolean hasContent() {
                return (this.bitField0_ & 4) != 0;
            }

            @Override
            public String getContent() {
                Object ref = this.content_;
                if (ref instanceof String) {
                    return (String)ref;
                }
                ByteString bs = (ByteString)ref;
                String s = bs.toStringUtf8();
                if (bs.isValidUtf8()) {
                    this.content_ = s;
                }
                return s;
            }

            @Override
            public ByteString getContentBytes() {
                Object ref = this.content_;
                if (ref instanceof String) {
                    ByteString b = ByteString.copyFromUtf8((String)ref);
                    this.content_ = b;
                    return b;
                }
                return (ByteString)ref;
            }

            @Override
            public final boolean isInitialized() {
                byte isInitialized = this.memoizedIsInitialized;
                if (isInitialized == 1) {
                    return true;
                }
                if (isInitialized == 0) {
                    return false;
                }
                this.memoizedIsInitialized = 1;
                return true;
            }

            @Override
            public void writeTo(CodedOutputStream output) throws IOException {
                if ((this.bitField0_ & 1) != 0) {
                    GeneratedMessageV3.writeString(output, 1, this.name_);
                }
                if ((this.bitField0_ & 2) != 0) {
                    GeneratedMessageV3.writeString(output, 2, this.insertionPoint_);
                }
                if ((this.bitField0_ & 4) != 0) {
                    GeneratedMessageV3.writeString(output, 15, this.content_);
                }
                this.unknownFields.writeTo(output);
            }

            @Override
            public int getSerializedSize() {
                int size = this.memoizedSize;
                if (size != -1) {
                    return size;
                }
                size = 0;
                if ((this.bitField0_ & 1) != 0) {
                    size += GeneratedMessageV3.computeStringSize(1, this.name_);
                }
                if ((this.bitField0_ & 2) != 0) {
                    size += GeneratedMessageV3.computeStringSize(2, this.insertionPoint_);
                }
                if ((this.bitField0_ & 4) != 0) {
                    size += GeneratedMessageV3.computeStringSize(15, this.content_);
                }
                this.memoizedSize = size += this.unknownFields.getSerializedSize();
                return size;
            }

            @Override
            public boolean equals(Object obj) {
                if (obj == this) {
                    return true;
                }
                if (!(obj instanceof File)) {
                    return super.equals(obj);
                }
                File other = (File)obj;
                if (this.hasName() != other.hasName()) {
                    return false;
                }
                if (this.hasName() && !this.getName().equals(other.getName())) {
                    return false;
                }
                if (this.hasInsertionPoint() != other.hasInsertionPoint()) {
                    return false;
                }
                if (this.hasInsertionPoint() && !this.getInsertionPoint().equals(other.getInsertionPoint())) {
                    return false;
                }
                if (this.hasContent() != other.hasContent()) {
                    return false;
                }
                if (this.hasContent() && !this.getContent().equals(other.getContent())) {
                    return false;
                }
                return this.unknownFields.equals(other.unknownFields);
            }

            @Override
            public int hashCode() {
                if (this.memoizedHashCode != 0) {
                    return this.memoizedHashCode;
                }
                int hash = 41;
                hash = 19 * hash + File.getDescriptor().hashCode();
                if (this.hasName()) {
                    hash = 37 * hash + 1;
                    hash = 53 * hash + this.getName().hashCode();
                }
                if (this.hasInsertionPoint()) {
                    hash = 37 * hash + 2;
                    hash = 53 * hash + this.getInsertionPoint().hashCode();
                }
                if (this.hasContent()) {
                    hash = 37 * hash + 15;
                    hash = 53 * hash + this.getContent().hashCode();
                }
                this.memoizedHashCode = hash = 29 * hash + this.unknownFields.hashCode();
                return hash;
            }

            public static File parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
                return PARSER.parseFrom(data);
            }

            public static File parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return PARSER.parseFrom(data, extensionRegistry);
            }

            public static File parseFrom(ByteString data) throws InvalidProtocolBufferException {
                return PARSER.parseFrom(data);
            }

            public static File parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return PARSER.parseFrom(data, extensionRegistry);
            }

            public static File parseFrom(byte[] data) throws InvalidProtocolBufferException {
                return PARSER.parseFrom(data);
            }

            public static File parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return PARSER.parseFrom(data, extensionRegistry);
            }

            public static File parseFrom(InputStream input) throws IOException {
                return GeneratedMessageV3.parseWithIOException(PARSER, input);
            }

            public static File parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
            }

            public static File parseDelimitedFrom(InputStream input) throws IOException {
                return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
            }

            public static File parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
            }

            public static File parseFrom(CodedInputStream input) throws IOException {
                return GeneratedMessageV3.parseWithIOException(PARSER, input);
            }

            public static File parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
            }

            @Override
            public Builder newBuilderForType() {
                return File.newBuilder();
            }

            public static Builder newBuilder() {
                return DEFAULT_INSTANCE.toBuilder();
            }

            public static Builder newBuilder(File prototype) {
                return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
            }

            @Override
            public Builder toBuilder() {
                return this == DEFAULT_INSTANCE ? new Builder() : new Builder().mergeFrom(this);
            }

            @Override
            protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
                Builder builder = new Builder(parent);
                return builder;
            }

            public static File getDefaultInstance() {
                return DEFAULT_INSTANCE;
            }

            public static Parser<File> parser() {
                return PARSER;
            }

            public Parser<File> getParserForType() {
                return PARSER;
            }

            @Override
            public File getDefaultInstanceForType() {
                return DEFAULT_INSTANCE;
            }

            public static final class Builder
            extends GeneratedMessageV3.Builder<Builder>
            implements FileOrBuilder {
                private int bitField0_;
                private Object name_ = "";
                private Object insertionPoint_ = "";
                private Object content_ = "";

                public static final Descriptors.Descriptor getDescriptor() {
                    return internal_static_google_protobuf_compiler_CodeGeneratorResponse_File_descriptor;
                }

                @Override
                protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
                    return internal_static_google_protobuf_compiler_CodeGeneratorResponse_File_fieldAccessorTable.ensureFieldAccessorsInitialized(File.class, Builder.class);
                }

                private Builder() {
                    this.maybeForceBuilderInitialization();
                }

                private Builder(GeneratedMessageV3.BuilderParent parent) {
                    super(parent);
                    this.maybeForceBuilderInitialization();
                }

                private void maybeForceBuilderInitialization() {
                    if (alwaysUseFieldBuilders) {
                        // empty if block
                    }
                }

                @Override
                public Builder clear() {
                    super.clear();
                    this.name_ = "";
                    this.bitField0_ &= 0xFFFFFFFE;
                    this.insertionPoint_ = "";
                    this.bitField0_ &= 0xFFFFFFFD;
                    this.content_ = "";
                    this.bitField0_ &= 0xFFFFFFFB;
                    return this;
                }

                @Override
                public Descriptors.Descriptor getDescriptorForType() {
                    return internal_static_google_protobuf_compiler_CodeGeneratorResponse_File_descriptor;
                }

                @Override
                public File getDefaultInstanceForType() {
                    return File.getDefaultInstance();
                }

                @Override
                public File build() {
                    File result = this.buildPartial();
                    if (!result.isInitialized()) {
                        throw Builder.newUninitializedMessageException(result);
                    }
                    return result;
                }

                @Override
                public File buildPartial() {
                    File result = new File(this);
                    int from_bitField0_ = this.bitField0_;
                    int to_bitField0_ = 0;
                    if ((from_bitField0_ & 1) != 0) {
                        to_bitField0_ |= 1;
                    }
                    result.name_ = this.name_;
                    if ((from_bitField0_ & 2) != 0) {
                        to_bitField0_ |= 2;
                    }
                    result.insertionPoint_ = this.insertionPoint_;
                    if ((from_bitField0_ & 4) != 0) {
                        to_bitField0_ |= 4;
                    }
                    result.content_ = this.content_;
                    result.bitField0_ = to_bitField0_;
                    this.onBuilt();
                    return result;
                }

                @Override
                public Builder clone() {
                    return (Builder)super.clone();
                }

                @Override
                public Builder setField(Descriptors.FieldDescriptor field, Object value) {
                    return (Builder)super.setField(field, value);
                }

                @Override
                public Builder clearField(Descriptors.FieldDescriptor field) {
                    return (Builder)super.clearField(field);
                }

                @Override
                public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
                    return (Builder)super.clearOneof(oneof);
                }

                @Override
                public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
                    return (Builder)super.setRepeatedField(field, index, value);
                }

                @Override
                public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
                    return (Builder)super.addRepeatedField(field, value);
                }

                @Override
                public Builder mergeFrom(Message other) {
                    if (other instanceof File) {
                        return this.mergeFrom((File)other);
                    }
                    super.mergeFrom(other);
                    return this;
                }

                public Builder mergeFrom(File other) {
                    if (other == File.getDefaultInstance()) {
                        return this;
                    }
                    if (other.hasName()) {
                        this.bitField0_ |= 1;
                        this.name_ = other.name_;
                        this.onChanged();
                    }
                    if (other.hasInsertionPoint()) {
                        this.bitField0_ |= 2;
                        this.insertionPoint_ = other.insertionPoint_;
                        this.onChanged();
                    }
                    if (other.hasContent()) {
                        this.bitField0_ |= 4;
                        this.content_ = other.content_;
                        this.onChanged();
                    }
                    this.mergeUnknownFields(other.unknownFields);
                    this.onChanged();
                    return this;
                }

                @Override
                public final boolean isInitialized() {
                    return true;
                }

                @Override
                public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                    File parsedMessage = null;
                    try {
                        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
                    }
                    catch (InvalidProtocolBufferException e) {
                        parsedMessage = (File)e.getUnfinishedMessage();
                        throw e.unwrapIOException();
                    }
                    finally {
                        if (parsedMessage != null) {
                            this.mergeFrom(parsedMessage);
                        }
                    }
                    return this;
                }

                @Override
                public boolean hasName() {
                    return (this.bitField0_ & 1) != 0;
                }

                @Override
                public String getName() {
                    Object ref = this.name_;
                    if (!(ref instanceof String)) {
                        ByteString bs = (ByteString)ref;
                        String s = bs.toStringUtf8();
                        if (bs.isValidUtf8()) {
                            this.name_ = s;
                        }
                        return s;
                    }
                    return (String)ref;
                }

                @Override
                public ByteString getNameBytes() {
                    Object ref = this.name_;
                    if (ref instanceof String) {
                        ByteString b = ByteString.copyFromUtf8((String)ref);
                        this.name_ = b;
                        return b;
                    }
                    return (ByteString)ref;
                }

                public Builder setName(String value) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.bitField0_ |= 1;
                    this.name_ = value;
                    this.onChanged();
                    return this;
                }

                public Builder clearName() {
                    this.bitField0_ &= 0xFFFFFFFE;
                    this.name_ = File.getDefaultInstance().getName();
                    this.onChanged();
                    return this;
                }

                public Builder setNameBytes(ByteString value) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.bitField0_ |= 1;
                    this.name_ = value;
                    this.onChanged();
                    return this;
                }

                @Override
                public boolean hasInsertionPoint() {
                    return (this.bitField0_ & 2) != 0;
                }

                @Override
                public String getInsertionPoint() {
                    Object ref = this.insertionPoint_;
                    if (!(ref instanceof String)) {
                        ByteString bs = (ByteString)ref;
                        String s = bs.toStringUtf8();
                        if (bs.isValidUtf8()) {
                            this.insertionPoint_ = s;
                        }
                        return s;
                    }
                    return (String)ref;
                }

                @Override
                public ByteString getInsertionPointBytes() {
                    Object ref = this.insertionPoint_;
                    if (ref instanceof String) {
                        ByteString b = ByteString.copyFromUtf8((String)ref);
                        this.insertionPoint_ = b;
                        return b;
                    }
                    return (ByteString)ref;
                }

                public Builder setInsertionPoint(String value) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.bitField0_ |= 2;
                    this.insertionPoint_ = value;
                    this.onChanged();
                    return this;
                }

                public Builder clearInsertionPoint() {
                    this.bitField0_ &= 0xFFFFFFFD;
                    this.insertionPoint_ = File.getDefaultInstance().getInsertionPoint();
                    this.onChanged();
                    return this;
                }

                public Builder setInsertionPointBytes(ByteString value) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.bitField0_ |= 2;
                    this.insertionPoint_ = value;
                    this.onChanged();
                    return this;
                }

                @Override
                public boolean hasContent() {
                    return (this.bitField0_ & 4) != 0;
                }

                @Override
                public String getContent() {
                    Object ref = this.content_;
                    if (!(ref instanceof String)) {
                        ByteString bs = (ByteString)ref;
                        String s = bs.toStringUtf8();
                        if (bs.isValidUtf8()) {
                            this.content_ = s;
                        }
                        return s;
                    }
                    return (String)ref;
                }

                @Override
                public ByteString getContentBytes() {
                    Object ref = this.content_;
                    if (ref instanceof String) {
                        ByteString b = ByteString.copyFromUtf8((String)ref);
                        this.content_ = b;
                        return b;
                    }
                    return (ByteString)ref;
                }

                public Builder setContent(String value) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.bitField0_ |= 4;
                    this.content_ = value;
                    this.onChanged();
                    return this;
                }

                public Builder clearContent() {
                    this.bitField0_ &= 0xFFFFFFFB;
                    this.content_ = File.getDefaultInstance().getContent();
                    this.onChanged();
                    return this;
                }

                public Builder setContentBytes(ByteString value) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.bitField0_ |= 4;
                    this.content_ = value;
                    this.onChanged();
                    return this;
                }

                @Override
                public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
                    return (Builder)super.setUnknownFields(unknownFields);
                }

                @Override
                public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
                    return (Builder)super.mergeUnknownFields(unknownFields);
                }
            }
        }

        public static interface FileOrBuilder
        extends MessageOrBuilder {
            public boolean hasName();

            public String getName();

            public ByteString getNameBytes();

            public boolean hasInsertionPoint();

            public String getInsertionPoint();

            public ByteString getInsertionPointBytes();

            public boolean hasContent();

            public String getContent();

            public ByteString getContentBytes();
        }
    }

    public static interface CodeGeneratorResponseOrBuilder
    extends MessageOrBuilder {
        public boolean hasError();

        public String getError();

        public ByteString getErrorBytes();

        public List<CodeGeneratorResponse.File> getFileList();

        public CodeGeneratorResponse.File getFile(int var1);

        public int getFileCount();

        public List<? extends CodeGeneratorResponse.FileOrBuilder> getFileOrBuilderList();

        public CodeGeneratorResponse.FileOrBuilder getFileOrBuilder(int var1);
    }

    public static final class CodeGeneratorRequest
    extends GeneratedMessageV3
    implements CodeGeneratorRequestOrBuilder {
        private static final long serialVersionUID = 0L;
        private int bitField0_;
        public static final int FILE_TO_GENERATE_FIELD_NUMBER = 1;
        private LazyStringList fileToGenerate_;
        public static final int PARAMETER_FIELD_NUMBER = 2;
        private volatile Object parameter_;
        public static final int PROTO_FILE_FIELD_NUMBER = 15;
        private List<DescriptorProtos.FileDescriptorProto> protoFile_;
        public static final int COMPILER_VERSION_FIELD_NUMBER = 3;
        private Version compilerVersion_;
        private byte memoizedIsInitialized = (byte)-1;
        private static final CodeGeneratorRequest DEFAULT_INSTANCE = new CodeGeneratorRequest();
        @Deprecated
        public static final Parser<CodeGeneratorRequest> PARSER = new AbstractParser<CodeGeneratorRequest>(){

            @Override
            public CodeGeneratorRequest parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return new CodeGeneratorRequest(input, extensionRegistry);
            }
        };

        private CodeGeneratorRequest(GeneratedMessageV3.Builder<?> builder) {
            super(builder);
        }

        private CodeGeneratorRequest() {
            this.fileToGenerate_ = LazyStringArrayList.EMPTY;
            this.parameter_ = "";
            this.protoFile_ = Collections.emptyList();
        }

        @Override
        protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
            return new CodeGeneratorRequest();
        }

        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }

        private CodeGeneratorRequest(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            this();
            if (extensionRegistry == null) {
                throw new NullPointerException();
            }
            int mutable_bitField0_ = 0;
            UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();
            try {
                boolean done = false;
                block13: while (!done) {
                    int tag = input.readTag();
                    switch (tag) {
                        case 0: {
                            done = true;
                            continue block13;
                        }
                        case 10: {
                            ByteString bs = input.readBytes();
                            if ((mutable_bitField0_ & 1) == 0) {
                                this.fileToGenerate_ = new LazyStringArrayList();
                                mutable_bitField0_ |= 1;
                            }
                            this.fileToGenerate_.add(bs);
                            continue block13;
                        }
                        case 18: {
                            ByteString bs = input.readBytes();
                            this.bitField0_ |= 1;
                            this.parameter_ = bs;
                            continue block13;
                        }
                        case 26: {
                            Version.Builder subBuilder = null;
                            if ((this.bitField0_ & 2) != 0) {
                                subBuilder = this.compilerVersion_.toBuilder();
                            }
                            this.compilerVersion_ = input.readMessage(Version.PARSER, extensionRegistry);
                            if (subBuilder != null) {
                                subBuilder.mergeFrom(this.compilerVersion_);
                                this.compilerVersion_ = subBuilder.buildPartial();
                            }
                            this.bitField0_ |= 2;
                            continue block13;
                        }
                        case 122: {
                            if ((mutable_bitField0_ & 4) == 0) {
                                this.protoFile_ = new ArrayList<DescriptorProtos.FileDescriptorProto>();
                                mutable_bitField0_ |= 4;
                            }
                            this.protoFile_.add(input.readMessage(DescriptorProtos.FileDescriptorProto.PARSER, extensionRegistry));
                            continue block13;
                        }
                    }
                    if (this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) continue;
                    done = true;
                }
            }
            catch (InvalidProtocolBufferException e) {
                throw e.setUnfinishedMessage(this);
            }
            catch (IOException e) {
                throw new InvalidProtocolBufferException(e).setUnfinishedMessage(this);
            }
            finally {
                if (mutable_bitField0_ & true) {
                    this.fileToGenerate_ = this.fileToGenerate_.getUnmodifiableView();
                }
                if ((mutable_bitField0_ & 4) != 0) {
                    this.protoFile_ = Collections.unmodifiableList(this.protoFile_);
                }
                this.unknownFields = unknownFields.build();
                this.makeExtensionsImmutable();
            }
        }

        public static final Descriptors.Descriptor getDescriptor() {
            return internal_static_google_protobuf_compiler_CodeGeneratorRequest_descriptor;
        }

        @Override
        protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return internal_static_google_protobuf_compiler_CodeGeneratorRequest_fieldAccessorTable.ensureFieldAccessorsInitialized(CodeGeneratorRequest.class, Builder.class);
        }

        public ProtocolStringList getFileToGenerateList() {
            return this.fileToGenerate_;
        }

        @Override
        public int getFileToGenerateCount() {
            return this.fileToGenerate_.size();
        }

        @Override
        public String getFileToGenerate(int index) {
            return (String)this.fileToGenerate_.get(index);
        }

        @Override
        public ByteString getFileToGenerateBytes(int index) {
            return this.fileToGenerate_.getByteString(index);
        }

        @Override
        public boolean hasParameter() {
            return (this.bitField0_ & 1) != 0;
        }

        @Override
        public String getParameter() {
            Object ref = this.parameter_;
            if (ref instanceof String) {
                return (String)ref;
            }
            ByteString bs = (ByteString)ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.parameter_ = s;
            }
            return s;
        }

        @Override
        public ByteString getParameterBytes() {
            Object ref = this.parameter_;
            if (ref instanceof String) {
                ByteString b = ByteString.copyFromUtf8((String)ref);
                this.parameter_ = b;
                return b;
            }
            return (ByteString)ref;
        }

        @Override
        public List<DescriptorProtos.FileDescriptorProto> getProtoFileList() {
            return this.protoFile_;
        }

        @Override
        public List<? extends DescriptorProtos.FileDescriptorProtoOrBuilder> getProtoFileOrBuilderList() {
            return this.protoFile_;
        }

        @Override
        public int getProtoFileCount() {
            return this.protoFile_.size();
        }

        @Override
        public DescriptorProtos.FileDescriptorProto getProtoFile(int index) {
            return this.protoFile_.get(index);
        }

        @Override
        public DescriptorProtos.FileDescriptorProtoOrBuilder getProtoFileOrBuilder(int index) {
            return this.protoFile_.get(index);
        }

        @Override
        public boolean hasCompilerVersion() {
            return (this.bitField0_ & 2) != 0;
        }

        @Override
        public Version getCompilerVersion() {
            return this.compilerVersion_ == null ? Version.getDefaultInstance() : this.compilerVersion_;
        }

        @Override
        public VersionOrBuilder getCompilerVersionOrBuilder() {
            return this.compilerVersion_ == null ? Version.getDefaultInstance() : this.compilerVersion_;
        }

        @Override
        public final boolean isInitialized() {
            byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized == 1) {
                return true;
            }
            if (isInitialized == 0) {
                return false;
            }
            for (int i = 0; i < this.getProtoFileCount(); ++i) {
                if (this.getProtoFile(i).isInitialized()) continue;
                this.memoizedIsInitialized = 0;
                return false;
            }
            this.memoizedIsInitialized = 1;
            return true;
        }

        @Override
        public void writeTo(CodedOutputStream output) throws IOException {
            int i;
            for (i = 0; i < this.fileToGenerate_.size(); ++i) {
                GeneratedMessageV3.writeString(output, 1, this.fileToGenerate_.getRaw(i));
            }
            if ((this.bitField0_ & 1) != 0) {
                GeneratedMessageV3.writeString(output, 2, this.parameter_);
            }
            if ((this.bitField0_ & 2) != 0) {
                output.writeMessage(3, this.getCompilerVersion());
            }
            for (i = 0; i < this.protoFile_.size(); ++i) {
                output.writeMessage(15, this.protoFile_.get(i));
            }
            this.unknownFields.writeTo(output);
        }

        @Override
        public int getSerializedSize() {
            int size = this.memoizedSize;
            if (size != -1) {
                return size;
            }
            size = 0;
            int dataSize = 0;
            for (int i = 0; i < this.fileToGenerate_.size(); ++i) {
                dataSize += CodeGeneratorRequest.computeStringSizeNoTag(this.fileToGenerate_.getRaw(i));
            }
            size += dataSize;
            size += 1 * this.getFileToGenerateList().size();
            if ((this.bitField0_ & 1) != 0) {
                size += GeneratedMessageV3.computeStringSize(2, this.parameter_);
            }
            if ((this.bitField0_ & 2) != 0) {
                size += CodedOutputStream.computeMessageSize(3, this.getCompilerVersion());
            }
            for (int i = 0; i < this.protoFile_.size(); ++i) {
                size += CodedOutputStream.computeMessageSize(15, this.protoFile_.get(i));
            }
            this.memoizedSize = size += this.unknownFields.getSerializedSize();
            return size;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof CodeGeneratorRequest)) {
                return super.equals(obj);
            }
            CodeGeneratorRequest other = (CodeGeneratorRequest)obj;
            if (!this.getFileToGenerateList().equals(other.getFileToGenerateList())) {
                return false;
            }
            if (this.hasParameter() != other.hasParameter()) {
                return false;
            }
            if (this.hasParameter() && !this.getParameter().equals(other.getParameter())) {
                return false;
            }
            if (!this.getProtoFileList().equals(other.getProtoFileList())) {
                return false;
            }
            if (this.hasCompilerVersion() != other.hasCompilerVersion()) {
                return false;
            }
            if (this.hasCompilerVersion() && !this.getCompilerVersion().equals(other.getCompilerVersion())) {
                return false;
            }
            return this.unknownFields.equals(other.unknownFields);
        }

        @Override
        public int hashCode() {
            if (this.memoizedHashCode != 0) {
                return this.memoizedHashCode;
            }
            int hash = 41;
            hash = 19 * hash + CodeGeneratorRequest.getDescriptor().hashCode();
            if (this.getFileToGenerateCount() > 0) {
                hash = 37 * hash + 1;
                hash = 53 * hash + this.getFileToGenerateList().hashCode();
            }
            if (this.hasParameter()) {
                hash = 37 * hash + 2;
                hash = 53 * hash + this.getParameter().hashCode();
            }
            if (this.getProtoFileCount() > 0) {
                hash = 37 * hash + 15;
                hash = 53 * hash + this.getProtoFileList().hashCode();
            }
            if (this.hasCompilerVersion()) {
                hash = 37 * hash + 3;
                hash = 53 * hash + this.getCompilerVersion().hashCode();
            }
            this.memoizedHashCode = hash = 29 * hash + this.unknownFields.hashCode();
            return hash;
        }

        public static CodeGeneratorRequest parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static CodeGeneratorRequest parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static CodeGeneratorRequest parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static CodeGeneratorRequest parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static CodeGeneratorRequest parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static CodeGeneratorRequest parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static CodeGeneratorRequest parseFrom(InputStream input) throws IOException {
            return GeneratedMessageV3.parseWithIOException(PARSER, input);
        }

        public static CodeGeneratorRequest parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
        }

        public static CodeGeneratorRequest parseDelimitedFrom(InputStream input) throws IOException {
            return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
        }

        public static CodeGeneratorRequest parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
        }

        public static CodeGeneratorRequest parseFrom(CodedInputStream input) throws IOException {
            return GeneratedMessageV3.parseWithIOException(PARSER, input);
        }

        public static CodeGeneratorRequest parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
        }

        @Override
        public Builder newBuilderForType() {
            return CodeGeneratorRequest.newBuilder();
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(CodeGeneratorRequest prototype) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
        }

        @Override
        public Builder toBuilder() {
            return this == DEFAULT_INSTANCE ? new Builder() : new Builder().mergeFrom(this);
        }

        @Override
        protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
            Builder builder = new Builder(parent);
            return builder;
        }

        public static CodeGeneratorRequest getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<CodeGeneratorRequest> parser() {
            return PARSER;
        }

        public Parser<CodeGeneratorRequest> getParserForType() {
            return PARSER;
        }

        @Override
        public CodeGeneratorRequest getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }

        public static final class Builder
        extends GeneratedMessageV3.Builder<Builder>
        implements CodeGeneratorRequestOrBuilder {
            private int bitField0_;
            private LazyStringList fileToGenerate_ = LazyStringArrayList.EMPTY;
            private Object parameter_ = "";
            private List<DescriptorProtos.FileDescriptorProto> protoFile_ = Collections.emptyList();
            private RepeatedFieldBuilderV3<DescriptorProtos.FileDescriptorProto, DescriptorProtos.FileDescriptorProto.Builder, DescriptorProtos.FileDescriptorProtoOrBuilder> protoFileBuilder_;
            private Version compilerVersion_;
            private SingleFieldBuilderV3<Version, Version.Builder, VersionOrBuilder> compilerVersionBuilder_;

            public static final Descriptors.Descriptor getDescriptor() {
                return internal_static_google_protobuf_compiler_CodeGeneratorRequest_descriptor;
            }

            @Override
            protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
                return internal_static_google_protobuf_compiler_CodeGeneratorRequest_fieldAccessorTable.ensureFieldAccessorsInitialized(CodeGeneratorRequest.class, Builder.class);
            }

            private Builder() {
                this.maybeForceBuilderInitialization();
            }

            private Builder(GeneratedMessageV3.BuilderParent parent) {
                super(parent);
                this.maybeForceBuilderInitialization();
            }

            private void maybeForceBuilderInitialization() {
                if (alwaysUseFieldBuilders) {
                    this.getProtoFileFieldBuilder();
                    this.getCompilerVersionFieldBuilder();
                }
            }

            @Override
            public Builder clear() {
                super.clear();
                this.fileToGenerate_ = LazyStringArrayList.EMPTY;
                this.bitField0_ &= 0xFFFFFFFE;
                this.parameter_ = "";
                this.bitField0_ &= 0xFFFFFFFD;
                if (this.protoFileBuilder_ == null) {
                    this.protoFile_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFFB;
                } else {
                    this.protoFileBuilder_.clear();
                }
                if (this.compilerVersionBuilder_ == null) {
                    this.compilerVersion_ = null;
                } else {
                    this.compilerVersionBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFF7;
                return this;
            }

            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return internal_static_google_protobuf_compiler_CodeGeneratorRequest_descriptor;
            }

            @Override
            public CodeGeneratorRequest getDefaultInstanceForType() {
                return CodeGeneratorRequest.getDefaultInstance();
            }

            @Override
            public CodeGeneratorRequest build() {
                CodeGeneratorRequest result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw Builder.newUninitializedMessageException(result);
                }
                return result;
            }

            @Override
            public CodeGeneratorRequest buildPartial() {
                CodeGeneratorRequest result = new CodeGeneratorRequest(this);
                int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((this.bitField0_ & 1) != 0) {
                    this.fileToGenerate_ = this.fileToGenerate_.getUnmodifiableView();
                    this.bitField0_ &= 0xFFFFFFFE;
                }
                result.fileToGenerate_ = this.fileToGenerate_;
                if ((from_bitField0_ & 2) != 0) {
                    to_bitField0_ |= 1;
                }
                result.parameter_ = this.parameter_;
                if (this.protoFileBuilder_ == null) {
                    if ((this.bitField0_ & 4) != 0) {
                        this.protoFile_ = Collections.unmodifiableList(this.protoFile_);
                        this.bitField0_ &= 0xFFFFFFFB;
                    }
                    result.protoFile_ = this.protoFile_;
                } else {
                    result.protoFile_ = this.protoFileBuilder_.build();
                }
                if ((from_bitField0_ & 8) != 0) {
                    if (this.compilerVersionBuilder_ == null) {
                        result.compilerVersion_ = this.compilerVersion_;
                    } else {
                        result.compilerVersion_ = this.compilerVersionBuilder_.build();
                    }
                    to_bitField0_ |= 2;
                }
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }

            @Override
            public Builder clone() {
                return (Builder)super.clone();
            }

            @Override
            public Builder setField(Descriptors.FieldDescriptor field, Object value) {
                return (Builder)super.setField(field, value);
            }

            @Override
            public Builder clearField(Descriptors.FieldDescriptor field) {
                return (Builder)super.clearField(field);
            }

            @Override
            public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
                return (Builder)super.clearOneof(oneof);
            }

            @Override
            public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
                return (Builder)super.setRepeatedField(field, index, value);
            }

            @Override
            public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
                return (Builder)super.addRepeatedField(field, value);
            }

            @Override
            public Builder mergeFrom(Message other) {
                if (other instanceof CodeGeneratorRequest) {
                    return this.mergeFrom((CodeGeneratorRequest)other);
                }
                super.mergeFrom(other);
                return this;
            }

            public Builder mergeFrom(CodeGeneratorRequest other) {
                if (other == CodeGeneratorRequest.getDefaultInstance()) {
                    return this;
                }
                if (!other.fileToGenerate_.isEmpty()) {
                    if (this.fileToGenerate_.isEmpty()) {
                        this.fileToGenerate_ = other.fileToGenerate_;
                        this.bitField0_ &= 0xFFFFFFFE;
                    } else {
                        this.ensureFileToGenerateIsMutable();
                        this.fileToGenerate_.addAll(other.fileToGenerate_);
                    }
                    this.onChanged();
                }
                if (other.hasParameter()) {
                    this.bitField0_ |= 2;
                    this.parameter_ = other.parameter_;
                    this.onChanged();
                }
                if (this.protoFileBuilder_ == null) {
                    if (!other.protoFile_.isEmpty()) {
                        if (this.protoFile_.isEmpty()) {
                            this.protoFile_ = other.protoFile_;
                            this.bitField0_ &= 0xFFFFFFFB;
                        } else {
                            this.ensureProtoFileIsMutable();
                            this.protoFile_.addAll(other.protoFile_);
                        }
                        this.onChanged();
                    }
                } else if (!other.protoFile_.isEmpty()) {
                    if (this.protoFileBuilder_.isEmpty()) {
                        this.protoFileBuilder_.dispose();
                        this.protoFileBuilder_ = null;
                        this.protoFile_ = other.protoFile_;
                        this.bitField0_ &= 0xFFFFFFFB;
                        this.protoFileBuilder_ = alwaysUseFieldBuilders ? this.getProtoFileFieldBuilder() : null;
                    } else {
                        this.protoFileBuilder_.addAllMessages(other.protoFile_);
                    }
                }
                if (other.hasCompilerVersion()) {
                    this.mergeCompilerVersion(other.getCompilerVersion());
                }
                this.mergeUnknownFields(other.unknownFields);
                this.onChanged();
                return this;
            }

            @Override
            public final boolean isInitialized() {
                for (int i = 0; i < this.getProtoFileCount(); ++i) {
                    if (this.getProtoFile(i).isInitialized()) continue;
                    return false;
                }
                return true;
            }

            @Override
            public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                CodeGeneratorRequest parsedMessage = null;
                try {
                    parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (CodeGeneratorRequest)e.getUnfinishedMessage();
                    throw e.unwrapIOException();
                }
                finally {
                    if (parsedMessage != null) {
                        this.mergeFrom(parsedMessage);
                    }
                }
                return this;
            }

            private void ensureFileToGenerateIsMutable() {
                if ((this.bitField0_ & 1) == 0) {
                    this.fileToGenerate_ = new LazyStringArrayList(this.fileToGenerate_);
                    this.bitField0_ |= 1;
                }
            }

            public ProtocolStringList getFileToGenerateList() {
                return this.fileToGenerate_.getUnmodifiableView();
            }

            @Override
            public int getFileToGenerateCount() {
                return this.fileToGenerate_.size();
            }

            @Override
            public String getFileToGenerate(int index) {
                return (String)this.fileToGenerate_.get(index);
            }

            @Override
            public ByteString getFileToGenerateBytes(int index) {
                return this.fileToGenerate_.getByteString(index);
            }

            public Builder setFileToGenerate(int index, String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.ensureFileToGenerateIsMutable();
                this.fileToGenerate_.set(index, value);
                this.onChanged();
                return this;
            }

            public Builder addFileToGenerate(String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.ensureFileToGenerateIsMutable();
                this.fileToGenerate_.add(value);
                this.onChanged();
                return this;
            }

            public Builder addAllFileToGenerate(Iterable<String> values) {
                this.ensureFileToGenerateIsMutable();
                AbstractMessageLite.Builder.addAll(values, this.fileToGenerate_);
                this.onChanged();
                return this;
            }

            public Builder clearFileToGenerate() {
                this.fileToGenerate_ = LazyStringArrayList.EMPTY;
                this.bitField0_ &= 0xFFFFFFFE;
                this.onChanged();
                return this;
            }

            public Builder addFileToGenerateBytes(ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.ensureFileToGenerateIsMutable();
                this.fileToGenerate_.add(value);
                this.onChanged();
                return this;
            }

            @Override
            public boolean hasParameter() {
                return (this.bitField0_ & 2) != 0;
            }

            @Override
            public String getParameter() {
                Object ref = this.parameter_;
                if (!(ref instanceof String)) {
                    ByteString bs = (ByteString)ref;
                    String s = bs.toStringUtf8();
                    if (bs.isValidUtf8()) {
                        this.parameter_ = s;
                    }
                    return s;
                }
                return (String)ref;
            }

            @Override
            public ByteString getParameterBytes() {
                Object ref = this.parameter_;
                if (ref instanceof String) {
                    ByteString b = ByteString.copyFromUtf8((String)ref);
                    this.parameter_ = b;
                    return b;
                }
                return (ByteString)ref;
            }

            public Builder setParameter(String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 2;
                this.parameter_ = value;
                this.onChanged();
                return this;
            }

            public Builder clearParameter() {
                this.bitField0_ &= 0xFFFFFFFD;
                this.parameter_ = CodeGeneratorRequest.getDefaultInstance().getParameter();
                this.onChanged();
                return this;
            }

            public Builder setParameterBytes(ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 2;
                this.parameter_ = value;
                this.onChanged();
                return this;
            }

            private void ensureProtoFileIsMutable() {
                if ((this.bitField0_ & 4) == 0) {
                    this.protoFile_ = new ArrayList<DescriptorProtos.FileDescriptorProto>(this.protoFile_);
                    this.bitField0_ |= 4;
                }
            }

            @Override
            public List<DescriptorProtos.FileDescriptorProto> getProtoFileList() {
                if (this.protoFileBuilder_ == null) {
                    return Collections.unmodifiableList(this.protoFile_);
                }
                return this.protoFileBuilder_.getMessageList();
            }

            @Override
            public int getProtoFileCount() {
                if (this.protoFileBuilder_ == null) {
                    return this.protoFile_.size();
                }
                return this.protoFileBuilder_.getCount();
            }

            @Override
            public DescriptorProtos.FileDescriptorProto getProtoFile(int index) {
                if (this.protoFileBuilder_ == null) {
                    return this.protoFile_.get(index);
                }
                return this.protoFileBuilder_.getMessage(index);
            }

            public Builder setProtoFile(int index, DescriptorProtos.FileDescriptorProto value) {
                if (this.protoFileBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureProtoFileIsMutable();
                    this.protoFile_.set(index, value);
                    this.onChanged();
                } else {
                    this.protoFileBuilder_.setMessage(index, value);
                }
                return this;
            }

            public Builder setProtoFile(int index, DescriptorProtos.FileDescriptorProto.Builder builderForValue) {
                if (this.protoFileBuilder_ == null) {
                    this.ensureProtoFileIsMutable();
                    this.protoFile_.set(index, builderForValue.build());
                    this.onChanged();
                } else {
                    this.protoFileBuilder_.setMessage(index, builderForValue.build());
                }
                return this;
            }

            public Builder addProtoFile(DescriptorProtos.FileDescriptorProto value) {
                if (this.protoFileBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureProtoFileIsMutable();
                    this.protoFile_.add(value);
                    this.onChanged();
                } else {
                    this.protoFileBuilder_.addMessage(value);
                }
                return this;
            }

            public Builder addProtoFile(int index, DescriptorProtos.FileDescriptorProto value) {
                if (this.protoFileBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureProtoFileIsMutable();
                    this.protoFile_.add(index, value);
                    this.onChanged();
                } else {
                    this.protoFileBuilder_.addMessage(index, value);
                }
                return this;
            }

            public Builder addProtoFile(DescriptorProtos.FileDescriptorProto.Builder builderForValue) {
                if (this.protoFileBuilder_ == null) {
                    this.ensureProtoFileIsMutable();
                    this.protoFile_.add(builderForValue.build());
                    this.onChanged();
                } else {
                    this.protoFileBuilder_.addMessage(builderForValue.build());
                }
                return this;
            }

            public Builder addProtoFile(int index, DescriptorProtos.FileDescriptorProto.Builder builderForValue) {
                if (this.protoFileBuilder_ == null) {
                    this.ensureProtoFileIsMutable();
                    this.protoFile_.add(index, builderForValue.build());
                    this.onChanged();
                } else {
                    this.protoFileBuilder_.addMessage(index, builderForValue.build());
                }
                return this;
            }

            public Builder addAllProtoFile(Iterable<? extends DescriptorProtos.FileDescriptorProto> values) {
                if (this.protoFileBuilder_ == null) {
                    this.ensureProtoFileIsMutable();
                    AbstractMessageLite.Builder.addAll(values, this.protoFile_);
                    this.onChanged();
                } else {
                    this.protoFileBuilder_.addAllMessages(values);
                }
                return this;
            }

            public Builder clearProtoFile() {
                if (this.protoFileBuilder_ == null) {
                    this.protoFile_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFFB;
                    this.onChanged();
                } else {
                    this.protoFileBuilder_.clear();
                }
                return this;
            }

            public Builder removeProtoFile(int index) {
                if (this.protoFileBuilder_ == null) {
                    this.ensureProtoFileIsMutable();
                    this.protoFile_.remove(index);
                    this.onChanged();
                } else {
                    this.protoFileBuilder_.remove(index);
                }
                return this;
            }

            public DescriptorProtos.FileDescriptorProto.Builder getProtoFileBuilder(int index) {
                return this.getProtoFileFieldBuilder().getBuilder(index);
            }

            @Override
            public DescriptorProtos.FileDescriptorProtoOrBuilder getProtoFileOrBuilder(int index) {
                if (this.protoFileBuilder_ == null) {
                    return this.protoFile_.get(index);
                }
                return this.protoFileBuilder_.getMessageOrBuilder(index);
            }

            @Override
            public List<? extends DescriptorProtos.FileDescriptorProtoOrBuilder> getProtoFileOrBuilderList() {
                if (this.protoFileBuilder_ != null) {
                    return this.protoFileBuilder_.getMessageOrBuilderList();
                }
                return Collections.unmodifiableList(this.protoFile_);
            }

            public DescriptorProtos.FileDescriptorProto.Builder addProtoFileBuilder() {
                return this.getProtoFileFieldBuilder().addBuilder(DescriptorProtos.FileDescriptorProto.getDefaultInstance());
            }

            public DescriptorProtos.FileDescriptorProto.Builder addProtoFileBuilder(int index) {
                return this.getProtoFileFieldBuilder().addBuilder(index, DescriptorProtos.FileDescriptorProto.getDefaultInstance());
            }

            public List<DescriptorProtos.FileDescriptorProto.Builder> getProtoFileBuilderList() {
                return this.getProtoFileFieldBuilder().getBuilderList();
            }

            private RepeatedFieldBuilderV3<DescriptorProtos.FileDescriptorProto, DescriptorProtos.FileDescriptorProto.Builder, DescriptorProtos.FileDescriptorProtoOrBuilder> getProtoFileFieldBuilder() {
                if (this.protoFileBuilder_ == null) {
                    this.protoFileBuilder_ = new RepeatedFieldBuilderV3(this.protoFile_, (this.bitField0_ & 4) != 0, this.getParentForChildren(), this.isClean());
                    this.protoFile_ = null;
                }
                return this.protoFileBuilder_;
            }

            @Override
            public boolean hasCompilerVersion() {
                return (this.bitField0_ & 8) != 0;
            }

            @Override
            public Version getCompilerVersion() {
                if (this.compilerVersionBuilder_ == null) {
                    return this.compilerVersion_ == null ? Version.getDefaultInstance() : this.compilerVersion_;
                }
                return this.compilerVersionBuilder_.getMessage();
            }

            public Builder setCompilerVersion(Version value) {
                if (this.compilerVersionBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.compilerVersion_ = value;
                    this.onChanged();
                } else {
                    this.compilerVersionBuilder_.setMessage(value);
                }
                this.bitField0_ |= 8;
                return this;
            }

            public Builder setCompilerVersion(Version.Builder builderForValue) {
                if (this.compilerVersionBuilder_ == null) {
                    this.compilerVersion_ = builderForValue.build();
                    this.onChanged();
                } else {
                    this.compilerVersionBuilder_.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 8;
                return this;
            }

            public Builder mergeCompilerVersion(Version value) {
                if (this.compilerVersionBuilder_ == null) {
                    this.compilerVersion_ = (this.bitField0_ & 8) != 0 && this.compilerVersion_ != null && this.compilerVersion_ != Version.getDefaultInstance() ? Version.newBuilder(this.compilerVersion_).mergeFrom(value).buildPartial() : value;
                    this.onChanged();
                } else {
                    this.compilerVersionBuilder_.mergeFrom(value);
                }
                this.bitField0_ |= 8;
                return this;
            }

            public Builder clearCompilerVersion() {
                if (this.compilerVersionBuilder_ == null) {
                    this.compilerVersion_ = null;
                    this.onChanged();
                } else {
                    this.compilerVersionBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFF7;
                return this;
            }

            public Version.Builder getCompilerVersionBuilder() {
                this.bitField0_ |= 8;
                this.onChanged();
                return this.getCompilerVersionFieldBuilder().getBuilder();
            }

            @Override
            public VersionOrBuilder getCompilerVersionOrBuilder() {
                if (this.compilerVersionBuilder_ != null) {
                    return this.compilerVersionBuilder_.getMessageOrBuilder();
                }
                return this.compilerVersion_ == null ? Version.getDefaultInstance() : this.compilerVersion_;
            }

            private SingleFieldBuilderV3<Version, Version.Builder, VersionOrBuilder> getCompilerVersionFieldBuilder() {
                if (this.compilerVersionBuilder_ == null) {
                    this.compilerVersionBuilder_ = new SingleFieldBuilderV3(this.getCompilerVersion(), this.getParentForChildren(), this.isClean());
                    this.compilerVersion_ = null;
                }
                return this.compilerVersionBuilder_;
            }

            @Override
            public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
                return (Builder)super.setUnknownFields(unknownFields);
            }

            @Override
            public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
                return (Builder)super.mergeUnknownFields(unknownFields);
            }
        }
    }

    public static interface CodeGeneratorRequestOrBuilder
    extends MessageOrBuilder {
        public List<String> getFileToGenerateList();

        public int getFileToGenerateCount();

        public String getFileToGenerate(int var1);

        public ByteString getFileToGenerateBytes(int var1);

        public boolean hasParameter();

        public String getParameter();

        public ByteString getParameterBytes();

        public List<DescriptorProtos.FileDescriptorProto> getProtoFileList();

        public DescriptorProtos.FileDescriptorProto getProtoFile(int var1);

        public int getProtoFileCount();

        public List<? extends DescriptorProtos.FileDescriptorProtoOrBuilder> getProtoFileOrBuilderList();

        public DescriptorProtos.FileDescriptorProtoOrBuilder getProtoFileOrBuilder(int var1);

        public boolean hasCompilerVersion();

        public Version getCompilerVersion();

        public VersionOrBuilder getCompilerVersionOrBuilder();
    }

    public static final class Version
    extends GeneratedMessageV3
    implements VersionOrBuilder {
        private static final long serialVersionUID = 0L;
        private int bitField0_;
        public static final int MAJOR_FIELD_NUMBER = 1;
        private int major_;
        public static final int MINOR_FIELD_NUMBER = 2;
        private int minor_;
        public static final int PATCH_FIELD_NUMBER = 3;
        private int patch_;
        public static final int SUFFIX_FIELD_NUMBER = 4;
        private volatile Object suffix_;
        private byte memoizedIsInitialized = (byte)-1;
        private static final Version DEFAULT_INSTANCE = new Version();
        @Deprecated
        public static final Parser<Version> PARSER = new AbstractParser<Version>(){

            @Override
            public Version parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return new Version(input, extensionRegistry);
            }
        };

        private Version(GeneratedMessageV3.Builder<?> builder) {
            super(builder);
        }

        private Version() {
            this.suffix_ = "";
        }

        @Override
        protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
            return new Version();
        }

        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }

        private Version(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            this();
            if (extensionRegistry == null) {
                throw new NullPointerException();
            }
            boolean mutable_bitField0_ = false;
            UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();
            try {
                boolean done = false;
                block13: while (!done) {
                    int tag = input.readTag();
                    switch (tag) {
                        case 0: {
                            done = true;
                            continue block13;
                        }
                        case 8: {
                            this.bitField0_ |= 1;
                            this.major_ = input.readInt32();
                            continue block13;
                        }
                        case 16: {
                            this.bitField0_ |= 2;
                            this.minor_ = input.readInt32();
                            continue block13;
                        }
                        case 24: {
                            this.bitField0_ |= 4;
                            this.patch_ = input.readInt32();
                            continue block13;
                        }
                        case 34: {
                            ByteString bs = input.readBytes();
                            this.bitField0_ |= 8;
                            this.suffix_ = bs;
                            continue block13;
                        }
                    }
                    if (this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) continue;
                    done = true;
                }
            }
            catch (InvalidProtocolBufferException e) {
                throw e.setUnfinishedMessage(this);
            }
            catch (IOException e) {
                throw new InvalidProtocolBufferException(e).setUnfinishedMessage(this);
            }
            finally {
                this.unknownFields = unknownFields.build();
                this.makeExtensionsImmutable();
            }
        }

        public static final Descriptors.Descriptor getDescriptor() {
            return internal_static_google_protobuf_compiler_Version_descriptor;
        }

        @Override
        protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return internal_static_google_protobuf_compiler_Version_fieldAccessorTable.ensureFieldAccessorsInitialized(Version.class, Builder.class);
        }

        @Override
        public boolean hasMajor() {
            return (this.bitField0_ & 1) != 0;
        }

        @Override
        public int getMajor() {
            return this.major_;
        }

        @Override
        public boolean hasMinor() {
            return (this.bitField0_ & 2) != 0;
        }

        @Override
        public int getMinor() {
            return this.minor_;
        }

        @Override
        public boolean hasPatch() {
            return (this.bitField0_ & 4) != 0;
        }

        @Override
        public int getPatch() {
            return this.patch_;
        }

        @Override
        public boolean hasSuffix() {
            return (this.bitField0_ & 8) != 0;
        }

        @Override
        public String getSuffix() {
            Object ref = this.suffix_;
            if (ref instanceof String) {
                return (String)ref;
            }
            ByteString bs = (ByteString)ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.suffix_ = s;
            }
            return s;
        }

        @Override
        public ByteString getSuffixBytes() {
            Object ref = this.suffix_;
            if (ref instanceof String) {
                ByteString b = ByteString.copyFromUtf8((String)ref);
                this.suffix_ = b;
                return b;
            }
            return (ByteString)ref;
        }

        @Override
        public final boolean isInitialized() {
            byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized == 1) {
                return true;
            }
            if (isInitialized == 0) {
                return false;
            }
            this.memoizedIsInitialized = 1;
            return true;
        }

        @Override
        public void writeTo(CodedOutputStream output) throws IOException {
            if ((this.bitField0_ & 1) != 0) {
                output.writeInt32(1, this.major_);
            }
            if ((this.bitField0_ & 2) != 0) {
                output.writeInt32(2, this.minor_);
            }
            if ((this.bitField0_ & 4) != 0) {
                output.writeInt32(3, this.patch_);
            }
            if ((this.bitField0_ & 8) != 0) {
                GeneratedMessageV3.writeString(output, 4, this.suffix_);
            }
            this.unknownFields.writeTo(output);
        }

        @Override
        public int getSerializedSize() {
            int size = this.memoizedSize;
            if (size != -1) {
                return size;
            }
            size = 0;
            if ((this.bitField0_ & 1) != 0) {
                size += CodedOutputStream.computeInt32Size(1, this.major_);
            }
            if ((this.bitField0_ & 2) != 0) {
                size += CodedOutputStream.computeInt32Size(2, this.minor_);
            }
            if ((this.bitField0_ & 4) != 0) {
                size += CodedOutputStream.computeInt32Size(3, this.patch_);
            }
            if ((this.bitField0_ & 8) != 0) {
                size += GeneratedMessageV3.computeStringSize(4, this.suffix_);
            }
            this.memoizedSize = size += this.unknownFields.getSerializedSize();
            return size;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof Version)) {
                return super.equals(obj);
            }
            Version other = (Version)obj;
            if (this.hasMajor() != other.hasMajor()) {
                return false;
            }
            if (this.hasMajor() && this.getMajor() != other.getMajor()) {
                return false;
            }
            if (this.hasMinor() != other.hasMinor()) {
                return false;
            }
            if (this.hasMinor() && this.getMinor() != other.getMinor()) {
                return false;
            }
            if (this.hasPatch() != other.hasPatch()) {
                return false;
            }
            if (this.hasPatch() && this.getPatch() != other.getPatch()) {
                return false;
            }
            if (this.hasSuffix() != other.hasSuffix()) {
                return false;
            }
            if (this.hasSuffix() && !this.getSuffix().equals(other.getSuffix())) {
                return false;
            }
            return this.unknownFields.equals(other.unknownFields);
        }

        @Override
        public int hashCode() {
            if (this.memoizedHashCode != 0) {
                return this.memoizedHashCode;
            }
            int hash = 41;
            hash = 19 * hash + Version.getDescriptor().hashCode();
            if (this.hasMajor()) {
                hash = 37 * hash + 1;
                hash = 53 * hash + this.getMajor();
            }
            if (this.hasMinor()) {
                hash = 37 * hash + 2;
                hash = 53 * hash + this.getMinor();
            }
            if (this.hasPatch()) {
                hash = 37 * hash + 3;
                hash = 53 * hash + this.getPatch();
            }
            if (this.hasSuffix()) {
                hash = 37 * hash + 4;
                hash = 53 * hash + this.getSuffix().hashCode();
            }
            this.memoizedHashCode = hash = 29 * hash + this.unknownFields.hashCode();
            return hash;
        }

        public static Version parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static Version parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static Version parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static Version parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static Version parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static Version parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static Version parseFrom(InputStream input) throws IOException {
            return GeneratedMessageV3.parseWithIOException(PARSER, input);
        }

        public static Version parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
        }

        public static Version parseDelimitedFrom(InputStream input) throws IOException {
            return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
        }

        public static Version parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
        }

        public static Version parseFrom(CodedInputStream input) throws IOException {
            return GeneratedMessageV3.parseWithIOException(PARSER, input);
        }

        public static Version parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
        }

        @Override
        public Builder newBuilderForType() {
            return Version.newBuilder();
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(Version prototype) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
        }

        @Override
        public Builder toBuilder() {
            return this == DEFAULT_INSTANCE ? new Builder() : new Builder().mergeFrom(this);
        }

        @Override
        protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
            Builder builder = new Builder(parent);
            return builder;
        }

        public static Version getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<Version> parser() {
            return PARSER;
        }

        public Parser<Version> getParserForType() {
            return PARSER;
        }

        @Override
        public Version getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }

        public static final class Builder
        extends GeneratedMessageV3.Builder<Builder>
        implements VersionOrBuilder {
            private int bitField0_;
            private int major_;
            private int minor_;
            private int patch_;
            private Object suffix_ = "";

            public static final Descriptors.Descriptor getDescriptor() {
                return internal_static_google_protobuf_compiler_Version_descriptor;
            }

            @Override
            protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
                return internal_static_google_protobuf_compiler_Version_fieldAccessorTable.ensureFieldAccessorsInitialized(Version.class, Builder.class);
            }

            private Builder() {
                this.maybeForceBuilderInitialization();
            }

            private Builder(GeneratedMessageV3.BuilderParent parent) {
                super(parent);
                this.maybeForceBuilderInitialization();
            }

            private void maybeForceBuilderInitialization() {
                if (alwaysUseFieldBuilders) {
                    // empty if block
                }
            }

            @Override
            public Builder clear() {
                super.clear();
                this.major_ = 0;
                this.bitField0_ &= 0xFFFFFFFE;
                this.minor_ = 0;
                this.bitField0_ &= 0xFFFFFFFD;
                this.patch_ = 0;
                this.bitField0_ &= 0xFFFFFFFB;
                this.suffix_ = "";
                this.bitField0_ &= 0xFFFFFFF7;
                return this;
            }

            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return internal_static_google_protobuf_compiler_Version_descriptor;
            }

            @Override
            public Version getDefaultInstanceForType() {
                return Version.getDefaultInstance();
            }

            @Override
            public Version build() {
                Version result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw Builder.newUninitializedMessageException(result);
                }
                return result;
            }

            @Override
            public Version buildPartial() {
                Version result = new Version(this);
                int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 1) != 0) {
                    result.major_ = this.major_;
                    to_bitField0_ |= 1;
                }
                if ((from_bitField0_ & 2) != 0) {
                    result.minor_ = this.minor_;
                    to_bitField0_ |= 2;
                }
                if ((from_bitField0_ & 4) != 0) {
                    result.patch_ = this.patch_;
                    to_bitField0_ |= 4;
                }
                if ((from_bitField0_ & 8) != 0) {
                    to_bitField0_ |= 8;
                }
                result.suffix_ = this.suffix_;
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }

            @Override
            public Builder clone() {
                return (Builder)super.clone();
            }

            @Override
            public Builder setField(Descriptors.FieldDescriptor field, Object value) {
                return (Builder)super.setField(field, value);
            }

            @Override
            public Builder clearField(Descriptors.FieldDescriptor field) {
                return (Builder)super.clearField(field);
            }

            @Override
            public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
                return (Builder)super.clearOneof(oneof);
            }

            @Override
            public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
                return (Builder)super.setRepeatedField(field, index, value);
            }

            @Override
            public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
                return (Builder)super.addRepeatedField(field, value);
            }

            @Override
            public Builder mergeFrom(Message other) {
                if (other instanceof Version) {
                    return this.mergeFrom((Version)other);
                }
                super.mergeFrom(other);
                return this;
            }

            public Builder mergeFrom(Version other) {
                if (other == Version.getDefaultInstance()) {
                    return this;
                }
                if (other.hasMajor()) {
                    this.setMajor(other.getMajor());
                }
                if (other.hasMinor()) {
                    this.setMinor(other.getMinor());
                }
                if (other.hasPatch()) {
                    this.setPatch(other.getPatch());
                }
                if (other.hasSuffix()) {
                    this.bitField0_ |= 8;
                    this.suffix_ = other.suffix_;
                    this.onChanged();
                }
                this.mergeUnknownFields(other.unknownFields);
                this.onChanged();
                return this;
            }

            @Override
            public final boolean isInitialized() {
                return true;
            }

            @Override
            public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                Version parsedMessage = null;
                try {
                    parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (Version)e.getUnfinishedMessage();
                    throw e.unwrapIOException();
                }
                finally {
                    if (parsedMessage != null) {
                        this.mergeFrom(parsedMessage);
                    }
                }
                return this;
            }

            @Override
            public boolean hasMajor() {
                return (this.bitField0_ & 1) != 0;
            }

            @Override
            public int getMajor() {
                return this.major_;
            }

            public Builder setMajor(int value) {
                this.bitField0_ |= 1;
                this.major_ = value;
                this.onChanged();
                return this;
            }

            public Builder clearMajor() {
                this.bitField0_ &= 0xFFFFFFFE;
                this.major_ = 0;
                this.onChanged();
                return this;
            }

            @Override
            public boolean hasMinor() {
                return (this.bitField0_ & 2) != 0;
            }

            @Override
            public int getMinor() {
                return this.minor_;
            }

            public Builder setMinor(int value) {
                this.bitField0_ |= 2;
                this.minor_ = value;
                this.onChanged();
                return this;
            }

            public Builder clearMinor() {
                this.bitField0_ &= 0xFFFFFFFD;
                this.minor_ = 0;
                this.onChanged();
                return this;
            }

            @Override
            public boolean hasPatch() {
                return (this.bitField0_ & 4) != 0;
            }

            @Override
            public int getPatch() {
                return this.patch_;
            }

            public Builder setPatch(int value) {
                this.bitField0_ |= 4;
                this.patch_ = value;
                this.onChanged();
                return this;
            }

            public Builder clearPatch() {
                this.bitField0_ &= 0xFFFFFFFB;
                this.patch_ = 0;
                this.onChanged();
                return this;
            }

            @Override
            public boolean hasSuffix() {
                return (this.bitField0_ & 8) != 0;
            }

            @Override
            public String getSuffix() {
                Object ref = this.suffix_;
                if (!(ref instanceof String)) {
                    ByteString bs = (ByteString)ref;
                    String s = bs.toStringUtf8();
                    if (bs.isValidUtf8()) {
                        this.suffix_ = s;
                    }
                    return s;
                }
                return (String)ref;
            }

            @Override
            public ByteString getSuffixBytes() {
                Object ref = this.suffix_;
                if (ref instanceof String) {
                    ByteString b = ByteString.copyFromUtf8((String)ref);
                    this.suffix_ = b;
                    return b;
                }
                return (ByteString)ref;
            }

            public Builder setSuffix(String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 8;
                this.suffix_ = value;
                this.onChanged();
                return this;
            }

            public Builder clearSuffix() {
                this.bitField0_ &= 0xFFFFFFF7;
                this.suffix_ = Version.getDefaultInstance().getSuffix();
                this.onChanged();
                return this;
            }

            public Builder setSuffixBytes(ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 8;
                this.suffix_ = value;
                this.onChanged();
                return this;
            }

            @Override
            public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
                return (Builder)super.setUnknownFields(unknownFields);
            }

            @Override
            public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
                return (Builder)super.mergeUnknownFields(unknownFields);
            }
        }
    }

    public static interface VersionOrBuilder
    extends MessageOrBuilder {
        public boolean hasMajor();

        public int getMajor();

        public boolean hasMinor();

        public int getMinor();

        public boolean hasPatch();

        public int getPatch();

        public boolean hasSuffix();

        public String getSuffix();

        public ByteString getSuffixBytes();
    }
}

