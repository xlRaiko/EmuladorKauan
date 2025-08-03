/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionCustomValues;
import com.eu.habbo.habbohotel.items.interactions.InteractionRoller;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.items.interactions.wired.WiredSettings;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomTileState;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.customs.AdsBackground;
import com.eu.habbo.habbohotel.wired.customs.WiredMatchFurniSetting;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.incoming.wired.WiredSaveException;
import com.eu.habbo.messages.outgoing.rooms.items.FloorItemOnRollerComposer;
import com.eu.habbo.messages.outgoing.rooms.items.FloorItemUpdateComposer;
import gnu.trove.set.hash.THashSet;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class WiredEffectMatchFurni
extends InteractionWiredEffect {
    private static final WiredEffectType type = WiredEffectType.MATCH_SSHOT;
    private THashSet<WiredMatchFurniSetting> settings = new THashSet(0);
    private boolean state = false;
    private boolean direction = false;
    private boolean position = false;
    public boolean checkForWiredResetPermission = true;

    public WiredEffectMatchFurni(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredEffectMatchFurni(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        THashSet<RoomTile> tilesToUpdate = new THashSet<RoomTile>(this.settings.size());
        for (WiredMatchFurniSetting setting : this.settings) {
            RoomTile t;
            HabboItem item = room.getHabboItem(setting.itemId);
            if (item == null) continue;
            if (item.getBaseItem().getInteractionType().getName().contains("ads_bg")) {
                String obtener = AdsBackground.adsObtenerWired(this.getId());
                String extraData = item.getExtradata();
                String[] extraDataP = extraData.split(";");
                String[] extraFinal = new String[5];
                for (int i = 0; i < extraDataP.length; ++i) {
                    String[] iguales = extraDataP[i].split("=");
                    extraFinal[i] = iguales.length <= 1 ? "" : iguales[1];
                }
                ((InteractionCustomValues)item).values.put("imageUrl", obtener);
                ((InteractionCustomValues)item).values.put("clickUrl", extraFinal[3]);
                ((InteractionCustomValues)item).values.put("offsetX", extraFinal[2]);
                ((InteractionCustomValues)item).values.put("offsetY", extraFinal[1]);
                ((InteractionCustomValues)item).values.put("offsetZ", extraFinal[0]);
                item.setExtradata(((InteractionCustomValues)item).toExtraData());
                item.needsUpdate(true);
                Emulator.getThreading().run(item);
                room.updateItem(item);
            }
            if (this.state && this.checkForWiredResetPermission && item.allowWiredResetState() && !setting.state.equals(" ")) {
                item.setExtradata(setting.state);
                tilesToUpdate.addAll(room.getLayout().getTilesAt(room.getLayout().getTile(item.getX(), item.getY()), item.getBaseItem().getWidth(), item.getBaseItem().getLength(), item.getRotation()));
            }
            int oldRotation = item.getRotation();
            boolean slideAnimation = !this.direction;
            room.sendComposer(new FloorItemUpdateComposer(item).compose());
            if (this.position && (t = room.getLayout().getTile((short)setting.x, (short)setting.y)) != null && t.state != RoomTileState.INVALID) {
                boolean canMove = true;
                if (!room.hasHabbosAt(t.x, t.y) || item.isWalkable() || item.getBaseItem().allowSit()) {
                    THashSet<RoomTile> tiles = room.getLayout().getTilesAt(t, item.getBaseItem().getWidth(), item.getBaseItem().getLength(), setting.rotation);
                    double highestZ = setting.z;
                    for (RoomTile tile : tiles) {
                        double stackHeight;
                        if (tile.state == RoomTileState.INVALID) {
                            highestZ = -1.0;
                            break;
                        }
                        if (item instanceof InteractionRoller && room.hasItemsAt(tile.x, tile.y)) {
                            highestZ = -1.0;
                            break;
                        }
                        if (setting.useZ || !((stackHeight = room.getStackHeight(tile.x, tile.y, false, item)) > highestZ)) continue;
                        highestZ = stackHeight;
                    }
                    if (highestZ != -1.0) {
                        tilesToUpdate.addAll(tiles);
                        double offsetZ = highestZ - item.getZ();
                        tilesToUpdate.addAll(room.getLayout().getTilesAt(room.getLayout().getTile(item.getX(), item.getY()), item.getBaseItem().getWidth(), item.getBaseItem().getLength(), oldRotation));
                        if (!slideAnimation) {
                            item.setX(t.x);
                            item.setY(t.y);
                            if (setting.useZ) {
                                item.setZ(setting.z);
                            }
                            room.sendComposer(new FloorItemUpdateComposer(item).compose());
                        } else {
                            room.sendComposer(new FloorItemOnRollerComposer(item, null, t, offsetZ, room).compose());
                        }
                        if (room.hasHabbosAt(t.x, t.y)) {
                            THashSet<Habbo> habbos = room.getHabbosAt(t.x, t.y);
                            for (Habbo habbo : habbos) {
                                try {
                                    item.onWalkOn(habbo.getRoomUnit(), room, null);
                                }
                                catch (Exception exception) {}
                            }
                        }
                    }
                }
            }
            if (this.direction) {
                item.setRotation(setting.rotation);
                room.sendComposer(new FloorItemUpdateComposer(item).compose());
            }
            item.needsUpdate(true);
        }
        room.updateTiles(tilesToUpdate);
        return true;
    }

    @Override
    public String getWiredData() {
        this.refresh();
        StringBuilder data = new StringBuilder(this.settings.size() + ":");
        if (this.settings.isEmpty()) {
            data.append(";");
        } else {
            Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId());
            for (WiredMatchFurniSetting item : this.settings) {
                HabboItem i;
                if (room == null || (i = room.getHabboItem(item.itemId)) == null) continue;
                data.append(item.toString(this.checkForWiredResetPermission && i.allowWiredResetState())).append(";");
            }
        }
        data.append(":").append(this.state ? 1 : 0).append(":").append(this.direction ? 1 : 0).append(":").append(this.position ? 1 : 0).append(":").append(this.getDelay());
        return data.toString();
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException {
        String[] items;
        String[] data = set.getString("wired_data").split(":");
        int itemCount = Integer.parseInt(data[0]);
        for (String item : items = data[1].split(";")) {
            try {
                String[] stuff = item.split("-");
                if (stuff.length >= 7) {
                    this.settings.add(new WiredMatchFurniSetting(Integer.parseInt(stuff[0]), stuff[1], Integer.parseInt(stuff[2]), Integer.parseInt(stuff[3]), Integer.parseInt(stuff[4]), Double.parseDouble(stuff[5] + "-" + stuff[6])));
                    continue;
                }
                if (stuff.length >= 6) {
                    this.settings.add(new WiredMatchFurniSetting(Integer.parseInt(stuff[0]), stuff[1], Integer.parseInt(stuff[2]), Integer.parseInt(stuff[3]), Integer.parseInt(stuff[4]), Double.parseDouble(stuff[5])));
                    continue;
                }
                if (stuff.length < 5) continue;
                this.settings.add(new WiredMatchFurniSetting(Integer.parseInt(stuff[0]), stuff[1], Integer.parseInt(stuff[2]), Integer.parseInt(stuff[3]), Integer.parseInt(stuff[4])));
            }
            catch (Exception e) {
                Emulator.getLogging().logErrorLine(e);
            }
        }
        this.state = data[2].equals("1");
        this.direction = data[3].equals("1");
        this.position = data[4].equals("1");
        this.setDelay(Integer.parseInt(data[5]));
    }

    @Override
    public void onPickUp() {
        this.settings.clear();
        this.state = false;
        this.direction = false;
        this.position = false;
        this.setDelay(0);
    }

    @Override
    public WiredEffectType getType() {
        return type;
    }

    @Override
    public void serializeWiredData(ServerMessage message, Room room) {
        this.refresh();
        message.appendBoolean(false);
        message.appendInt(WiredHandler.MAXIMUM_FURNI_SELECTION);
        message.appendInt(this.settings.size());
        for (WiredMatchFurniSetting item : this.settings) {
            message.appendInt(item.itemId);
        }
        message.appendInt(this.getBaseItem().getSpriteId());
        message.appendInt(this.getId());
        message.appendString("");
        message.appendInt(3);
        message.appendInt(this.state ? 1 : 0);
        message.appendInt(this.direction ? 1 : 0);
        message.appendInt(this.position ? 1 : 0);
        message.appendInt(0);
        message.appendInt(this.getType().code);
        message.appendInt(this.getDelay());
        message.appendInt(0);
    }

    @Override
    public boolean saveData(WiredSettings settings, GameClient gameClient) throws WiredSaveException {
        if (settings.getIntParams().length < 3) {
            throw new WiredSaveException("Invalid data");
        }
        boolean setState = settings.getIntParams()[0] == 1;
        boolean setDirection = settings.getIntParams()[1] == 1;
        boolean setPosition = settings.getIntParams()[2] == 1;
        Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId());
        if (room == null) {
            throw new WiredSaveException("Trying to save wired in unloaded room");
        }
        int itemsCount = settings.getFurniIds().length;
        if (itemsCount > Emulator.getConfig().getInt("hotel.wired.furni.selection.count")) {
            throw new WiredSaveException("Too many furni selected");
        }
        ArrayList<WiredMatchFurniSetting> newSettings = new ArrayList<WiredMatchFurniSetting>();
        for (int i = 0; i < itemsCount; ++i) {
            int itemId = settings.getFurniIds()[i];
            HabboItem it = Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId()).getHabboItem(itemId);
            if (it == null) {
                throw new WiredSaveException(String.format("Item %s not found", itemId));
            }
            newSettings.add(new WiredMatchFurniSetting(it.getId(), this.checkForWiredResetPermission && it.allowWiredResetState() ? it.getExtradata() : " ", it.getRotation(), it.getX(), it.getY()));
        }
        int delay = settings.getDelay();
        if (delay > Emulator.getConfig().getInt("hotel.wired.max_delay", 20)) {
            throw new WiredSaveException("Delay too long");
        }
        this.state = setState;
        this.direction = setDirection;
        this.position = setPosition;
        this.settings.clear();
        this.settings.addAll(newSettings);
        this.setDelay(delay);
        return true;
    }

    private void refresh() {
        Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId());
        if (room != null && room.isLoaded()) {
            THashSet<WiredMatchFurniSetting> remove = new THashSet<WiredMatchFurniSetting>();
            for (WiredMatchFurniSetting setting : this.settings) {
                HabboItem item = room.getHabboItem(setting.itemId);
                if (item != null) continue;
                remove.add(setting);
            }
            for (WiredMatchFurniSetting setting : remove) {
                this.settings.remove(setting);
            }
        }
    }
}

