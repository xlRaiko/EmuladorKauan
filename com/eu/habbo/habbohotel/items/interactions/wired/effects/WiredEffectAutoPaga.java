/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.wired.WiredSettings;
import com.eu.habbo.habbohotel.items.interactions.wired.effects.WiredEffectWhisper;
import com.eu.habbo.habbohotel.items.interactions.wired.extra.WAutoPagaManager;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.incoming.wired.WiredSaveException;
import com.eu.habbo.messages.outgoing.generic.alerts.BubbleAlertComposer;
import com.eu.habbo.messages.outgoing.inventory.AddHabboItemComposer;
import com.eu.habbo.messages.outgoing.inventory.InventoryRefreshComposer;
import gnu.trove.map.hash.THashMap;
import java.sql.ResultSet;
import java.sql.SQLException;

public class WiredEffectAutoPaga
extends WiredEffectWhisper {
    public WiredEffectAutoPaga(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        Habbo habbo = room.getHabbo(roomUnit);
        String[] str = this.message.split(":");
        Object xd = "";
        if (habbo != null && str.length >= 1) {
            THashMap<String, String> keys;
            HabboItem newItem;
            Item item;
            if (WAutoPagaManager.getInstance().hasUserIsWinnerInRoom(habbo.getHabboInfo().getId(), this.getRoomId())) {
                habbo.whisper("Has cumplido el limite de victorias, espera otra hora", RoomChatMessageBubbles.WIRED);
                return false;
            }
            if (!Emulator.isNumeric(str[1])) {
                habbo.whisper("La cantidad que has puesto no es un n\u00famero", RoomChatMessageBubbles.WIRED);
                return false;
            }
            int cantidad = Integer.parseInt(str[1]);
            switch (str[0].toLowerCase()) {
                case "diamantes": 
                case "diamonds": {
                    habbo.givePoints(101, cantidad);
                    break;
                }
                case "exp": {
                    habbo.givePixels(cantidad);
                    break;
                }
                case "credits": 
                case "creditos": {
                    habbo.giveCredits(cantidad);
                    break;
                }
                case "item": 
                case "furni": {
                    if (!Emulator.isNumeric(str[1])) {
                        return false;
                    }
                    Item item2 = Emulator.getGameEnvironment().getItemManager().getItem(cantidad);
                    if (item2 != null) {
                        HabboItem newItem2 = Emulator.getGameEnvironment().getItemManager().createItem(habbo.getHabboInfo().getId(), item2, 0, 0, "");
                        if (newItem2 == null) break;
                        habbo.getInventory().getItemsComponent().addItem(newItem2);
                        habbo.getClient().sendResponse(new AddHabboItemComposer(newItem2));
                        habbo.getClient().sendResponse(new InventoryRefreshComposer());
                        THashMap<String, String> keys2 = new THashMap<String, String>();
                        keys2.put("display", "BUBBLE");
                        keys2.put("image", "${image.library.url}/dcr/icons/" + item2.getName().replaceAll("\\*", "_") + "_icon.png");
                        keys2.put("message", Emulator.getTexts().getValue("commands.generic.furni.received", "Ni un brillo pelao e osito sacalo del clan al malparido ese a tu maldita madre la van a sacar pero del estudio de porno mamagueo"));
                        habbo.getClient().sendResponse(new BubbleAlertComposer("Bubble_Furni_Received", keys2));
                        break;
                    }
                    System.out.println("Item ID Invalida");
                    break;
                }
                default: {
                    habbo.whisper("Ingresaste una moneda no aceptada!", RoomChatMessageBubbles.WIRED);
                    return false;
                }
            }
            xd = (String)xd + str[0] + ";" + cantidad + ";;";
            if (str.length >= 3) {
                int cantidad2 = Integer.parseInt(str[3]);
                xd = (String)xd + str[2] + ";" + cantidad2;
                switch (str[2].toLowerCase()) {
                    case "diamonds": {
                        habbo.givePoints(101, cantidad2);
                        break;
                    }
                    case "exp": {
                        habbo.givePixels(cantidad2);
                        break;
                    }
                    case "credits": 
                    case "creditos": {
                        habbo.giveCredits(cantidad2);
                        break;
                    }
                    case "item": 
                    case "furni": {
                        if (!Emulator.isNumeric(str[3])) {
                            return false;
                        }
                        item = Emulator.getGameEnvironment().getItemManager().getItem(cantidad2);
                        if (item != null) {
                            newItem = Emulator.getGameEnvironment().getItemManager().createItem(habbo.getHabboInfo().getId(), item, 0, 0, "");
                            if (newItem == null) break;
                            habbo.getInventory().getItemsComponent().addItem(newItem);
                            habbo.getClient().sendResponse(new AddHabboItemComposer(newItem));
                            habbo.getClient().sendResponse(new InventoryRefreshComposer());
                            keys = new THashMap<String, String>();
                            keys.put("display", "BUBBLE");
                            keys.put("image", "${image.library.url}/dcr/icons/" + item.getName().replaceAll("\\*", "_") + "_icon.png");
                            keys.put("message", Emulator.getTexts().getValue("commands.generic.furni.received", "Ni un brillo pelao e osito sacalo del clan al malparido ese a tu maldita madre la van a sacar pero del estudio de porno mamagueo"));
                            habbo.getClient().sendResponse(new BubbleAlertComposer("Bubble_Furni_Received", keys));
                            break;
                        }
                        System.out.println("Item ID Invalida");
                        break;
                    }
                    default: {
                        habbo.whisper("Ingresaste una moneda no aceptada!", RoomChatMessageBubbles.WIRED);
                        return false;
                    }
                }
            }
            if (str.length >= 5) {
                int cantidad3 = Integer.parseInt(str[5]);
                xd = (String)xd + ";;" + str[4] + ";" + cantidad3;
                switch (str[4]) {
                    case "diamantes": 
                    case "diamonds": {
                        habbo.givePoints(101, cantidad3);
                        break;
                    }
                    case "exp": {
                        habbo.givePixels(cantidad3);
                        break;
                    }
                    case "credits": 
                    case "creditos": {
                        habbo.giveCredits(cantidad3);
                        break;
                    }
                    case "item": 
                    case "furni": {
                        if (!Emulator.isNumeric(str[5])) {
                            return false;
                        }
                        item = Emulator.getGameEnvironment().getItemManager().getItem(cantidad3);
                        if (item != null) {
                            newItem = Emulator.getGameEnvironment().getItemManager().createItem(habbo.getHabboInfo().getId(), item, 0, 0, "");
                            if (newItem == null) break;
                            habbo.getInventory().getItemsComponent().addItem(newItem);
                            habbo.getClient().sendResponse(new AddHabboItemComposer(newItem));
                            habbo.getClient().sendResponse(new InventoryRefreshComposer());
                            keys = new THashMap();
                            keys.put("display", "BUBBLE");
                            keys.put("image", "${image.library.url}/dcr/icons/" + item.getName().replaceAll("\\*", "_") + "_icon.png");
                            keys.put("message", Emulator.getTexts().getValue("commands.generic.furni.received", "Ni un brillo pelao e osito sacalo del clan al malparido ese a tu maldita madre la van a sacar pero del estudio de porno mamagueo"));
                            habbo.getClient().sendResponse(new BubbleAlertComposer("Bubble_Furni_Received", keys));
                            break;
                        }
                        System.out.println("Item ID Invalida");
                        break;
                    }
                    default: {
                        habbo.whisper("Ingresaste una moneda no aceptada!", RoomChatMessageBubbles.WIRED);
                        return false;
                    }
                }
            }
            WAutoPagaManager.getInstance().newUser(habbo.getHabboInfo().getId(), Emulator.getIntUnixTimestamp() + 3600, true, this.getRoomId(), this.getId(), (String)xd);
            habbo.whisper("Felicidades, has recibido un premio especial por ganar", RoomChatMessageBubbles.WIRED);
        }
        return true;
    }

    @Override
    public void onPickUp() {
        this.message = "";
    }

    @Override
    public boolean saveData(WiredSettings settings, GameClient gameClient) throws WiredSaveException {
        String message = settings.getStringParam();
        int delay = settings.getDelay();
        if (gameClient.getHabbo() != null && gameClient.getHabbo().getHabboInfo().getRank().getId() <= 16) {
            throw new WiredSaveException("");
        }
        if (delay > Emulator.getConfig().getInt("hotel.wired.max_delay", 20)) {
            throw new WiredSaveException("Delay too long");
        }
        this.message = message;
        this.setDelay(delay);
        return true;
    }
}

