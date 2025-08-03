/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.hotelview;

import com.eu.habbo.habbohotel.hotelview.HallOfFame;
import com.eu.habbo.habbohotel.hotelview.HallOfFameWinner;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import java.util.ArrayList;
import java.util.Collections;

public class HallOfFameComposer
extends MessageComposer {
    private final HallOfFame hallOfFame;

    public HallOfFameComposer(HallOfFame hallOfFame) {
        this.hallOfFame = hallOfFame;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(3005);
        this.response.appendString(this.hallOfFame.getCompetitionName());
        this.response.appendInt(this.hallOfFame.getWinners().size());
        int count = 1;
        ArrayList<HallOfFameWinner> winners = new ArrayList<HallOfFameWinner>(this.hallOfFame.getWinners().values());
        Collections.sort(winners);
        for (HallOfFameWinner winner : winners) {
            this.response.appendInt(winner.getId());
            this.response.appendString(winner.getUsername());
            this.response.appendString(winner.getLook());
            this.response.appendInt(count);
            this.response.appendInt(winner.getPoints());
            ++count;
        }
        return this.response;
    }

    public HallOfFame getHallOfFame() {
        return this.hallOfFame;
    }
}

