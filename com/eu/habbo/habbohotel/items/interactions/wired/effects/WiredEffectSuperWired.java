/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.wired.WiredSettings;
import com.eu.habbo.habbohotel.items.interactions.wired.effects.WiredEffectWhisper;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.DanceType;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboBadge;
import com.eu.habbo.habbohotel.users.inventory.BadgesComponent;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.incoming.wired.WiredSaveException;
import com.eu.habbo.messages.outgoing.generic.alerts.StaffAlertWithLinkComposer;
import com.eu.habbo.messages.outgoing.modtool.ModToolIssueHandledComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserDanceComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserWhisperComposer;
import com.eu.habbo.messages.outgoing.users.AddUserBadgeComposer;
import com.eu.habbo.messages.outgoing.wired.WiredRewardAlertComposer;
import com.eu.habbo.threading.runnables.RoomUnitKick;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class WiredEffectSuperWired
extends WiredEffectWhisper {
    public static int numero;

    public WiredEffectSuperWired(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredEffectSuperWired(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        Habbo habbo = room.getHabbo(roomUnit);
        String str = this.message;
        String[] finalText = str.split(":");
        String[] params = str.split(" ");
        switch (finalText[0]) {
            case "enable": {
                int efecto = Integer.parseInt(finalText[1]);
                if (finalText[1] == null) {
                    habbo.whisper(Emulator.getTexts().getValue("wired.error.superwired"), RoomChatMessageBubbles.ALERT);
                    return false;
                }
                if (!Emulator.getGameEnvironment().getPermissionsManager().isEffectBlocked(efecto, habbo.getHabboInfo().getRank().getId())) {
                    habbo.getHabboInfo().getCurrentRoom().giveEffect(habbo, efecto, -1);
                    break;
                }
                habbo.whisper(Emulator.getTexts().getValue("commands.error.cmd_enable.not_allowed"), RoomChatMessageBubbles.ALERT);
                return true;
            }
            case "randomenable": {
                numero = (int)(Math.random() * 199.0) + 1;
                room.giveEffect(roomUnit, numero, Integer.MAX_VALUE);
                break;
            }
            case "roomenable": {
                if (finalText[1] == null) {
                    habbo.whisper(Emulator.getTexts().getValue("wired.error.superwired"), RoomChatMessageBubbles.ALERT);
                    return false;
                }
                int effectId = Integer.parseInt(finalText[1]);
                if (effectId >= 0) {
                    Room roomusers = habbo.getHabboInfo().getCurrentRoom();
                    for (Habbo habbos : roomusers.getHabbos()) {
                        roomusers.giveEffect(habbos, effectId, -1);
                    }
                    return true;
                }
                habbo.whisper(Emulator.getTexts().getValue("commands.error.cmd_roomeffect.positive"), RoomChatMessageBubbles.ALERT);
                break;
            }
            case "handitem": {
                if (finalText[1] == null) {
                    habbo.whisper(Emulator.getTexts().getValue("wired.error.superwired"), RoomChatMessageBubbles.ALERT);
                    return false;
                }
                room.giveHandItem(habbo, Integer.parseInt(finalText[1]));
                break;
            }
            case "randomitem": {
                numero = (int)(Math.random() * 1999.0) + 1;
                room.giveHandItem(habbo, numero);
                break;
            }
            case "roomitem": {
                if (finalText[1] == null) {
                    habbo.whisper(Emulator.getTexts().getValue("wired.error.superwired"), RoomChatMessageBubbles.ALERT);
                    return false;
                }
                int item = Integer.parseInt(finalText[1]);
                if (item < 0) break;
                Room roomusers = habbo.getHabboInfo().getCurrentRoom();
                for (Habbo habbos : roomusers.getHabbos()) {
                    roomusers.giveHandItem(habbos, item);
                }
                return true;
            }
            case "speed": {
                if (finalText[1] == null) {
                    habbo.whisper(Emulator.getTexts().getValue("wired.error.superwired"), RoomChatMessageBubbles.ALERT);
                    return false;
                }
                room.setRollerSpeed(Integer.parseInt(finalText[1]));
                break;
            }
            case "dance": {
                if (finalText[1] == null) {
                    habbo.whisper(Emulator.getTexts().getValue("wired.error.superwired"), RoomChatMessageBubbles.ALERT);
                    return false;
                }
                if (Integer.parseInt(finalText[1]) >= 0 && Integer.parseInt(finalText[1]) <= 4) {
                    habbo.getRoomUnit().setDanceType(DanceType.values()[Integer.parseInt(finalText[1])]);
                    habbo.getHabboInfo().getCurrentRoom().sendComposer(new RoomUserDanceComposer(habbo.getRoomUnit()).compose());
                    break;
                }
                habbo.getRoomUnit().setDanceType(DanceType.values()[0]);
                habbo.getHabboInfo().getCurrentRoom().sendComposer(new RoomUserDanceComposer(habbo.getRoomUnit()).compose());
                break;
            }
            case "roomdance": {
                int danceId;
                if (finalText[1] == null) {
                    habbo.whisper(Emulator.getTexts().getValue("wired.error.superwired"), RoomChatMessageBubbles.ALERT);
                    return false;
                }
                try {
                    danceId = Integer.parseInt(finalText[1]);
                }
                catch (Exception e) {
                    habbo.whisper(Emulator.getTexts().getValue("commands.error.cmd_danceall.invalid_dance"), RoomChatMessageBubbles.ALERT);
                    return true;
                }
                if (danceId < 0 || danceId > 4) {
                    habbo.whisper(Emulator.getTexts().getValue("commands.error.cmd_danceall.outside_bounds"), RoomChatMessageBubbles.ALERT);
                    return true;
                }
                for (Habbo habbos : habbo.getHabboInfo().getCurrentRoom().getHabbos()) {
                    habbos.getRoomUnit().setDanceType(DanceType.values()[danceId]);
                    habbos.getHabboInfo().getCurrentRoom().sendComposer(new RoomUserDanceComposer(habbos.getRoomUnit()).compose());
                }
                break;
            }
            case "freeze": {
                habbo.getRoomUnit().setCanWalk(false);
                break;
            }
            case "unfreeze": {
                habbo.getRoomUnit().setCanWalk(true);
                break;
            }
            case "moonwalk": {
                room.giveEffect(roomUnit, 136, Integer.MAX_VALUE);
                break;
            }
            case "sit": {
                habbo.getHabboInfo().getCurrentRoom().makeSit(habbo);
                break;
            }
            case "kick": {
                if (habbo == null) break;
                if (habbo.hasPermission(Permission.ACC_UNKICKABLE)) {
                    habbo.whisper(Emulator.getTexts().getValue("hotel.wired.kickexception.unkickable"));
                    return true;
                }
                if (habbo.getHabboInfo().getId() == room.getOwnerId()) {
                    habbo.whisper(Emulator.getTexts().getValue("hotel.wired.kickexception.owner"));
                    return true;
                }
                if (!finalText[1].isEmpty()) {
                    habbo.getClient().sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(finalText[1], habbo, habbo, RoomChatMessageBubbles.ALERT)));
                }
                Emulator.getThreading().run(new RoomUnitKick(habbo, room, true), 2000L);
                return true;
            }
            case "superkick": {
                room.kickHabbo(habbo, false);
                break;
            }
            case "fastwalk": {
                habbo.getRoomUnit().setFastWalk(!habbo.getRoomUnit().isFastWalk());
                break;
            }
            case "badge": {
                if (finalText[1] == null) {
                    habbo.whisper(Emulator.getTexts().getValue("wired.error.superwired"), RoomChatMessageBubbles.ALERT);
                    return false;
                }
                if (habbo != null) {
                    if (!habbo.addBadge(finalText[1])) {
                        habbo.whisper(Emulator.getTexts().getValue("commands.error.cmd_badge.already_owned").replace("%user%", habbo.getHabboInfo().getUsername()).replace("%badge%", finalText[1]), RoomChatMessageBubbles.ALERT);
                    } else {
                        return true;
                    }
                }
                return false;
            }
            case "testgivebadge": {
                if (finalText[1] == null) {
                    habbo.whisper(Emulator.getTexts().getValue("wired.error.superwired"), RoomChatMessageBubbles.ALERT);
                    return false;
                }
                if (habbo == null) break;
                if (habbo.getInventory().getBadgesComponent().hasBadge(finalText[1])) {
                    habbo.getClient().sendResponse(new WiredRewardAlertComposer(1));
                    break;
                }
                HabboBadge badge = new HabboBadge(0, finalText[1], 0, habbo);
                Emulator.getThreading().run(badge);
                habbo.getInventory().getBadgesComponent().addBadge(badge);
                habbo.getClient().sendResponse(new AddUserBadgeComposer(badge));
                habbo.getClient().sendResponse(new WiredRewardAlertComposer(7));
                break;
            }
            case "testremovebadge": {
                if (habbo == null) {
                    return false;
                }
                if (finalText[1].isEmpty()) {
                    return false;
                }
                if (habbo.getInventory().getBadgesComponent().hasBadge(finalText[1])) {
                    BadgesComponent.deleteBadge(habbo.getHabboInfo().getId(), finalText[1]);
                    habbo.getInventory().getBadgesComponent().removeBadge(finalText[1]);
                    break;
                }
                return false;
            }
            case "removebadge": {
                if (habbo == null) {
                    return false;
                }
                if (finalText[1].isEmpty()) {
                    return false;
                }
                if (habbo.getInventory().getBadgesComponent().hasBadge(finalText[1])) {
                    BadgesComponent.deleteBadge(habbo.getHabboInfo().getId(), finalText[1]);
                    habbo.getInventory().getBadgesComponent().removeBadge(finalText[1]);
                    break;
                }
                return false;
            }
            case "points": {
                if (finalText[1] == null) {
                    habbo.whisper(Emulator.getTexts().getValue("wired.error.superwired"), RoomChatMessageBubbles.ALERT);
                    return false;
                }
                if (habbo.getHabboStats().cache.containsKey("points_game")) {
                    int point = habbo.getHabboStats().cache.get("points_game").hashCode() + Integer.parseInt(finalText[1]);
                    habbo.getHabboStats().cache.put("points_game", point);
                    habbo.whisper("Se te a aumentado " + finalText[1] + " points, ahora tienes " + point);
                    break;
                }
                habbo.getHabboStats().cache.put("points_game", Integer.parseInt(finalText[1]));
                habbo.whisper("Se te a dado " + finalText[1] + " points");
                break;
            }
            case "removepoints": {
                if (finalText[1] == null) {
                    habbo.whisper(Emulator.getTexts().getValue("wired.error.superwired"), RoomChatMessageBubbles.ALERT);
                    return false;
                }
                if (!habbo.getHabboStats().cache.containsKey("points_game")) break;
                int point = habbo.getHabboStats().cache.get("points_game").hashCode() - Integer.parseInt(finalText[1]);
                habbo.getHabboStats().cache.put("points_game", point);
                habbo.whisper("Te quedan " + point + " points");
                if (habbo.getHabboStats().cache.get("points_game").hashCode() != 0) break;
                habbo.getHabboStats().cache.remove("points_game");
                habbo.whisper("Se te a reseteado los points");
                break;
            }
            case "resetpoints": {
                if (!habbo.getHabboStats().cache.containsKey("points_game")) break;
                habbo.getHabboStats().cache.remove("points_game");
                habbo.whisper("Se te a reseteado los points");
            }
        }
        switch (params[0]) {
            case "roomalert": {
                StringBuilder message = new StringBuilder();
                if (params.length < 2) break;
                for (int i = 1; i < params.length; ++i) {
                    message.append(params[i]).append(" ");
                }
                if (message.length() == 0) {
                    return true;
                }
                room = habbo.getHabboInfo().getCurrentRoom();
                if (room == null) break;
                room.sendComposer(new ModToolIssueHandledComposer(message.toString()).compose());
                return true;
            }
            case "alert": {
                int i;
                if (params.length < 2) {
                    return true;
                }
                if (params.length < 3) {
                    return true;
                }
                String targetUsername = params[1];
                StringBuilder mensaje = new StringBuilder();
                for (i = 2; i < params.length; ++i) {
                    mensaje.append(params[i]).append(" ");
                }
                if (habbo == null) break;
                habbo.alert(String.valueOf(mensaje) + "\r\n    -" + habbo.getHabboInfo().getUsername());
                habbo.whisper(Emulator.getTexts().getValue("commands.succes.cmd_alert.message_send").replace("%user%", targetUsername), RoomChatMessageBubbles.ALERT);
                break;
            }
            case "roomalerturl": {
                int i;
                if (params.length < 3) {
                    return true;
                }
                String url = params[1];
                StringBuilder alertUrl = new StringBuilder();
                for (i = 2; i < params.length; ++i) {
                    alertUrl.append(params[i]);
                    alertUrl.append(" ");
                }
                alertUrl.append("\r\r-<b>").append(habbo.getHabboInfo().getUsername()).append("</b>");
                room = habbo.getHabboInfo().getCurrentRoom();
                if (room == null) break;
                room.sendComposer(new StaffAlertWithLinkComposer(alertUrl.toString(), url).compose());
                return true;
            }
        }
        return true;
    }

    @Override
    public boolean saveData(WiredSettings settings, GameClient gameClient) throws WiredSaveException {
        String message = settings.getStringParam();
        String[] params = message.split(" ");
        String[] params2 = message.split(":");
        if (gameClient.getHabbo() == null || !gameClient.getHabbo().hasPermission(Permission.ACC_SUPERWIRED)) {
            message = Emulator.getGameEnvironment().getWordFilter().filter(message, null);
            message = message.substring(0, Math.min(message.length(), Emulator.getConfig().getInt("hotel.wired.message.max_length", 100)));
        }
        switch (params2[0]) {
            case "roomitem": {
                if (gameClient.getHabbo().hasPermission("cmd_roomitem")) break;
                gameClient.getHabbo().whisper(Emulator.getTexts().getValue("wired.error.superwired"), RoomChatMessageBubbles.ALERT);
                return false;
            }
            case "roomenable": {
                if (gameClient.getHabbo().hasPermission("cmd_roomeffect")) break;
                gameClient.getHabbo().whisper(Emulator.getTexts().getValue("wired.error.superwired"), RoomChatMessageBubbles.ALERT);
                return false;
            }
            case "roomdance": {
                if (gameClient.getHabbo().hasPermission("cmd_danceall")) break;
                gameClient.getHabbo().whisper(Emulator.getTexts().getValue("wired.error.superwired"), RoomChatMessageBubbles.ALERT);
                return false;
            }
            case "moonwalk": {
                if (gameClient.getHabbo().hasPermission("cmd_moonwalk")) break;
                gameClient.getHabbo().whisper(Emulator.getTexts().getValue("wired.error.superwired"), RoomChatMessageBubbles.ALERT);
                return false;
            }
            case "kick": {
                if (gameClient.getHabbo().hasPermission("cmd_softkick")) break;
                gameClient.getHabbo().whisper(Emulator.getTexts().getValue("wired.error.superwired"), RoomChatMessageBubbles.ALERT);
                return false;
            }
            case "superkick": {
                if (gameClient.getHabbo().hasPermission("cmd_alert")) break;
                gameClient.getHabbo().whisper(Emulator.getTexts().getValue("wired.error.superwired"), RoomChatMessageBubbles.ALERT);
                return false;
            }
            case "badge": {
                if (gameClient.getHabbo().hasPermission("cmd_badge")) break;
                gameClient.getHabbo().whisper(Emulator.getTexts().getValue("wired.error.superwired"), RoomChatMessageBubbles.ALERT);
                return false;
            }
            case "removebadge": {
                if (gameClient.getHabbo().hasPermission("cmd_take_badge")) break;
                gameClient.getHabbo().whisper(Emulator.getTexts().getValue("wired.error.superwired"), RoomChatMessageBubbles.ALERT);
                return false;
            }
            case "fastwalk": {
                if (gameClient.getHabbo().hasPermission("cmd_fastwalk")) break;
                gameClient.getHabbo().whisper(Emulator.getTexts().getValue("wired.error.superwired"), RoomChatMessageBubbles.ALERT);
                return false;
            }
            case "rank": {
                if (gameClient.getHabbo().hasPermission("cmd_giverank")) break;
                gameClient.getHabbo().whisper(Emulator.getTexts().getValue("wired.error.superwired"), RoomChatMessageBubbles.ALERT);
                return false;
            }
            case "giveitem": {
                if (gameClient.getHabbo().hasPermission("cmd_ban")) break;
                gameClient.getHabbo().whisper(Emulator.getTexts().getValue("wired.error.superwired"), RoomChatMessageBubbles.ALERT);
                return false;
            }
        }
        switch (params[0]) {
            case "roomalert": {
                if (gameClient.getHabbo().hasPermission("cmd_roomalert")) break;
                gameClient.getHabbo().whisper(Emulator.getTexts().getValue("wired.error.superwired"), RoomChatMessageBubbles.ALERT);
                return false;
            }
            case "alert": {
                if (gameClient.getHabbo().hasPermission("cmd_alert")) break;
                gameClient.getHabbo().whisper(Emulator.getTexts().getValue("wired.error.superwired"), RoomChatMessageBubbles.ALERT);
                return false;
            }
            case "roomalerturl": {
                if (gameClient.getHabbo().hasPermission("cmd_hal")) break;
                gameClient.getHabbo().whisper(Emulator.getTexts().getValue("wired.error.superwired"), RoomChatMessageBubbles.ALERT);
                return false;
            }
        }
        int delay = settings.getDelay();
        if (delay > Emulator.getConfig().getInt("hotel.wired.max_delay", 20)) {
            throw new WiredSaveException("Delay too long");
        }
        this.message = message;
        this.setDelay(delay);
        return true;
    }

    @Override
    public void serializeWiredData(ServerMessage message, Room room) {
        message.appendBoolean(false);
        message.appendInt(0);
        message.appendInt(0);
        message.appendInt(this.getBaseItem().getSpriteId());
        message.appendInt(this.getId());
        message.appendString(this.message);
        message.appendInt(0);
        message.appendInt(0);
        message.appendInt(WiredEffectSuperWired.type.code);
        message.appendInt(this.getDelay());
        if (this.requiresTriggeringUser()) {
            ArrayList invalidTriggers = new ArrayList();
            room.getRoomSpecialTypes().getTriggers(this.getX(), this.getY()).forEach(object -> {
                if (!object.isTriggeredByRoomUnit()) {
                    invalidTriggers.add(object.getBaseItem().getSpriteId());
                }
                return true;
            });
            message.appendInt(invalidTriggers.size());
            for (Integer i : invalidTriggers) {
                message.appendInt(i);
            }
        } else {
            message.appendInt(0);
        }
    }
}

