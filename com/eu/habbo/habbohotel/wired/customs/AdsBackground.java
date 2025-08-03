/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.wired.customs;

import com.eu.habbo.Emulator;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdsBackground {
    public static void adsUpdate(int item, String extraData) {
        try (Connection connect = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement pS1 = connect.prepareStatement("UPDATE items SET wired_data = ? WHERE id = ?");){
            pS1.setString(1, extraData);
            pS1.setInt(2, item);
            pS1.execute();
        }
        catch (SQLException sQLException) {
            // empty catch block
        }
    }

    public static String adsObtener(int item) {
        String dato = "";
        try (Connection connect = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement pS1 = connect.prepareStatement("SELECT wired_data FROM items WHERE id = ? LIMIT 1");){
            pS1.setInt(1, item);
            ResultSet rs1 = pS1.executeQuery();
            rs1.next();
            dato = rs1.getString("wired_data");
            rs1.close();
        }
        catch (SQLException sQLException) {
            // empty catch block
        }
        return dato;
    }

    public static void adsUpdateWired(int item, String extraData) {
        try (Connection connect = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement pS1 = connect.prepareStatement("UPDATE items SET extra_data2 = ? WHERE id = ?");){
            pS1.setString(1, extraData);
            pS1.setInt(2, item);
            pS1.execute();
        }
        catch (SQLException sQLException) {
            // empty catch block
        }
    }

    public static String adsObtenerWired(int item) {
        String dato = "";
        try (Connection connect = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement pS1 = connect.prepareStatement("SELECT extra_data2 FROM items WHERE id = ? LIMIT 1");){
            pS1.setInt(1, item);
            ResultSet rs1 = pS1.executeQuery();
            rs1.next();
            dato = rs1.getString("extra_data2");
            rs1.close();
        }
        catch (SQLException sQLException) {
            // empty catch block
        }
        return dato;
    }
}

