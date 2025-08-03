/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.guilds;

import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class GuildRefreshMembersListComposer
extends MessageComposer {
    private final Guild guild;

    public GuildRefreshMembersListComposer(Guild guild) {
        this.guild = guild;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(2445);
        this.response.appendInt(this.guild.getId());
        this.response.appendInt(0);
        return this.response;
    }

    public Guild getGuild() {
        return this.guild;
    }
}

