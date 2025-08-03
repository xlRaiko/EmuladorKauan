/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.items.interactions.wired.conditions;

import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.wired.conditions.WiredConditionMatchStatePosition;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.WiredConditionType;
import com.eu.habbo.habbohotel.wired.WiredMatchFurniSetting;
import java.sql.ResultSet;
import java.sql.SQLException;

public class WiredConditionMatchNew
extends WiredConditionMatchStatePosition {
    public static final WiredConditionType type = WiredConditionType.NOT_MATCH_SSHOT;

    public WiredConditionMatchNew(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredConditionMatchNew(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        if (this.getSettings().isEmpty()) {
            return true;
        }
        for (WiredMatchFurniSetting setting2 : this.getSettings()) {
            HabboItem item = room.getHabboItem(setting2.item_id);
            if (item != null) continue;
            this.getSettings().remove(setting2);
        }
        return this.getSettings().stream().allMatch(setting -> {
            HabboItem item = room.getHabboItem(setting.item_id);
            if (item != null) {
                if (this.shouldMatchState() && !item.getExtradata().equals(setting.state)) {
                    return false;
                }
                if (this.shouldMatchRotation() && (setting.x != item.getX() || setting.y != item.getY())) {
                    return false;
                }
                return !this.shouldMatchPosition() || setting.rotation == item.getRotation();
            }
            return false;
        });
    }

    @Override
    public WiredConditionType getType() {
        return type;
    }
}

