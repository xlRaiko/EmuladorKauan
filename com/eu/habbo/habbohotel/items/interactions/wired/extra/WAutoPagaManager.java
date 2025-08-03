/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.items.interactions.wired.extra;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.interactions.wired.extra.WiredAutoPaga;
import io.netty.util.internal.ConcurrentSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class WAutoPagaManager {
    private static WAutoPagaManager wAutoPagaManager;
    private final ConcurrentSet<WiredAutoPaga> user = new ConcurrentSet();

    public WAutoPagaManager() {
        wAutoPagaManager = this;
    }

    public static WAutoPagaManager getInstance() {
        if (wAutoPagaManager == null) {
            wAutoPagaManager = new WAutoPagaManager();
        }
        return wAutoPagaManager;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void newUser(int id, long timestamp, boolean winer, int room, int item_id, String paga) {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO alfrevid_auto_paga (`item_id`, `room_id`, `user_id`, `paga`) VALUES (?, ?, ?, ?)");){
            statement.setInt(1, item_id);
            statement.setInt(2, room);
            statement.setInt(3, id);
            statement.setString(4, paga);
            statement.execute();
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        finally {
            this.user.add(new WiredAutoPaga(id, timestamp, winer, room));
        }
    }

    public boolean hasUserIsWinnerInRoom(int id, int room_id) {
        for (WiredAutoPaga wired : this.user) {
            if (wired.getUserID() != id || !wired.isWins() || wired.getRoom() != room_id) continue;
            return true;
        }
        return false;
    }

    public void clearUsers() {
        this.user.removeIf(wiredAutoPaga -> (long)Emulator.getIntUnixTimestamp() >= wiredAutoPaga.getTimestamp());
    }
}

