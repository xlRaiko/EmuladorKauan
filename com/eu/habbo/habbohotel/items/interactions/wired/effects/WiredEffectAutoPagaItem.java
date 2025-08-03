/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.wired.WiredSettings;
import com.eu.habbo.habbohotel.items.interactions.wired.effects.WiredEffectWhisper;
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

public class WiredEffectAutoPagaItem
extends WiredEffectWhisper {
    public WiredEffectAutoPagaItem(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        Habbo habbo = room.getHabbo(roomUnit);
        String[] str = this.message.split(":");
        String xd = "";
        if (habbo != null && str.length >= 1) {
            if (!Emulator.isNumeric(str[1])) {
                habbo.whisper("La id de item debe ser numerica", RoomChatMessageBubbles.WIRED);
                return false;
            }
            int cantidad = Integer.parseInt(str[1]);
            switch (str[0].toLowerCase()) {
                case "item": 
                case "furni": {
                    if (!Emulator.isNumeric(str[1])) {
                        return false;
                    }
                    Item item = Emulator.getGameEnvironment().getItemManager().getItem(cantidad);
                    if (item != null) {
                        HabboItem newItem = Emulator.getGameEnvironment().getItemManager().createItem(habbo.getHabboInfo().getId(), item, 0, 0, "");
                        if (newItem == null) break;
                        habbo.getInventory().getItemsComponent().addItem(newItem);
                        habbo.getClient().sendResponse(new AddHabboItemComposer(newItem));
                        habbo.getClient().sendResponse(new InventoryRefreshComposer());
                        THashMap<String, String> keys = new THashMap<String, String>();
                        keys.put("display", "BUBBLE");
                        keys.put("image", "${image.library.url}/dcr/icons/" + item.getName().replaceAll("\\*", "_") + "_icon.png");
                        keys.put("message", Emulator.getTexts().getValue("commands.generic.furni.received", "Ni un brillo pelao e osito sacalo del clan al malparido ese a tu maldita madre la van a sacar pero del estudio de porno mamagueo"));
                        habbo.getClient().sendResponse(new BubbleAlertComposer("Bubble_Furni_Received", keys));
                        break;
                    }
                    habbo.whisper("Item ID Invalida", RoomChatMessageBubbles.WIRED);
                    break;
                }
                default: {
                    habbo.whisper("No esta configurado correctamente. Ejemplo: item:id_item", RoomChatMessageBubbles.WIRED);
                    return false;
                }
            }
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

