/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.games.Game;
import com.eu.habbo.habbohotel.games.GameTeamColors;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.items.interactions.wired.WiredSettings;
import com.eu.habbo.habbohotel.items.interactions.wired.extra.WiredAddonNoAnimationUnit;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomTileState;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserEffectComposer;
import com.eu.habbo.threading.runnables.RoomUnitTeleport;
import com.eu.habbo.threading.runnables.SendRoomUnitEffectComposer;
import gnu.trove.set.hash.THashSet;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WiredEffectTeleportYellow
extends InteractionWiredEffect {
    public static final WiredEffectType type = WiredEffectType.TELEPORT;
    private static final GameTeamColors teamColor = GameTeamColors.YELLOW;
    protected List<HabboItem> items = new ArrayList<HabboItem>();

    public WiredEffectTeleportYellow(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredEffectTeleportYellow(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    public static void teleportUnitToTile(Room room, RoomTile tile, boolean isAnimation) {
        if (tile == null || room == null) {
            return;
        }
        ArrayList<Habbo> habbos = new ArrayList<Habbo>();
        for (Habbo habbo : room.getHabbos()) {
            Game game;
            if (habbo.getHabboInfo().getCurrentGame() == null || (game = room.getGame(habbo.getHabboInfo().getCurrentGame())) == null || !game.getTeamForHabbo((Habbo)habbo).teamColor.equals((Object)teamColor) || habbo.getRoomUnit().isWiredTeleporting) continue;
            habbo.getRoomUnit().getRoom().unIdle(habbo.getRoomUnit().getRoom().getHabbo(habbo.getRoomUnit()));
            if (!isAnimation) {
                room.sendComposer(new RoomUserEffectComposer(habbo.getRoomUnit(), 4).compose());
                Emulator.getThreading().run(new SendRoomUnitEffectComposer(room, habbo.getRoomUnit()), WiredHandler.TELEPORT_DELAY + 1000);
            }
            habbos.add(habbo);
        }
        if (tile.state == RoomTileState.INVALID || tile.state == RoomTileState.BLOCKED) {
            RoomTile alternativeTile = null;
            List<RoomTile> optionalTiles = room.getLayout().getTilesAround(tile);
            Collections.reverse(optionalTiles);
            for (RoomTile optionalTile : optionalTiles) {
                if (optionalTile.state == RoomTileState.INVALID || optionalTile.state == RoomTileState.BLOCKED) continue;
                alternativeTile = optionalTile;
                break;
            }
            if (alternativeTile != null) {
                tile = alternativeTile;
            }
        }
        for (Habbo habbo : habbos) {
            Emulator.getThreading().run(new RoomUnitTeleport(habbo.getRoomUnit(), room, tile.x, tile.y, tile.getStackHeight() + (tile.state == RoomTileState.SIT ? -0.5 : 0.0), habbo.getRoomUnit().getEffectId()), WiredHandler.TELEPORT_DELAY);
        }
    }

    @Override
    public void serializeWiredData(ServerMessage message, Room room) {
        THashSet<HabboItem> items = new THashSet<HabboItem>();
        for (HabboItem item : this.items) {
            if (item.getRoomId() == this.getRoomId() && Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId()).getHabboItem(item.getId()) != null) continue;
            items.add(item);
        }
        for (HabboItem item : items) {
            this.items.remove(item);
        }
        message.appendBoolean(Boolean.FALSE);
        message.appendInt(WiredHandler.MAXIMUM_FURNI_SELECTION);
        message.appendInt(this.items.size());
        for (HabboItem item : this.items) {
            message.appendInt(item.getId());
        }
        message.appendInt(this.getBaseItem().getSpriteId());
        message.appendInt(this.getId());
        message.appendString("");
        message.appendInt(0);
        message.appendInt(0);
        message.appendInt(this.getType().code);
        message.appendInt(this.getDelay());
        if (this.requiresTriggeringUser()) {
            ArrayList invalidTriggers = new ArrayList();
            room.getRoomSpecialTypes().getTriggers(this.getX(), this.getY()).forEach(object -> {
                if (!object.isTriggeredByRoomUnit()) {
                    invalidTriggers.add(object.getId());
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

    @Override
    public boolean saveData(WiredSettings settings, GameClient gameClient) {
        this.items.clear();
        int count = settings.getFurniIds().length;
        for (int i = 0; i < count; ++i) {
            this.items.add(Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId()).getHabboItem(settings.getFurniIds()[i]));
        }
        this.setDelay(settings.getDelay());
        return true;
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        this.items.removeIf(item -> item == null || item.getRoomId() != this.getRoomId() || Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId()).getHabboItem(item.getId()) == null);
        if (!this.items.isEmpty()) {
            int i = Emulator.getRandom().nextInt(this.items.size());
            HabboItem item2 = this.items.get(i);
            boolean isNotAnimation = room.getRoomSpecialTypes().hasExtraType(this.getX(), this.getY(), WiredAddonNoAnimationUnit.class);
            WiredEffectTeleportYellow.teleportUnitToTile(room, room.getLayout().getTile(item2.getX(), item2.getY()), isNotAnimation);
            return true;
        }
        return false;
    }

    @Override
    public String getWiredData() {
        StringBuilder wiredData = new StringBuilder(this.getDelay() + "\t");
        if (this.items != null && !this.items.isEmpty()) {
            for (HabboItem item : this.items) {
                wiredData.append(item.getId()).append(";");
            }
        }
        return wiredData.toString();
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException {
        this.items = new ArrayList<HabboItem>();
        String[] wiredData = set.getString("wired_data").split("\t");
        if (wiredData.length >= 1) {
            this.setDelay(Integer.parseInt(wiredData[0]));
        }
        if (wiredData.length == 2 && wiredData[1].contains(";")) {
            for (String s : wiredData[1].split(";")) {
                HabboItem item = room.getHabboItem(Integer.parseInt(s));
                if (item == null) continue;
                this.items.add(item);
            }
        }
    }

    @Override
    public void onPickUp() {
        this.items.clear();
        this.setDelay(0);
    }

    @Override
    public WiredEffectType getType() {
        return type;
    }

    @Override
    public boolean requiresTriggeringUser() {
        return false;
    }

    @Override
    protected long requiredCooldown() {
        return 50L;
    }
}

