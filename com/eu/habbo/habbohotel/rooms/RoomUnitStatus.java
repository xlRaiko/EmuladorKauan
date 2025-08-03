/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.rooms;

public enum RoomUnitStatus {
    MOVE("mv", true),
    SIT_IN("sit-in"),
    SIT("sit", true),
    SIT_OUT("sit-out"),
    LAY_IN("lay-in"),
    LAY("lay", true),
    LAY_OUT("lay-out"),
    EAT_IN("eat-in"),
    EAT("eat"),
    EAT_OUT("eat-out"),
    FLAT_CONTROL("flatctrl"),
    SIGN("sign"),
    GESTURE("gst"),
    WAVE("wav"),
    TRADING("trd"),
    KICK("kck"),
    SPEAK("spk"),
    SRP("srp"),
    SRP_IN("srp-in"),
    SWIM("swm"),
    SLEEP_IN("slp-in"),
    SLEEP("slp", true),
    SLEEP_OUT("slp-out"),
    DEAD_IN("ded-in"),
    DEAD("ded", true),
    DEAD_OUT("ded-out"),
    JUMP_IN("jmp-in"),
    JUMP("jmp", true),
    JUMP_OUT("jmp-out"),
    PLAY_IN("pla-in"),
    PLAY("pla", true),
    PLAY_OUT("pla-out"),
    DIP("dip"),
    BEG("beg", true),
    WAG_TAIL("wag"),
    DANCE("dan"),
    AMS("ams"),
    TURN("trn"),
    SPIN("spn"),
    CROAK("crk"),
    FLAT("flt"),
    FLAT_IN("flt-in"),
    BOUNCE("bnc"),
    RELAX("rlx"),
    WINGS("wng", true),
    FLAME("flm"),
    RINGOFFIRE("rng"),
    SWING("swg"),
    HANG("hg"),
    ROLL("rll"),
    RIP("rip"),
    GROW("grw"),
    GROW_1("grw1"),
    GROW_2("grw2"),
    GROW_3("grw3"),
    GROW_4("grw4"),
    GROW_5("grw5"),
    GROW_6("grw6"),
    GROW_7("grw7");

    public final String key;
    public final boolean removeWhenWalking;

    private RoomUnitStatus(String key) {
        this.key = key;
        this.removeWhenWalking = false;
    }

    private RoomUnitStatus(String key, boolean removeWhenWalking) {
        this.key = key;
        this.removeWhenWalking = removeWhenWalking;
    }

    public static RoomUnitStatus fromString(String key) {
        for (RoomUnitStatus status : RoomUnitStatus.values()) {
            if (!status.key.equalsIgnoreCase(key)) continue;
            return status;
        }
        return null;
    }

    public String toString() {
        return this.key;
    }
}

