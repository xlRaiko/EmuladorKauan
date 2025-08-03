/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.incoming.wired;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredCondition;
import com.eu.habbo.habbohotel.items.interactions.wired.WiredSettings;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.generic.alerts.UpdateFailedComposer;
import com.eu.habbo.messages.outgoing.wired.WiredSavedComposer;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

public class WiredConditionSaveDataEvent
extends MessageHandler {
    @Override
    public void handle() throws Exception {
        InteractionWiredCondition condition;
        int itemId = this.packet.readInt();
        Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();
        if (room != null && (room.hasRights(this.client.getHabbo()) || room.getOwnerId() == this.client.getHabbo().getHabboInfo().getId() || this.client.getHabbo().hasPermission(Permission.ACC_ANYROOMOWNER) || this.client.getHabbo().hasPermission(Permission.ACC_MOVEROTATE)) && (condition = room.getRoomSpecialTypes().getCondition(itemId)) != null) {
            Optional<Method> saveMethod = Arrays.stream(condition.getClass().getMethods()).filter(x -> x.getName().equals("saveData")).findFirst();
            if (saveMethod.isPresent()) {
                if (saveMethod.get().getParameterTypes()[0] == WiredSettings.class) {
                    WiredSettings settings = condition.readSettings(this.packet, false);
                    if (condition.saveData(settings)) {
                        this.client.sendResponse(new WiredSavedComposer());
                        condition.needsUpdate(true);
                        Emulator.getThreading().run(condition);
                    } else {
                        this.client.sendResponse(new UpdateFailedComposer("There was an error while saving that condition"));
                    }
                } else if (((Boolean)saveMethod.get().invoke((Object)condition, this.packet)).booleanValue()) {
                    this.client.sendResponse(new WiredSavedComposer());
                    condition.needsUpdate(true);
                    Emulator.getThreading().run(condition);
                } else {
                    this.client.sendResponse(new UpdateFailedComposer("There was an error while saving that condition"));
                }
            } else {
                this.client.sendResponse(new UpdateFailedComposer("Save method was not found"));
            }
        }
    }
}

