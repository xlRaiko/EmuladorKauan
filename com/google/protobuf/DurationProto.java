/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.Descriptors;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedMessageV3;

public final class DurationProto {
    static final Descriptors.Descriptor internal_static_google_protobuf_Duration_descriptor;
    static final GeneratedMessageV3.FieldAccessorTable internal_static_google_protobuf_Duration_fieldAccessorTable;
    private static Descriptors.FileDescriptor descriptor;

    private DurationProto() {
    }

    public static void registerAllExtensions(ExtensionRegistryLite registry) {
    }

    public static void registerAllExtensions(ExtensionRegistry registry) {
        DurationProto.registerAllExtensions((ExtensionRegistryLite)registry);
    }

    public static Descriptors.FileDescriptor getDescriptor() {
        return descriptor;
    }

    static {
        String[] descriptorData = new String[]{"\n\u001egoogle/protobuf/duration.proto\u0012\u000fgoogle.protobuf\"*\n\bDuration\u0012\u000f\n\u0007seconds\u0018\u0001 \u0001(\u0003\u0012\r\n\u0005nanos\u0018\u0002 \u0001(\u0005B|\n\u0013com.google.protobufB\rDurationProtoP\u0001Z*github.com/golang/protobuf/ptypes/duration\u00f8\u0001\u0001\u00a2\u0002\u0003GPB\u00aa\u0002\u001eGoogle.Protobuf.WellKnownTypesb\u0006proto3"};
        descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[0]);
        internal_static_google_protobuf_Duration_descriptor = DurationProto.getDescriptor().getMessageTypes().get(0);
        internal_static_google_protobuf_Duration_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_google_protobuf_Duration_descriptor, new String[]{"Seconds", "Nanos"});
    }
}

