/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.items.interactions.games.battlebanzai;

import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.games.GameTeam;
import com.eu.habbo.habbohotel.games.battlebanzai.BattleBanzaiGame;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionPushable;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomTileState;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.rooms.RoomUserRotation;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionBattleBanzaiPuck
extends InteractionPushable {
    public InteractionBattleBanzaiPuck(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public InteractionBattleBanzaiPuck(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public int getWalkOnVelocity(RoomUnit roomUnit, Room room) {
        return 6;
    }

    @Override
    public RoomUserRotation getWalkOnDirection(RoomUnit roomUnit, Room room) {
        return roomUnit.getBodyRotation();
    }

    @Override
    public int getWalkOffVelocity(RoomUnit roomUnit, Room room) {
        return 0;
    }

    @Override
    public RoomUserRotation getWalkOffDirection(RoomUnit roomUnit, Room room) {
        return roomUnit.getBodyRotation();
    }

    @Override
    public int getDragVelocity(RoomUnit roomUnit, Room room) {
        return 1;
    }

    @Override
    public RoomUserRotation getDragDirection(RoomUnit roomUnit, Room room) {
        return roomUnit.getBodyRotation();
    }

    @Override
    public int getTackleVelocity(RoomUnit roomUnit, Room room) {
        return 6;
    }

    @Override
    public RoomUserRotation getTackleDirection(RoomUnit roomUnit, Room room) {
        return roomUnit.getBodyRotation();
    }

    @Override
    public int getNextRollDelay(int currentStep, int totalSteps) {
        int n;
        int t = 2500;
        if (totalSteps == 1) {
            n = 500;
        } else {
            t = t / t - 1;
            n = 100 * (t * t * t * t * t + 1) + currentStep * 100;
        }
        return n;
    }

    @Override
    public RoomUserRotation getBounceDirection(Room room, RoomUserRotation currentDirection) {
        switch (currentDirection) {
            default: {
                return RoomUserRotation.SOUTH;
            }
            case NORTH_EAST: {
                if (this.validMove(room, room.getLayout().getTile(this.getX(), this.getY()), room.getLayout().getTileInFront(room.getLayout().getTile(this.getX(), this.getY()), RoomUserRotation.NORTH_WEST.getValue()))) {
                    return RoomUserRotation.NORTH_WEST;
                }
                if (this.validMove(room, room.getLayout().getTile(this.getX(), this.getY()), room.getLayout().getTileInFront(room.getLayout().getTile(this.getX(), this.getY()), RoomUserRotation.SOUTH_EAST.getValue()))) {
                    return RoomUserRotation.SOUTH_EAST;
                }
                return RoomUserRotation.SOUTH_WEST;
            }
            case EAST: {
                return RoomUserRotation.WEST;
            }
            case SOUTH_EAST: {
                if (this.validMove(room, room.getLayout().getTile(this.getX(), this.getY()), room.getLayout().getTileInFront(room.getLayout().getTile(this.getX(), this.getY()), RoomUserRotation.SOUTH_WEST.getValue()))) {
                    return RoomUserRotation.SOUTH_WEST;
                }
                if (this.validMove(room, room.getLayout().getTile(this.getX(), this.getY()), room.getLayout().getTileInFront(room.getLayout().getTile(this.getX(), this.getY()), RoomUserRotation.NORTH_EAST.getValue()))) {
                    return RoomUserRotation.NORTH_EAST;
                }
                return RoomUserRotation.NORTH_WEST;
            }
            case SOUTH: {
                return RoomUserRotation.NORTH;
            }
            case SOUTH_WEST: {
                if (this.validMove(room, room.getLayout().getTile(this.getX(), this.getY()), room.getLayout().getTileInFront(room.getLayout().getTile(this.getX(), this.getY()), RoomUserRotation.SOUTH_EAST.getValue()))) {
                    return RoomUserRotation.SOUTH_EAST;
                }
                if (this.validMove(room, room.getLayout().getTile(this.getX(), this.getY()), room.getLayout().getTileInFront(room.getLayout().getTile(this.getX(), this.getY()), RoomUserRotation.NORTH_WEST.getValue()))) {
                    return RoomUserRotation.NORTH_WEST;
                }
                return RoomUserRotation.NORTH_EAST;
            }
            case WEST: {
                return RoomUserRotation.EAST;
            }
            case NORTH_WEST: 
        }
        if (this.validMove(room, room.getLayout().getTile(this.getX(), this.getY()), room.getLayout().getTileInFront(room.getLayout().getTile(this.getX(), this.getY()), RoomUserRotation.NORTH_EAST.getValue()))) {
            return RoomUserRotation.NORTH_EAST;
        }
        if (this.validMove(room, room.getLayout().getTile(this.getX(), this.getY()), room.getLayout().getTileInFront(room.getLayout().getTile(this.getX(), this.getY()), RoomUserRotation.SOUTH_WEST.getValue()))) {
            return RoomUserRotation.SOUTH_WEST;
        }
        return RoomUserRotation.SOUTH_EAST;
    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) throws Exception {
        super.onClick(client, room, objects);
    }

    @Override
    public boolean validMove(Room room, RoomTile from, RoomTile to) {
        if (to == null) {
            return false;
        }
        HabboItem topItem = room.getTopItemAt(to.x, to.y, this);
        return room.getLayout().tileWalkable(to.x, to.y) && (topItem == null || topItem.getBaseItem().allowStack() && !topItem.getBaseItem().allowSit() && !topItem.getBaseItem().allowLay());
    }

    @Override
    public void onDrag(Room room, RoomUnit roomUnit, int velocity, RoomUserRotation direction) {
    }

    @Override
    public void onKick(Room room, RoomUnit roomUnit, int velocity, RoomUserRotation direction) {
    }

    @Override
    public void onTackle(Room room, RoomUnit roomUnit, int velocity, RoomUserRotation direction) {
    }

    @Override
    public void onMove(Room room, RoomTile from, RoomTile to, RoomUserRotation direction, RoomUnit kicker, int nextRoll, int currentStep, int totalSteps) {
        GameTeam team;
        BattleBanzaiGame game;
        Habbo habbo = room.getHabbo(kicker);
        if (habbo != null && (game = (BattleBanzaiGame)room.getGame(BattleBanzaiGame.class)) != null && (team = game.getTeamForHabbo(habbo)) != null) {
            HabboItem item = room.getTopItemAt(to.x, to.y);
            try {
                item.onWalkOn(kicker, room, null);
            }
            catch (Exception e) {
                return;
            }
            this.setExtradata("" + team.teamColor.type);
            room.updateItemState(this);
        }
    }

    @Override
    public void onBounce(Room room, RoomUserRotation oldDirection, RoomUserRotation newDirection, RoomUnit kicker) {
    }

    @Override
    public void onStop(Room room, RoomUnit kicker, int currentStep, int totalSteps) {
    }

    @Override
    public boolean canStillMove(Room room, RoomTile from, RoomTile to, RoomUserRotation direction, RoomUnit kicker, int nextRoll, int currentStep, int totalSteps) {
        return to.state == RoomTileState.OPEN && to.isWalkable();
    }
}

