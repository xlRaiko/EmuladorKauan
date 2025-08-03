/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.users.Habbo;

public class FastwalkCommand
extends Command {
    public FastwalkCommand() {
        super("cmd_fastwalk", Emulator.getTexts().getValue("commands.keys.cmd_fastwalk").split(";"));
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception {
        if (gameClient.getHabbo().getHabboInfo().getCurrentRoom() != null) {
            if (gameClient.getHabbo().getHabboInfo().getCurrentRoom() != null && gameClient.getHabbo().getHabboInfo().getRiding() != null) {
                return true;
            }
            Habbo habbo = gameClient.getHabbo();
            if (params.length >= 2) {
                String username = params[1];
                habbo = gameClient.getHabbo().getHabboInfo().getCurrentRoom().getHabbo(username);
                if (habbo == null) {
                    return false;
                }
            }
            habbo.getRoomUnit().setFastWalk(!habbo.getRoomUnit().isFastWalk());
            return true;
        }
        return false;
    }
}

