/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.plugin.events.users.catalog;

import com.eu.habbo.habbohotel.catalog.CatalogItem;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.plugin.events.users.catalog.UserCatalogEvent;
import gnu.trove.set.hash.THashSet;

public class UserCatalogFurnitureBoughtEvent
extends UserCatalogEvent {
    public final THashSet<HabboItem> furniture;

    public UserCatalogFurnitureBoughtEvent(Habbo habbo, CatalogItem catalogItem, THashSet<HabboItem> furniture) {
        super(habbo, catalogItem);
        this.furniture = furniture;
    }
}

