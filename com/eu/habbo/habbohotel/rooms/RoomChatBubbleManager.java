/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.rooms;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RoomChatBubbleManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoomChatBubbleManager.class);

    public RoomChatBubbleManager() {
        this.reload();
    }

    public void reload() {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM chat_bubbles");
             ResultSet resultSet = statement.executeQuery();){
            while (resultSet.next()) {
                int type = resultSet.getInt("type");
                String name = resultSet.getString("name");
                String permission = resultSet.getString("permission");
                boolean overridable = resultSet.getBoolean("overridable");
                boolean triggersTalkingFurniture = resultSet.getBoolean("triggers_talking_furniture");
                RoomChatMessageBubbles.addDynamicBubble(type, name, permission, overridable, triggersTalkingFurniture);
            }
        }
        catch (SQLException e) {
            LOGGER.error("Failed to load chat bubbles from database.", e);
        }
    }
}

