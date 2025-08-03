/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.items.interactions.wired.conditions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.games.Game;
import com.eu.habbo.habbohotel.games.GamePlayer;
import com.eu.habbo.habbohotel.games.GameTeamColors;
import com.eu.habbo.habbohotel.games.wired.WiredGame;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredCondition;
import com.eu.habbo.habbohotel.items.interactions.wired.WiredSettings;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.DanceType;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboBadge;
import com.eu.habbo.habbohotel.users.HabboGender;
import com.eu.habbo.habbohotel.wired.WiredConditionType;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.messages.ServerMessage;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.apache.commons.lang3.StringUtils;

public class WiredConditionSuperWired
extends InteractionWiredCondition {
    public static final WiredConditionType type = WiredConditionType.ACTOR_WEARS_BADGE;
    protected String key = "";

    public WiredConditionSuperWired(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredConditionSuperWired(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        Habbo habbo = room.getHabbo(roomUnit);
        String str = this.key;
        Game TeamManager = room.getGame(WiredGame.class);
        String[] finalText = str.split(":");
        switch (finalText[0]) {
            case "enable": {
                if (roomUnit == null) {
                    return false;
                }
                return roomUnit.getEffectId() == Integer.parseInt(finalText[1]);
            }
            case "noenable": {
                if (roomUnit == null) {
                    return false;
                }
                return roomUnit.getEffectId() != Integer.parseInt(finalText[1]);
            }
            case "handitem": {
                if (roomUnit == null) {
                    return false;
                }
                return roomUnit.getHandItem() == Integer.parseInt(finalText[1]);
            }
            case "nohanditem": {
                if (roomUnit == null) {
                    return false;
                }
                return roomUnit.getHandItem() != Integer.parseInt(finalText[1]);
            }
            case "dance": {
                if (roomUnit == null) {
                    return false;
                }
                return roomUnit.getDanceType().getType() == Integer.parseInt(finalText[1]);
            }
            case "isdance": {
                if (roomUnit == null) {
                    return false;
                }
                return roomUnit.getDanceType() != DanceType.NONE;
            }
            case "transf": {
                String petName = finalText[1];
                if (roomUnit == null) {
                    return false;
                }
                Emulator.getGameEnvironment().getPetManager().getPetData(petName);
                if (petName != null) {
                    return true;
                }
            }
            case "freeze": {
                if (roomUnit == null) {
                    return false;
                }
                return !roomUnit.canWalk();
            }
            case "nofreeze": {
                if (roomUnit == null) {
                    return false;
                }
                return roomUnit.canWalk();
            }
            case "lay": {
                if (roomUnit == null) {
                    return false;
                }
                return roomUnit.cmdLay;
            }
            case "sit": {
                if (roomUnit == null) {
                    return false;
                }
                return roomUnit.cmdSit;
            }
            case "mission": {
                return habbo != null && habbo.getHabboInfo().getMotto().contains(finalText[1]);
            }
            case "notmission": {
                return habbo != null && !habbo.getHabboInfo().getMotto().contains(finalText[1]);
            }
            case "badge": {
                if (habbo == null) break;
                ArrayList<HabboBadge> arrayList = habbo.getInventory().getBadgesComponent().getWearingBadges();
                synchronized (arrayList) {
                    for (HabboBadge badge : habbo.getInventory().getBadgesComponent().getWearingBadges()) {
                        if (!badge.getCode().equalsIgnoreCase(finalText[1])) continue;
                        return true;
                    }
                    break;
                }
            }
            case "nobadge": {
                if (habbo == null) break;
                ArrayList<HabboBadge> arrayList = habbo.getInventory().getBadgesComponent().getWearingBadges();
                synchronized (arrayList) {
                    for (HabboBadge b : habbo.getInventory().getBadgesComponent().getWearingBadges()) {
                        if (!b.getCode().equalsIgnoreCase(finalText[1])) continue;
                        return false;
                    }
                }
                return true;
            }
            case "pixels": {
                if (habbo == null) break;
                return habbo.getHabboInfo().getCurrencyAmount(0) >= Integer.parseInt(finalText[1]);
            }
            case "nopixels": {
                if (habbo == null) break;
                return habbo.getHabboInfo().getCurrencyAmount(0) < Integer.parseInt(finalText[1]);
            }
            case "diamantes": {
                if (habbo == null) break;
                return habbo.getHabboInfo().getCurrencyAmount(5) >= Integer.parseInt(finalText[1]);
            }
            case "nodiamantes": {
                if (habbo == null) break;
                return habbo.getHabboInfo().getCurrencyAmount(5) <= Integer.parseInt(finalText[1]);
            }
            case "creditos": 
            case "credits": {
                if (habbo == null) break;
                return habbo.getHabboInfo().getCredits() >= Integer.parseInt(finalText[1]);
            }
            case "nocreditos": 
            case "nocredits": {
                if (habbo == null) break;
                return habbo.getHabboInfo().getCredits() <= Integer.parseInt(finalText[1]);
            }
            case "userboy": {
                if (habbo == null || !habbo.getHabboInfo().getGender().equals((Object)HabboGender.M)) break;
                return true;
            }
            case "notuserboy": {
                if (habbo == null || habbo.getHabboInfo().getGender().equals((Object)HabboGender.M)) break;
                return true;
            }
            case "usergirl": {
                if (habbo == null || !habbo.getHabboInfo().getGender().equals((Object)HabboGender.F)) break;
                return true;
            }
            case "notusergirl": {
                if (habbo == null || habbo.getHabboInfo().getGender().equals((Object)HabboGender.F)) break;
                return true;
            }
            case "afk": {
                if (habbo == null || !habbo.getRoomUnit().isIdle()) break;
                return true;
            }
            case "notafk": {
                if (habbo == null || habbo.getRoomUnit().isIdle()) break;
                return true;
            }
            case "rank": {
                if (roomUnit == null) {
                    return false;
                }
                return habbo.getHabboInfo().getRank().getId() >= Integer.parseInt(finalText[1]);
            }
            case "notrank": {
                if (roomUnit == null) {
                    return false;
                }
                return habbo.getHabboInfo().getRank().getId() <= Integer.parseInt(finalText[1]);
            }
            case "teamred": {
                GameTeamColors teamColor = GameTeamColors.RED;
                if (habbo == null) break;
                return habbo.getHabboInfo().getGamePlayer() == null || !habbo.getHabboInfo().getGamePlayer().getTeamColor().equals((Object)teamColor);
            }
            case "teamblue": {
                GameTeamColors teamColor = GameTeamColors.BLUE;
                if (habbo == null) break;
                return habbo.getHabboInfo().getGamePlayer() == null || !habbo.getHabboInfo().getGamePlayer().getTeamColor().equals((Object)teamColor);
            }
            case "teamgreen": {
                GameTeamColors teamColor = GameTeamColors.GREEN;
                if (habbo == null) break;
                return habbo.getHabboInfo().getGamePlayer() == null || !habbo.getHabboInfo().getGamePlayer().getTeamColor().equals((Object)teamColor);
            }
            case "teamyellow": {
                GameTeamColors teamColor = GameTeamColors.YELLOW;
                if (habbo == null) break;
                return habbo.getHabboInfo().getGamePlayer() == null || !habbo.getHabboInfo().getGamePlayer().getTeamColor().equals((Object)teamColor);
            }
            case "teamredeq": {
                if (!StringUtils.isNumeric(finalText[1]) || TeamManager == null || TeamManager.getTeam(GameTeamColors.RED) == null) {
                    return false;
                }
                int redCountEq = 0;
                for (GamePlayer gamePlayer : TeamManager.getTeam(GameTeamColors.RED).getMembers()) {
                    if (gamePlayer == null) continue;
                    ++redCountEq;
                }
                return redCountEq == Integer.parseInt(finalText[1]);
            }
            case "teamgreeneq": {
                if (!StringUtils.isNumeric(finalText[1]) || TeamManager == null || TeamManager.getTeam(GameTeamColors.GREEN) == null) {
                    return false;
                }
                int greenCountEq = 0;
                for (GamePlayer gamePlayer : TeamManager.getTeam(GameTeamColors.GREEN).getMembers()) {
                    if (gamePlayer == null) continue;
                    ++greenCountEq;
                }
                return greenCountEq == Integer.parseInt(finalText[1]);
            }
            case "teamblueeq": {
                if (!StringUtils.isNumeric(finalText[1]) || TeamManager == null || TeamManager.getTeam(GameTeamColors.BLUE) == null) {
                    return false;
                }
                int blueCountEq = 0;
                for (GamePlayer gamePlayer : TeamManager.getTeam(GameTeamColors.BLUE).getMembers()) {
                    if (gamePlayer == null) continue;
                    ++blueCountEq;
                }
                return blueCountEq == Integer.parseInt(finalText[1]);
            }
            case "teamyelloweq": {
                if (!StringUtils.isNumeric(finalText[1]) || TeamManager == null || TeamManager.getTeam(GameTeamColors.YELLOW) == null) {
                    return false;
                }
                int yellowCountEq = 0;
                for (GamePlayer gamePlayer : TeamManager.getTeam(GameTeamColors.YELLOW).getMembers()) {
                    if (gamePlayer == null) continue;
                    ++yellowCountEq;
                }
                return yellowCountEq == Integer.parseInt(finalText[1]);
            }
            case "teamredcount": {
                if (!StringUtils.isNumeric(finalText[1]) || TeamManager == null || TeamManager.getTeam(GameTeamColors.RED) == null) {
                    return false;
                }
                int redCount = 0;
                for (GamePlayer gamePlayer : TeamManager.getTeam(GameTeamColors.RED).getMembers()) {
                    if (gamePlayer == null) continue;
                    ++redCount;
                }
                return redCount >= Integer.parseInt(finalText[1]);
            }
            case "teamgreencount": {
                if (!StringUtils.isNumeric(finalText[1]) || TeamManager == null || TeamManager.getTeam(GameTeamColors.GREEN) == null) {
                    return false;
                }
                int greenCount = 0;
                for (GamePlayer gamePlayer : TeamManager.getTeam(GameTeamColors.GREEN).getMembers()) {
                    if (gamePlayer == null) continue;
                    ++greenCount;
                }
                return greenCount >= Integer.parseInt(finalText[1]);
            }
            case "teambluecount": {
                if (!StringUtils.isNumeric(finalText[1]) || TeamManager == null || TeamManager.getTeam(GameTeamColors.BLUE) == null) {
                    return false;
                }
                int blueCount = 0;
                for (GamePlayer gamePlayer : TeamManager.getTeam(GameTeamColors.BLUE).getMembers()) {
                    if (gamePlayer == null) continue;
                    ++blueCount;
                }
                return blueCount >= Integer.parseInt(finalText[1]);
            }
            case "teamyellowcount": {
                if (!StringUtils.isNumeric(finalText[1]) || TeamManager == null || TeamManager.getTeam(GameTeamColors.YELLOW) == null) {
                    return false;
                }
                int yellowCount = 0;
                for (GamePlayer gamePlayer : TeamManager.getTeam(GameTeamColors.YELLOW).getMembers()) {
                    if (gamePlayer == null) continue;
                    ++yellowCount;
                }
                return yellowCount >= Integer.parseInt(finalText[1]);
            }
            case "teamredcountneg": {
                if (!StringUtils.isNumeric(finalText[1]) || TeamManager == null || TeamManager.getTeam(GameTeamColors.RED) == null) {
                    return false;
                }
                int redCountNeg = 0;
                for (GamePlayer gamePlayer : TeamManager.getTeam(GameTeamColors.RED).getMembers()) {
                    if (gamePlayer == null) continue;
                    ++redCountNeg;
                }
                return redCountNeg <= Integer.parseInt(finalText[1]);
            }
            case "teamgreencountneg": {
                if (!StringUtils.isNumeric(finalText[1]) || TeamManager == null || TeamManager.getTeam(GameTeamColors.GREEN) == null) {
                    return false;
                }
                int greenCountNeg = 0;
                for (GamePlayer gamePlayer : TeamManager.getTeam(GameTeamColors.GREEN).getMembers()) {
                    if (gamePlayer == null) continue;
                    ++greenCountNeg;
                }
                return greenCountNeg <= Integer.parseInt(finalText[1]);
            }
            case "teambluecountneg": {
                if (!StringUtils.isNumeric(finalText[1]) || TeamManager == null || TeamManager.getTeam(GameTeamColors.BLUE) == null) {
                    return false;
                }
                int blueCountNeg = 0;
                for (GamePlayer gamePlayer : TeamManager.getTeam(GameTeamColors.BLUE).getMembers()) {
                    if (gamePlayer == null) continue;
                    ++blueCountNeg;
                }
                return blueCountNeg <= Integer.parseInt(finalText[1]);
            }
            case "teamyellowcountneg": {
                if (!StringUtils.isNumeric(finalText[1]) || TeamManager == null || TeamManager.getTeam(GameTeamColors.YELLOW) == null) {
                    return false;
                }
                int yellowCountNeg = 0;
                for (GamePlayer gamePlayer : TeamManager.getTeam(GameTeamColors.YELLOW).getMembers()) {
                    if (gamePlayer == null) continue;
                    ++yellowCountNeg;
                }
                return yellowCountNeg <= Integer.parseInt(finalText[1]);
            }
            case "teamcallcount": {
                if (!StringUtils.isNumeric(finalText[1]) || TeamManager == null) {
                    return false;
                }
                int count = 0;
                if (TeamManager.getTeam(GameTeamColors.RED) != null) {
                    for (GamePlayer gamePlayer : TeamManager.getTeam(GameTeamColors.RED).getMembers()) {
                        if (gamePlayer == null) continue;
                        ++count;
                    }
                }
                if (TeamManager.getTeam(GameTeamColors.GREEN) != null) {
                    for (GamePlayer gamePlayer : TeamManager.getTeam(GameTeamColors.GREEN).getMembers()) {
                        if (gamePlayer == null) continue;
                        ++count;
                    }
                }
                if (TeamManager.getTeam(GameTeamColors.BLUE) != null) {
                    for (GamePlayer gamePlayer : TeamManager.getTeam(GameTeamColors.BLUE).getMembers()) {
                        if (gamePlayer == null) continue;
                        ++count;
                    }
                }
                if (TeamManager.getTeam(GameTeamColors.YELLOW) != null) {
                    for (GamePlayer gamePlayer : TeamManager.getTeam(GameTeamColors.YELLOW).getMembers()) {
                        if (gamePlayer == null) continue;
                        ++count;
                    }
                }
                if (Integer.parseInt(finalText[1]) == 0) {
                    return count == 0;
                }
                return count != 0;
            }
        }
        return false;
    }

    @Override
    public String getWiredData() {
        return WiredHandler.getGsonBuilder().create().toJson(new JsonData(this.key));
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException {
        String wiredData = set.getString("wired_data");
        if (wiredData.startsWith("{")) {
            JsonData data = WiredHandler.getGsonBuilder().create().fromJson(wiredData, JsonData.class);
            this.key = data.key;
        } else {
            this.key = wiredData;
        }
    }

    @Override
    public void onPickUp() {
        this.key = "";
    }

    @Override
    public WiredConditionType getType() {
        return type;
    }

    @Override
    public void serializeWiredData(ServerMessage message, Room room) {
        message.appendBoolean(false);
        message.appendInt(5);
        message.appendInt(0);
        message.appendInt(this.getBaseItem().getSpriteId());
        message.appendInt(this.getId());
        message.appendString(this.key);
        message.appendInt(0);
        message.appendInt(0);
        message.appendInt(this.getType().code);
        message.appendInt(0);
        message.appendInt(0);
    }

    @Override
    public boolean saveData(WiredSettings settings) {
        this.key = settings.getStringParam();
        return true;
    }

    static class JsonData {
        String key;

        public JsonData(String key) {
            this.key = key;
        }
    }
}

