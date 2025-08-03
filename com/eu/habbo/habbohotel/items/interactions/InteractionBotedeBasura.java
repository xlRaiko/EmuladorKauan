/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.inventory.AddHabboItemComposer;
import com.eu.habbo.messages.outgoing.inventory.InventoryRefreshComposer;
import com.eu.habbo.util.pathfinding.Rotation;
import gnu.trove.map.hash.THashMap;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionBotedeBasura
extends HabboItem {
    private final THashMap<String, String> databasurero = new THashMap();

    public InteractionBotedeBasura(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public InteractionBotedeBasura(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void serializeExtradata(ServerMessage serverMessage) {
        if (this.getExtradata().length() == 0) {
            this.setExtradata("0");
        }
        serverMessage.appendInt(7 + (this.isLimited() ? 256 : 0));
        serverMessage.appendString("" + Emulator.getGameEnvironment().getItemManager().calculateCrackState(Integer.parseInt(this.getExtradata()), Emulator.getGameEnvironment().getItemManager().getCrackableCount(this.getBaseItem().getId()), this.getBaseItem()));
        serverMessage.appendInt(Integer.valueOf(this.getExtradata()));
        serverMessage.appendInt(Emulator.getGameEnvironment().getItemManager().getCrackableCount(this.getBaseItem().getId()));
        super.serializeExtradata(serverMessage);
    }

    @Override
    public boolean canWalkOn(RoomUnit roomUnit, Room room, Object[] objects) {
        return false;
    }

    @Override
    public boolean isWalkable() {
        return false;
    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) throws Exception {
        if (client == null) {
            return;
        }
        if (this.getRoomId() == 0) {
            return;
        }
        if (client.getHabbo().getHabboStats().cache.get("var_basura") != null) {
            return;
        }
        super.onClick(client, room, objects);
        if (client.getHabbo().getRoomUnit().getCurrentLocation().distance(room.getLayout().getTile(this.getX(), this.getY())) > 1.5) {
            client.getHabbo().getRoomUnit().setGoalLocation(room.getLayout().getTileInFront(room.getLayout().getTile(this.getX(), this.getY()), Rotation.Calculate(client.getHabbo().getRoomUnit().getX(), client.getHabbo().getRoomUnit().getY(), this.getX(), this.getY())));
            client.getHabbo().whisper("Est\u00e1s muy lejos para abrir el bote de basura... acercate un poco m\u00e1s");
            return;
        }
        if (this.databasurero.get("cooldown") != null) {
            client.getHabbo().getHabboStats().cache.put("var_basura", "true");
            client.getHabbo().whisper("* " + client.getHabbo().getHabboInfo().getUsername() + " Empieza a buscar en el basurero! *");
            Emulator.getThreading().run(() -> {
                client.getHabbo().whisper("* " + client.getHabbo().getHabboInfo().getUsername() + " busco en la basura pero no encuentra nada. *");
                client.getHabbo().getHabboStats().cache.remove("var_basura");
            }, 5000L);
        } else {
            try {
                this.databasurero.put("cooldown", "true");
                client.getHabbo().getHabboStats().cache.put("var_basura", "true");
                client.getHabbo().shout("* " + client.getHabbo().getHabboInfo().getUsername() + " Empieza a buscar en el basurero! *");
                client.getHabbo().getRoomUnit().setCanWalk(false);
                int random = Emulator.getRandom().nextInt(4) + 1;
                if (random == 1) {
                    int creditos = Emulator.getRandom().nextInt(10) + 1;
                    Emulator.getThreading().run(() -> {
                        client.getHabbo().shout("* " + client.getHabbo().getHabboInfo().getUsername() + " busco en la basura y encontro " + creditos + " creditos *");
                        client.getHabbo().giveCredits(creditos);
                        client.getHabbo().getRoomUnit().setCanWalk(true);
                        client.getHabbo().getHabboStats().cache.remove("var_basura");
                    }, 8000L);
                } else if (random == 2) {
                    int diamantes = Emulator.getRandom().nextInt(3) + 1;
                    Emulator.getThreading().run(() -> {
                        client.getHabbo().shout("* " + client.getHabbo().getHabboInfo().getUsername() + " busco en la basura y encontro " + diamantes + " diamantes *");
                        client.getHabbo().givePoints(5, diamantes);
                        client.getHabbo().getRoomUnit().setCanWalk(true);
                        client.getHabbo().getHabboStats().cache.remove("var_basura");
                    }, 8000L);
                } else {
                    Emulator.getThreading().run(() -> {
                        client.getHabbo().shout("* " + client.getHabbo().getHabboInfo().getUsername() + " busco en la basura y encontro un furni *");
                        int itemId = Emulator.getConfig().getInt("bote_de_basura_itemid");
                        if (itemId > 0 && Emulator.getConfig().getValue("bote_de_basura_itemid") != null) {
                            HabboItem newItem;
                            Item item = Emulator.getGameEnvironment().getItemManager().getItem(itemId);
                            if (item != null && (newItem = Emulator.getGameEnvironment().getItemManager().createItem(client.getHabbo().getHabboInfo().getId(), item, 0, 0, "")) != null) {
                                client.getHabbo().getInventory().getItemsComponent().addItem(newItem);
                                client.getHabbo().getClient().sendResponse(new AddHabboItemComposer(newItem));
                                client.getHabbo().getClient().sendResponse(new InventoryRefreshComposer());
                                client.getHabbo().whisper("Revisa tu inventario ;)");
                            }
                        } else {
                            System.out.println("Catch en Bote de Basura!, no fue posible darle el furni ya que no existe ese furni o la id es negativa!");
                        }
                        client.getHabbo().getRoomUnit().setCanWalk(true);
                        client.getHabbo().getHabboStats().cache.remove("var_basura");
                    }, 10000L);
                }
            }
            catch (Exception e) {
                client.getHabbo().whisper("* Ocurrio un error al abrirlo, intentalo de nuevo! *");
            }
            Emulator.getThreading().run(() -> this.databasurero.remove("cooldown"), (long)Emulator.getConfig().getInt("bote_de_basura_cooldown") * 1000L);
        }
    }

    @Override
    public void onWalk(RoomUnit roomUnit, Room room, Object[] objects) {
    }

    @Override
    public void onWalkOn(RoomUnit client, Room room, Object[] objects) {
    }

    @Override
    public void onWalkOff(RoomUnit client, Room room, Object[] objects) {
    }

    @Override
    public boolean isUsable() {
        return false;
    }
}

