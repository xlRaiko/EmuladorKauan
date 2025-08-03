/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.items.interactions.wired.WiredSettings;
import com.eu.habbo.habbohotel.items.interactions.wired.extra.WiredAddonNoAnimation;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.incoming.wired.WiredSaveException;
import com.eu.habbo.messages.outgoing.rooms.items.FloorItemOnRollerComposer;
import com.eu.habbo.messages.outgoing.rooms.items.FloorItemUpdateComposer;
import gnu.trove.set.hash.THashSet;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WiredEffectMoveFurniTo
extends InteractionWiredEffect {
    public static final WiredEffectType type = WiredEffectType.MOVE_FURNI_TO;
    private final List<HabboItem> items = new ArrayList<HabboItem>();
    private int direction;
    private int spacing = 1;
    private Map<Integer, Integer> indexOffset = new LinkedHashMap<Integer, Integer>();

    public WiredEffectMoveFurniTo(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredEffectMoveFurniTo(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean saveData(WiredSettings settings, GameClient gameClient) throws WiredSaveException {
        Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId());
        if (room == null) {
            return false;
        }
        this.items.clear();
        this.indexOffset.clear();
        if (settings.getIntParams().length < 2) {
            throw new WiredSaveException("invalid data");
        }
        this.direction = settings.getIntParams()[0];
        this.spacing = settings.getIntParams()[1];
        int count = settings.getFurniIds().length;
        for (int i = 0; i < count; ++i) {
            this.items.add(room.getHabboItem(settings.getFurniIds()[i]));
        }
        this.setDelay(settings.getDelay());
        return true;
    }

    @Override
    public WiredEffectType getType() {
        return type;
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        ArrayList<HabboItem> items = new ArrayList<HabboItem>();
        for (HabboItem item : this.items) {
            if (Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId()).getHabboItem(item.getId()) != null) continue;
            items.add(item);
        }
        for (HabboItem item : items) {
            this.items.remove(item);
        }
        if (this.items.isEmpty()) {
            return false;
        }
        boolean isAddon = room.getRoomSpecialTypes().hasExtraType(this.getX(), this.getY(), WiredAddonNoAnimation.class);
        if (stuff != null && stuff.length > 0) {
            for (Object object : stuff) {
                HabboItem targetItem;
                if (!(object instanceof HabboItem) || (targetItem = this.items.get(Emulator.getRandom().nextInt(this.items.size()))) == null) continue;
                int indexOffset = 0;
                if (!this.indexOffset.containsKey(targetItem.getId())) {
                    this.indexOffset.put(targetItem.getId(), indexOffset);
                } else {
                    indexOffset = this.indexOffset.get(targetItem.getId()) + this.spacing;
                }
                RoomTile objectTile = room.getLayout().getTile(targetItem.getX(), targetItem.getY());
                if (objectTile == null) continue;
                THashSet<RoomTile> refreshTiles = room.getLayout().getTilesAt(room.getLayout().getTile(((HabboItem)object).getX(), ((HabboItem)object).getY()), ((HabboItem)object).getBaseItem().getWidth(), ((HabboItem)object).getBaseItem().getLength(), ((HabboItem)object).getRotation());
                RoomTile tile = room.getLayout().getTileInFront(objectTile, this.direction, indexOffset);
                if (tile == null || !tile.getAllowStack()) {
                    indexOffset = 0;
                    tile = room.getLayout().getTileInFront(objectTile, this.direction, indexOffset);
                }
                if (!isAddon) {
                    room.sendComposer(new FloorItemOnRollerComposer((HabboItem)object, null, tile, tile.getStackHeight() - ((HabboItem)object).getZ(), room).compose());
                } else {
                    RoomTile a = room.getLayout().getTile(((HabboItem)object).getX(), ((HabboItem)object).getY());
                    ((HabboItem)object).onMove(room, room.getLayout().getTile(((HabboItem)object).getX(), ((HabboItem)object).getY()), a);
                    ((HabboItem)object).setX(a.x);
                    ((HabboItem)object).setY(a.y);
                    ((HabboItem)object).setZ(tile.getStackHeight() - ((HabboItem)object).getZ());
                    ((HabboItem)object).needsUpdate(true);
                    room.sendComposer(new FloorItemUpdateComposer((HabboItem)object).compose());
                }
                refreshTiles.addAll(room.getLayout().getTilesAt(room.getLayout().getTile(((HabboItem)object).getX(), ((HabboItem)object).getY()), ((HabboItem)object).getBaseItem().getWidth(), ((HabboItem)object).getBaseItem().getLength(), ((HabboItem)object).getRotation()));
                room.updateTiles(refreshTiles);
                this.indexOffset.put(targetItem.getId(), indexOffset);
            }
        }
        return true;
    }

    @Override
    public String getWiredData() {
        THashSet<HabboItem> itemsToRemove = new THashSet<HabboItem>();
        for (HabboItem item : this.items) {
            if (item.getRoomId() == this.getRoomId() && Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId()).getHabboItem(item.getId()) != null) continue;
            itemsToRemove.add(item);
        }
        for (HabboItem item : itemsToRemove) {
            this.items.remove(item);
        }
        return WiredHandler.getGsonBuilder().create().toJson(new JsonData(this.direction, this.spacing, this.getDelay(), this.items.stream().map(HabboItem::getId).collect(Collectors.toList())));
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
        message.appendBoolean(false);
        message.appendInt(WiredHandler.MAXIMUM_FURNI_SELECTION);
        message.appendInt(this.items.size());
        for (HabboItem item : this.items) {
            message.appendInt(item.getId());
        }
        message.appendInt(this.getBaseItem().getSpriteId());
        message.appendInt(this.getId());
        message.appendString("");
        message.appendInt(2);
        message.appendInt(this.direction);
        message.appendInt(this.spacing);
        message.appendInt(0);
        message.appendInt(this.getType().code);
        message.appendInt(this.getDelay());
        message.appendInt(0);
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException {
        block5: {
            String wiredData;
            block4: {
                this.items.clear();
                wiredData = set.getString("wired_data");
                if (!wiredData.startsWith("{")) break block4;
                JsonData data = WiredHandler.getGsonBuilder().create().fromJson(wiredData, JsonData.class);
                this.direction = data.direction;
                this.spacing = data.spacing;
                this.setDelay(data.delay);
                for (Integer id : data.itemIds) {
                    HabboItem item = room.getHabboItem(id);
                    if (item == null) continue;
                    this.items.add(item);
                }
                break block5;
            }
            String[] data = wiredData.split("\t");
            if (data.length != 4) break block5;
            try {
                this.direction = Integer.parseInt(data[0]);
                this.spacing = Integer.parseInt(data[1]);
                this.setDelay(Integer.parseInt(data[2]));
            }
            catch (Exception exception) {
                // empty catch block
            }
            for (String s : data[3].split("\r")) {
                HabboItem item = room.getHabboItem(Integer.parseInt(s));
                if (item == null) continue;
                this.items.add(item);
            }
        }
    }

    @Override
    public void onPickUp() {
        this.setDelay(0);
        this.items.clear();
        this.direction = 0;
        this.spacing = 0;
        this.indexOffset.clear();
    }

    @Override
    protected long requiredCooldown() {
        return 495L;
    }

    static class JsonData {
        int direction;
        int spacing;
        int delay;
        List<Integer> itemIds;

        public JsonData(int direction, int spacing, int delay, List<Integer> itemIds) {
            this.direction = direction;
            this.spacing = spacing;
            this.delay = delay;
            this.itemIds = itemIds;
        }
    }
}

