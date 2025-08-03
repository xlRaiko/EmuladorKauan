/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.incoming.catalog;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.catalog.ClubOffer;
import com.eu.habbo.habbohotel.users.subscriptions.Subscription;
import com.eu.habbo.habbohotel.users.subscriptions.SubscriptionHabboClub;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.unknown.ExtendClubMessageComposer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CatalogRequestClubDiscountEvent
extends MessageHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(CatalogRequestClubDiscountEvent.class);

    @Override
    public void handle() throws Exception {
        ClubOffer regular;
        ClubOffer deal;
        Subscription subscription = this.client.getHabbo().getHabboStats().getSubscription("HABBO_CLUB");
        int days = 0;
        int minutes = 0;
        int timeRemaining = 0;
        if (subscription != null) {
            timeRemaining = subscription.getRemaining();
            days = (int)Math.floor((double)timeRemaining / 86400.0);
            minutes = (int)Math.ceil((double)timeRemaining / 60.0);
            if (days < 1 && minutes > 0) {
                days = 1;
            }
        }
        if (timeRemaining > 0 && SubscriptionHabboClub.DISCOUNT_ENABLED && days <= SubscriptionHabboClub.DISCOUNT_DAYS_BEFORE_END && (deal = (ClubOffer)Emulator.getGameEnvironment().getCatalogManager().clubOffers.values().stream().filter(ClubOffer::isDeal).findAny().orElse(null)) != null && (regular = (ClubOffer)Emulator.getGameEnvironment().getCatalogManager().getClubOffers().stream().filter(x -> x.getDays() == deal.getDays()).findAny().orElse(null)) != null) {
            this.client.sendResponse(new ExtendClubMessageComposer(this.client.getHabbo(), deal, regular.getCredits(), regular.getPoints(), regular.getPointsType(), Math.max(0, days)));
        }
    }
}

