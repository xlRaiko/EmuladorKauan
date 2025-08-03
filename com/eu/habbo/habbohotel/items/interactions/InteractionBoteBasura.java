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
import java.util.concurrent.ThreadLocalRandom;

public class InteractionBoteBasura
extends HabboItem {
    private THashMap<String, Integer> databasurero;
    private final Object lock = new Object();

    public InteractionBoteBasura(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
        this.databasurero = new THashMap();
    }

    public InteractionBoteBasura(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
        this.databasurero = new THashMap();
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

    public static int getRandomInt(int min, int max) {
        return ThreadLocalRandom.current().nextInt(max - min + 1) + min;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void onClick(GameClient client, Room room, Object[] objects) throws Exception {
        if (client == null) {
            return;
        }
        super.onClick(client, room, objects);
        Object object = this.lock;
        synchronized (object) {
            if (this.getRoomId() == 0) {
                return;
            }
            if (this.userRequiredToBeAdjacent() && client.getHabbo().getRoomUnit().getCurrentLocation().distance(room.getLayout().getTile(this.getX(), this.getY())) > 1.5) {
                client.getHabbo().getRoomUnit().setGoalLocation(room.getLayout().getTileInFront(room.getLayout().getTile(this.getX(), this.getY()), Rotation.Calculate(client.getHabbo().getRoomUnit().getX(), client.getHabbo().getRoomUnit().getY(), this.getX(), this.getY())));
                client.getHabbo().whisper("Est\u00e1s muy lejos para abrir el bote de basura... acercate un poco m\u00e1s");
                return;
            }
            this.setExtradata("1");
            room.updateItemState(this);
            if (this.getExtradata().equals("0") && this.databasurero.containsKey("cooldown") && this.databasurero.get("cooldown") > Emulator.getIntUnixTimestamp()) {
                client.getHabbo().whisper("La basura est\u00e1 vac\u00eda");
                return;
            }
            if (this.getExtradata().equals("1") && this.databasurero.containsKey("cooldown") && this.databasurero.get("cooldown") > Emulator.getIntUnixTimestamp()) {
                try {
                    client.getHabbo().shout("* " + client.getHabbo().getHabboInfo().getUsername() + " Empieza a buscar en el basurero! *");
                    client.getHabbo().getRoomUnit().setCanWalk(false);
                    int random = InteractionBoteBasura.getRandomInt(1, 3);
                    System.out.println(random);
                    if (random == 1) {
                        int creditos = InteractionBoteBasura.getRandomInt(1, 10);
                        Emulator.getThreading().run(() -> {
                            client.getHabbo().shout("* " + client.getHabbo().getHabboInfo().getUsername() + " saca del basurero un total de [" + creditos + "] cr*");
                            client.getHabbo().giveCredits(creditos);
                            client.getHabbo().getRoomUnit().setCanWalk(true);
                            this.databasurero.put("cooldown", Emulator.getIntUnixTimestamp() + Emulator.getConfig().getInt("bote_de_basura_cooldown"));
                            this.setExtradata("0");
                            room.updateItemState(this);
                        }, 8000L);
                    } else if (random == 2) {
                        int diamantes = InteractionBoteBasura.getRandomInt(1, 3);
                        Emulator.getThreading().run(() -> {
                            client.getHabbo().shout("* " + client.getHabbo().getHabboInfo().getUsername() + " saca del basurero un total de [" + diamantes + "] diamantes! *");
                            client.getHabbo().givePoints(5, diamantes);
                            client.getHabbo().getRoomUnit().setCanWalk(true);
                            this.databasurero.put("cooldown", Emulator.getIntUnixTimestamp() + Emulator.getConfig().getInt("bote_de_basura_cooldown"));
                            this.setExtradata("0");
                            room.updateItemState(this);
                        }, 8000L);
                    } else {
                        Emulator.getThreading().run(() -> {
                            client.getHabbo().shout("* " + client.getHabbo().getHabboInfo().getUsername() + " saca del basurero un furni!");
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
                            this.databasurero.put("cooldown", Emulator.getIntUnixTimestamp() + Emulator.getConfig().getInt("bote_de_basura_cooldown"));
                            this.setExtradata("1");
                            room.updateItemState(this);
                        }, 10000L);
                    }
                }
                catch (Exception e) {
                    client.getHabbo().whisper("* Ocurrio un error, intentalo de nuevo! *");
                }
            }
        }
    }

    @Override
    public void onWalk(RoomUnit roomUnit, Room room, Object[] objects) throws Exception {
    }

    @Override
    public void onWalkOn(RoomUnit client, Room room, Object[] objects) throws Exception {
    }

    @Override
    public void onWalkOff(RoomUnit client, Room room, Object[] objects) throws Exception {
    }

    public boolean allowAnyone() {
        return true;
    }

    protected boolean placeInRoom() {
        return true;
    }

    public boolean resetable() {
        return false;
    }

    public boolean userRequiredToBeAdjacent() {
        return true;
    }

    @Override
    public boolean isUsable() {
        return false;
    }
}

