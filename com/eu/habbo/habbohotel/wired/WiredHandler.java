/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.wired;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.catalog.CatalogItem;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredCondition;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredExtra;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredTrigger;
import com.eu.habbo.habbohotel.items.interactions.wired.WiredTriggerReset;
import com.eu.habbo.habbohotel.items.interactions.wired.effects.WiredEffectGiveReward;
import com.eu.habbo.habbohotel.items.interactions.wired.effects.WiredEffectTriggerStacks;
import com.eu.habbo.habbohotel.items.interactions.wired.effects.WiredEffectTriggerStacksNegative;
import com.eu.habbo.habbohotel.items.interactions.wired.effects.WiredEffectTriggerStacksNegativeCondition;
import com.eu.habbo.habbohotel.items.interactions.wired.extra.WiredAddonOneCondition;
import com.eu.habbo.habbohotel.items.interactions.wired.extra.WiredExtraRandom;
import com.eu.habbo.habbohotel.items.interactions.wired.extra.WiredExtraUnseen;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboBadge;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.WiredConditionOperator;
import com.eu.habbo.habbohotel.wired.WiredConditionType;
import com.eu.habbo.habbohotel.wired.WiredGiveRewardItem;
import com.eu.habbo.habbohotel.wired.WiredMatchFurniSetting;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import com.eu.habbo.messages.outgoing.catalog.PurchaseOKComposer;
import com.eu.habbo.messages.outgoing.inventory.AddHabboItemComposer;
import com.eu.habbo.messages.outgoing.inventory.InventoryRefreshComposer;
import com.eu.habbo.messages.outgoing.users.AddUserBadgeComposer;
import com.eu.habbo.messages.outgoing.wired.WiredRewardAlertComposer;
import com.eu.habbo.plugin.events.furniture.wired.WiredConditionFailedEvent;
import com.eu.habbo.plugin.events.users.UserWiredRewardReceived;
import com.google.gson.GsonBuilder;
import gnu.trove.set.hash.THashSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WiredHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(WiredHandler.class);
    public static int MAXIMUM_FURNI_SELECTION = 5;
    public static int TELEPORT_DELAY = 500;
    private static GsonBuilder gsonBuilder = null;

    public static boolean handle(WiredTriggerType triggerType, RoomUnit roomUnit, Room room, Object[] stuff) {
        if (triggerType == WiredTriggerType.CUSTOM) {
            return false;
        }
        boolean talked = false;
        if (!Emulator.isReady) {
            return false;
        }
        if (room == null) {
            return false;
        }
        if (!room.isLoaded()) {
            return false;
        }
        if (room.getRoomSpecialTypes() == null) {
            return false;
        }
        THashSet<InteractionWiredTrigger> triggers = room.getRoomSpecialTypes().getTriggers(triggerType);
        if (triggers == null || triggers.isEmpty()) {
            return false;
        }
        long millis = System.currentTimeMillis();
        THashSet<InteractionWiredEffect> effectsToExecute = new THashSet<InteractionWiredEffect>();
        ArrayList<RoomTile> triggeredTiles = new ArrayList<RoomTile>();
        for (InteractionWiredTrigger trigger : triggers) {
            RoomTile tile = room.getLayout().getTile(trigger.getX(), trigger.getY());
            if (triggeredTiles.contains(tile)) continue;
            THashSet<InteractionWiredEffect> tEffectsToExecute = new THashSet<InteractionWiredEffect>();
            if (WiredHandler.handle(trigger, roomUnit, room, stuff, tEffectsToExecute)) {
                effectsToExecute.addAll(tEffectsToExecute);
                if (triggerType.equals((Object)WiredTriggerType.SAY_SOMETHING)) {
                    talked = true;
                }
                triggeredTiles.add(tile);
                continue;
            }
            THashSet interactionWiredEffects = room.getRoomSpecialTypes().getEffects(trigger.getX(), trigger.getY()).stream().filter(item -> item instanceof WiredEffectTriggerStacksNegative || item instanceof WiredEffectTriggerStacksNegativeCondition).collect(Collectors.toCollection(THashSet::new));
            if (interactionWiredEffects.isEmpty() || !trigger.execute(roomUnit, room, stuff)) continue;
            if (triggerType.equals((Object)WiredTriggerType.SAY_SOMETHING)) {
                talked = true;
            }
            effectsToExecute.addAll(interactionWiredEffects);
            triggeredTiles.add(tile);
        }
        for (InteractionWiredEffect effect : effectsToExecute) {
            WiredHandler.triggerEffect(effect, roomUnit, room, stuff, millis);
        }
        return talked;
    }

    public static boolean handleCustomTrigger(Class<? extends InteractionWiredTrigger> triggerType, RoomUnit roomUnit, Room room, Object[] stuff) {
        if (!Emulator.isReady) {
            return false;
        }
        if (room == null) {
            return false;
        }
        if (!room.isLoaded()) {
            return false;
        }
        if (room.getRoomSpecialTypes() == null) {
            return false;
        }
        THashSet<InteractionWiredTrigger> triggers = room.getRoomSpecialTypes().getTriggers(WiredTriggerType.CUSTOM);
        if (triggers == null || triggers.isEmpty()) {
            return false;
        }
        long millis = System.currentTimeMillis();
        THashSet<InteractionWiredEffect> effectsToExecute = new THashSet<InteractionWiredEffect>();
        ArrayList<RoomTile> triggeredTiles = new ArrayList<RoomTile>();
        for (InteractionWiredTrigger trigger : triggers) {
            THashSet<InteractionWiredEffect> tEffectsToExecute;
            RoomTile tile;
            if (trigger.getClass() != triggerType || triggeredTiles.contains(tile = room.getLayout().getTile(trigger.getX(), trigger.getY())) || !WiredHandler.handle(trigger, roomUnit, room, stuff, tEffectsToExecute = new THashSet<InteractionWiredEffect>())) continue;
            effectsToExecute.addAll(tEffectsToExecute);
            triggeredTiles.add(tile);
        }
        for (InteractionWiredEffect effect : effectsToExecute) {
            WiredHandler.triggerEffect(effect, roomUnit, room, stuff, millis);
        }
        return !effectsToExecute.isEmpty();
    }

    public static boolean handle(InteractionWiredTrigger trigger, RoomUnit roomUnit, Room room, Object[] stuff) {
        long millis = System.currentTimeMillis();
        THashSet<InteractionWiredEffect> effectsToExecute = new THashSet<InteractionWiredEffect>();
        if (WiredHandler.handle(trigger, roomUnit, room, stuff, effectsToExecute)) {
            for (InteractionWiredEffect effect : effectsToExecute) {
                WiredHandler.triggerEffect(effect, roomUnit, room, stuff, millis);
            }
            return true;
        }
        THashSet interactionWiredEffects = room.getRoomSpecialTypes().getEffects(trigger.getX(), trigger.getY()).stream().filter(item -> item instanceof WiredEffectTriggerStacksNegative || item instanceof WiredEffectTriggerStacksNegativeCondition).collect(Collectors.toCollection(THashSet::new));
        if (!interactionWiredEffects.isEmpty()) {
            for (InteractionWiredEffect effect : interactionWiredEffects) {
                WiredHandler.triggerEffect(effect, roomUnit, room, stuff, millis);
            }
            return true;
        }
        return false;
    }

    public static boolean handle(InteractionWiredTrigger trigger, RoomUnit roomUnit, Room room, Object[] stuff, THashSet<InteractionWiredEffect> effectsToExecute) {
        int roomUnitId;
        long millis = System.currentTimeMillis();
        int n = roomUnitId = roomUnit != null ? roomUnit.getId() : -1;
        if (Emulator.isReady && (Emulator.getConfig().getBoolean("wired.custom.enabled", false) && (trigger.canExecute(millis) || roomUnitId > -1) && trigger.userCanExecute(roomUnitId, millis) || !Emulator.getConfig().getBoolean("wired.custom.enabled", false) && trigger.canExecute(millis)) && trigger.execute(roomUnit, room, stuff)) {
            trigger.activateBox(room, roomUnit, millis);
            THashSet<InteractionWiredCondition> conditions = room.getRoomSpecialTypes().getConditions(trigger.getX(), trigger.getY());
            THashSet<InteractionWiredEffect> effects = room.getRoomSpecialTypes().getEffects(trigger.getX(), trigger.getY());
            effects.removeIf(item -> item instanceof WiredEffectTriggerStacksNegative || item instanceof WiredEffectTriggerStacksNegativeCondition);
            int count = 0;
            WiredAddonOneCondition isAddon = conditions.stream().filter(condition -> condition instanceof WiredAddonOneCondition).findFirst().orElse(null);
            conditions.removeIf(condition -> condition instanceof WiredAddonOneCondition);
            if (!conditions.isEmpty()) {
                ArrayList<WiredConditionType> matchedConditions = new ArrayList<WiredConditionType>(conditions.size());
                for (InteractionWiredCondition searchMatched : conditions) {
                    if (matchedConditions.contains((Object)searchMatched.getType()) || searchMatched.operator() != WiredConditionOperator.OR || !searchMatched.execute(roomUnit, room, stuff)) continue;
                    matchedConditions.add(searchMatched.getType());
                }
                if (isAddon != null) {
                    int newKey;
                    if (!isAddon.items.isEmpty()) {
                        for (WiredMatchFurniSetting setting : isAddon.items) {
                            InteractionWiredCondition condition2;
                            HabboItem item2 = room.getHabboItem(setting.item_id);
                            if (!(item2 instanceof InteractionWiredCondition) || ((condition2 = (InteractionWiredCondition)item2).operator() != WiredConditionOperator.OR || !matchedConditions.contains((Object)condition2.getType())) && (condition2.operator() != WiredConditionOperator.AND || !condition2.execute(roomUnit, room, stuff)) && !Emulator.getPluginManager().fireEvent(new WiredConditionFailedEvent(room, roomUnit, trigger, condition2)).isCancelled()) continue;
                            ++count;
                        }
                    } else {
                        for (InteractionWiredCondition condition3 : conditions) {
                            if (!(condition3.operator() == WiredConditionOperator.OR && matchedConditions.contains((Object)condition3.getType()) || condition3.operator() == WiredConditionOperator.AND && condition3.execute(roomUnit, room, stuff) || Emulator.getPluginManager().fireEvent(new WiredConditionFailedEvent(room, roomUnit, trigger, condition3)).isCancelled())) continue;
                            ++count;
                        }
                    }
                    if (isAddon.type == 0 && conditions.size() != count) {
                        return false;
                    }
                    if (isAddon.type == 1 && count < 1) {
                        return false;
                    }
                    if (isAddon.type == 2 && count < conditions.size() - 1) {
                        return false;
                    }
                    if (isAddon.type == 3 && count != 0) {
                        return false;
                    }
                    try {
                        newKey = Integer.parseInt(isAddon.key);
                    }
                    catch (NumberFormatException ignored) {
                        newKey = 0;
                    }
                    if (isAddon.type == 4 && count >= newKey) {
                        return false;
                    }
                    if (isAddon.type == 5 && count <= newKey) {
                        return false;
                    }
                    if (isAddon.type == 6 && count != newKey) {
                        return false;
                    }
                } else {
                    for (InteractionWiredCondition condition3 : conditions) {
                        if (condition3.operator() == WiredConditionOperator.OR && matchedConditions.contains((Object)condition3.getType()) || condition3.operator() == WiredConditionOperator.AND && condition3.execute(roomUnit, room, stuff) || Emulator.getPluginManager().fireEvent(new WiredConditionFailedEvent(room, roomUnit, trigger, condition3)).isCancelled()) continue;
                        return false;
                    }
                }
            }
            trigger.setCooldown(millis);
            boolean hasExtraRandom = room.getRoomSpecialTypes().hasExtraType(trigger.getX(), trigger.getY(), WiredExtraRandom.class);
            boolean hasExtraUnseen = room.getRoomSpecialTypes().hasExtraType(trigger.getX(), trigger.getY(), WiredExtraUnseen.class);
            THashSet<InteractionWiredExtra> extras = room.getRoomSpecialTypes().getExtras(trigger.getX(), trigger.getY());
            for (InteractionWiredExtra extra : extras) {
                extra.activateBox(room, roomUnit, millis);
            }
            ArrayList<InteractionWiredEffect> effectList = new ArrayList<InteractionWiredEffect>(effects);
            if (hasExtraRandom || hasExtraUnseen) {
                Collections.shuffle(effectList);
            }
            if (hasExtraUnseen) {
                for (InteractionWiredExtra extra : room.getRoomSpecialTypes().getExtras(trigger.getX(), trigger.getY())) {
                    if (!(extra instanceof WiredExtraUnseen)) continue;
                    extra.setExtradata(extra.getExtradata().equals("1") ? "0" : "1");
                    InteractionWiredEffect effect = ((WiredExtraUnseen)extra).getUnseenEffect(effectList);
                    effectsToExecute.add(effect);
                    break;
                }
            } else {
                for (InteractionWiredEffect effect : effectList) {
                    boolean executed = effectsToExecute.add(effect);
                    if (!hasExtraRandom || !executed) continue;
                    break;
                }
            }
            return true;
        }
        return false;
    }

    public static boolean triggerEffect(InteractionWiredEffect effect, RoomUnit roomUnit, Room room, Object[] stuff, long millis) {
        boolean executed = false;
        if (effect != null && (effect.canExecute(millis) || roomUnit != null && effect.requiresTriggeringUser() && Emulator.getConfig().getBoolean("wired.custom.enabled", false) && effect.userCanExecute(roomUnit.getId(), millis))) {
            executed = true;
            if (!effect.requiresTriggeringUser() || roomUnit != null && effect.requiresTriggeringUser()) {
                long delay = (long)effect.getDelay() * 500L;
                long etc = System.currentTimeMillis();
                Emulator.getThreading().run(() -> {
                    if (room.isLoaded()) {
                        if (etc - effect.getLastExecuted() < delay) {
                            return;
                        }
                        try {
                            if (!effect.execute(roomUnit, room, stuff)) {
                                return;
                            }
                            effect.setCooldown(millis);
                            effect.setLastExecuted(etc);
                        }
                        catch (Exception e) {
                            LOGGER.error("Caught exception", e);
                        }
                        effect.activateBox(room, roomUnit, millis);
                    }
                }, delay);
            }
        }
        return executed;
    }

    public static GsonBuilder getGsonBuilder() {
        if (gsonBuilder == null) {
            gsonBuilder = new GsonBuilder();
        }
        return gsonBuilder;
    }

    public static boolean executeEffectsAtTiles(THashSet<RoomTile> tiles, RoomUnit roomUnit, Room room, Object[] stuff) {
        for (RoomTile tile : tiles) {
            if (room == null) continue;
            THashSet<HabboItem> items = room.getItemsAt(tile);
            long millis = room.getCycleTimestamp();
            for (HabboItem item : items) {
                if (!(item instanceof InteractionWiredEffect) || item instanceof WiredEffectTriggerStacks) continue;
                WiredHandler.triggerEffect((InteractionWiredEffect)item, roomUnit, room, stuff, millis);
                ((InteractionWiredEffect)item).setCooldown(millis);
            }
        }
        return true;
    }

    public static void dropRewards(int wiredId) {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM wired_rewards_given WHERE wired_item = ?");){
            statement.setInt(1, wiredId);
            statement.execute();
        }
        catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
    }

    private static void giveReward(Habbo habbo, WiredEffectGiveReward wiredBox, WiredGiveRewardItem reward) {
        if (wiredBox.limit > 0) {
            ++wiredBox.given;
        }
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO wired_rewards_given (wired_item, user_id, reward_id, timestamp) VALUES ( ?, ?, ?, ?)");){
            statement.setInt(1, wiredBox.getId());
            statement.setInt(2, habbo.getHabboInfo().getId());
            statement.setInt(3, reward.id);
            statement.setInt(4, Emulator.getIntUnixTimestamp());
            statement.execute();
        }
        catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
        if (reward.badge) {
            UserWiredRewardReceived rewardReceived = new UserWiredRewardReceived(habbo, wiredBox, "badge", reward.data);
            if (Emulator.getPluginManager().fireEvent(rewardReceived).isCancelled()) {
                return;
            }
            if (rewardReceived.value.isEmpty()) {
                return;
            }
            if (habbo.getInventory().getBadgesComponent().hasBadge(rewardReceived.value)) {
                return;
            }
            HabboBadge badge = new HabboBadge(0, rewardReceived.value, 0, habbo);
            Emulator.getThreading().run(badge);
            habbo.getInventory().getBadgesComponent().addBadge(badge);
            habbo.getClient().sendResponse(new AddUserBadgeComposer(badge));
            habbo.getClient().sendResponse(new WiredRewardAlertComposer(7));
        } else {
            String[] data = reward.data.split("#");
            if (data.length == 2) {
                UserWiredRewardReceived rewardReceived = new UserWiredRewardReceived(habbo, wiredBox, data[0], data[1]);
                if (Emulator.getPluginManager().fireEvent(rewardReceived).isCancelled()) {
                    return;
                }
                if (rewardReceived.value.isEmpty()) {
                    return;
                }
                if (rewardReceived.type.equalsIgnoreCase("credits")) {
                    int credits = Integer.valueOf(rewardReceived.value);
                    habbo.giveCredits(credits);
                } else if (rewardReceived.type.equalsIgnoreCase("pixels")) {
                    int pixels = Integer.valueOf(rewardReceived.value);
                    habbo.givePixels(pixels);
                } else if (rewardReceived.type.startsWith("points")) {
                    int points = Integer.valueOf(rewardReceived.value);
                    int type = 5;
                    try {
                        type = Integer.valueOf(rewardReceived.type.replace("points", ""));
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                    habbo.givePoints(type, points);
                } else if (rewardReceived.type.equalsIgnoreCase("furni")) {
                    HabboItem item;
                    Item baseItem = Emulator.getGameEnvironment().getItemManager().getItem(Integer.valueOf(rewardReceived.value));
                    if (baseItem != null && (item = Emulator.getGameEnvironment().getItemManager().createItem(habbo.getHabboInfo().getId(), baseItem, 0, 0, "")) != null) {
                        habbo.getClient().sendResponse(new AddHabboItemComposer(item));
                        habbo.getClient().getHabbo().getInventory().getItemsComponent().addItem(item);
                        habbo.getClient().sendResponse(new PurchaseOKComposer(null));
                        habbo.getClient().sendResponse(new InventoryRefreshComposer());
                        habbo.getClient().sendResponse(new WiredRewardAlertComposer(6));
                    }
                } else if (rewardReceived.type.equalsIgnoreCase("respect")) {
                    habbo.getHabboStats().respectPointsReceived += Integer.valueOf(rewardReceived.value).intValue();
                } else if (rewardReceived.type.equalsIgnoreCase("cata")) {
                    CatalogItem item = Emulator.getGameEnvironment().getCatalogManager().getCatalogItem(Integer.valueOf(rewardReceived.value));
                    if (item != null) {
                        Emulator.getGameEnvironment().getCatalogManager().purchaseItem(null, item, habbo, 1, "", true);
                    }
                    habbo.getClient().sendResponse(new WiredRewardAlertComposer(6));
                }
            }
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static boolean getReward(Habbo habbo, WiredEffectGiveReward wiredBox) {
        if (wiredBox.limit > 0 && wiredBox.limit - wiredBox.given == 0) {
            habbo.getClient().sendResponse(new WiredRewardAlertComposer(0));
            return false;
        }
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) as row_count, wired_rewards_given.* FROM wired_rewards_given WHERE user_id = ? AND wired_item = ? ORDER BY timestamp DESC LIMIT ?", 1004, 1007);){
            statement.setInt(1, habbo.getHabboInfo().getId());
            statement.setInt(2, wiredBox.getId());
            statement.setInt(3, wiredBox.rewardItems.size());
            try (ResultSet set = statement.executeQuery();){
                if (!set.first()) return false;
                if (set.getInt("row_count") >= 1 && wiredBox.rewardTime == 0) {
                    habbo.getClient().sendResponse(new WiredRewardAlertComposer(1));
                    boolean bl = false;
                    return bl;
                }
                set.beforeFirst();
                if (set.next()) {
                    if (wiredBox.rewardTime == 3 && Emulator.getIntUnixTimestamp() - set.getInt("timestamp") <= 60) {
                        habbo.getClient().sendResponse(new WiredRewardAlertComposer(8));
                        boolean bl = false;
                        return bl;
                    }
                    if (wiredBox.uniqueRewards && set.getInt("row_count") == wiredBox.rewardItems.size()) {
                        habbo.getClient().sendResponse(new WiredRewardAlertComposer(5));
                        boolean bl = false;
                        return bl;
                    }
                    if (wiredBox.rewardTime == 2 && Emulator.getIntUnixTimestamp() - set.getInt("timestamp") < 3600 * wiredBox.limitationInterval) {
                        habbo.getClient().sendResponse(new WiredRewardAlertComposer(3));
                        boolean bl = false;
                        return bl;
                    }
                    if (wiredBox.rewardTime == 1 && Emulator.getIntUnixTimestamp() - set.getInt("timestamp") < 86400 * wiredBox.limitationInterval) {
                        habbo.getClient().sendResponse(new WiredRewardAlertComposer(2));
                        boolean bl = false;
                        return bl;
                    }
                }
                if (wiredBox.uniqueRewards) {
                    WiredGiveRewardItem item;
                    boolean found;
                    Iterator iterator = wiredBox.rewardItems.iterator();
                    do {
                        if (!iterator.hasNext()) return false;
                        item = (WiredGiveRewardItem)iterator.next();
                        set.beforeFirst();
                        found = false;
                        while (set.next()) {
                            if (set.getInt("reward_id") != item.id) continue;
                            found = true;
                        }
                    } while (found);
                    WiredHandler.giveReward(habbo, wiredBox, item);
                    boolean bl = true;
                    return bl;
                }
                int randomNumber = Emulator.getRandom().nextInt(101);
                int count = 0;
                Iterator iterator = wiredBox.rewardItems.iterator();
                while (iterator.hasNext()) {
                    WiredGiveRewardItem item = (WiredGiveRewardItem)iterator.next();
                    if (randomNumber >= count && randomNumber <= count + item.probability) {
                        WiredHandler.giveReward(habbo, wiredBox, item);
                        boolean bl = true;
                        return bl;
                    }
                    count += item.probability;
                }
                return false;
            }
        }
        catch (SQLException e) {
            LOGGER.error("Caught SQL exception", e);
        }
        return false;
    }

    public static void resetTimers(Room room) {
        if (!room.isLoaded() || room.getRoomSpecialTypes() == null) {
            return;
        }
        room.getRoomSpecialTypes().getTriggers().forEach(t -> {
            if (t == null) {
                return;
            }
            if (t.getType() == WiredTriggerType.AT_GIVEN_TIME || t.getType() == WiredTriggerType.PERIODICALLY || t.getType() == WiredTriggerType.PERIODICALLY_LONG) {
                ((WiredTriggerReset)((Object)t)).resetTimer();
            }
        });
        room.setLastTimerReset(Emulator.getIntUnixTimestamp());
    }
}

