/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.Descriptors;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedMessageV3;

public final class FieldMaskProto {
    static final Descriptors.Descriptor internal_static_google_protobuf_FieldMask_descriptor;
    static final GeneratedMessageV3.FieldAccessorTable internal_static_google_protobuf_FieldMask_fieldAccessorTable;
    private static Descriptors.FileDescriptor descriptor;

    private FieldMaskProto() {
    }

    public static void registerAllExtensions(ExtensionRegistryLite registry) {
    }

    public static void registerAllExtensions(ExtensionRegistry registry) {
        FieldMaskProto.registerAllExtensions((ExtensionRegistryLite)registry);
    }

    public static Descriptors.FileDescriptor getDescriptor() {
        return descriptor;
    }

    static {
        String[] descriptorData = new String[]{"\n google/protobuf/field_mask.proto\u0012\u000fgoogle.protobuf\"\u001a\n\tFieldMask\u0012\r\n\u0005paths\u0018\u0001 \u0003(\tB\u008c\u0001\n\u0013com.google.protobufB\u000eFieldMaskProtoP\u0001Z9google.golang.org/genproto/protobuf/field_mask;field_mask\u00f8\u0001\u0001\u00a2\u0002\u0003GPB\u00aa\u0002\u001eGoogle.Protobuf.WellKnownTypesb\u0006proto3"};
        descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[0]);
        internal_static_google_protobuf_FieldMask_descriptor = FieldMaskProto.getDescriptor().getMessageTypes().get(0);
        internal_static_google_protobuf_FieldMask_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_google_protobuf_FieldMask_descriptor, new String[]{"Paths"});
    }
}

