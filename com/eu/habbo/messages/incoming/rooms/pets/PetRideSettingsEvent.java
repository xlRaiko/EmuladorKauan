/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.incoming.rooms.pets;

import com.eu.habbo.habbohotel.pets.HorsePet;
import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.pets.RideablePet;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.pets.RoomPetHorseFigureComposer;

public class PetRideSettingsEvent
extends MessageHandler {
    @Override
    public void handle() throws Exception {
        RideablePet rideablePet;
        int petId = this.packet.readInt();
        if (this.client.getHabbo().getHabboInfo().getCurrentRoom() == null) {
            return;
        }
        Pet pet = this.client.getHabbo().getHabboInfo().getCurrentRoom().getPet(petId);
        if (pet == null || pet.getUserId() != this.client.getHabbo().getHabboInfo().getId() || !(pet instanceof RideablePet)) {
            return;
        }
        rideablePet.setAnyoneCanRide(!(rideablePet = (RideablePet)pet).anyoneCanRide());
        rideablePet.needsUpdate = true;
        if (!rideablePet.anyoneCanRide() && rideablePet.getRider() != null && rideablePet.getRider().getHabboInfo().getId() != this.client.getHabbo().getHabboInfo().getId()) {
            rideablePet.getRider().getHabboInfo().dismountPet();
        }
        if (pet instanceof HorsePet) {
            this.client.sendResponse(new RoomPetHorseFigureComposer((HorsePet)pet));
        }
    }
}

