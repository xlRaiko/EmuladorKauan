/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.rcon;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;

public abstract class RCONMessage<T> {
    public static final int STATUS_OK = 0;
    public static final int STATUS_ERROR = 1;
    public static final int HABBO_NOT_FOUND = 2;
    public static final int ROOM_NOT_FOUND = 3;
    public static final int SYSTEM_ERROR = 4;
    public final Class<T> type;
    public int status = 0;
    public String message = "";

    public RCONMessage(Class<T> type) {
        this.type = type;
    }

    public abstract void handle(Gson var1, T var2);

    public static class RCONMessageSerializer
    implements JsonSerializer<RCONMessage> {
        @Override
        public JsonElement serialize(RCONMessage rconMessage, Type type, JsonSerializationContext context) {
            JsonObject result = new JsonObject();
            result.add("status", new JsonPrimitive(rconMessage.status));
            result.add("message", new JsonPrimitive(rconMessage.message));
            return result;
        }
    }
}

